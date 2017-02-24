package com.adashrod.mkvscanner;

import com.adashrod.mkvscanner.model.Format;
import com.adashrod.mkvscanner.model.Track;
import com.adashrod.mkvscanner.model.Video;
import com.adashrod.mkvscanner.util.FormatExtensionConfig;
import com.adashrod.mkvscanner.util.StreamConsumer;
import com.adashrod.mkvscanner.util.StringLineIterator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of FileScanner that uses eac3to for scanning and demuxing.
 */
public class Eac3toScanner implements FileScanner {
    private final Logger logger = Logger.getLogger(Eac3toScanner.class);

    private final Pattern videoIndicator = Pattern.compile("\\d+[xip](\\d+)?[xip]?.*"),
        audioIndicator = Pattern.compile("\\d+(\\.|/)\\d+\\s*(\\(strange setup\\))?\\s*channels"),
        subtitlesIndicator = Pattern.compile("Subtitle\\s*\\(([^)]+)\\).*"),
        chaptersIndicator = Pattern.compile("Chapters.*");

    private final Pattern singleTitleFirstLine = Pattern.compile("\\w+,\\s*[\\w\\d\\s,]+\\d+(:\\d+)*.*");

    private final String executableLocation;
    private final File outputDirectory;
    private final Collection<String> eac3toLanguages = new HashSet<>();
    private final Map<String, FormatExtensionConfig> formatExtensionConfigs = new HashMap<>();

    public Eac3toScanner(final String executableLocation, final File outputDirectory) {
        this.executableLocation = Objects.requireNonNull(executableLocation, "Eac3toScanner.executableLocation can't be null");
        this.outputDirectory = Objects.requireNonNull(outputDirectory, "Eac3toScanner.outputDirectory can't be null");
        try {
            loadEac3toLanguages();
            loadEac3toFormatExtensions();
        } catch (final IOException ioe) {
            logger.fatal("Config error: " + ioe.getMessage());
            throw new RuntimeException("Config error: " + ioe.getMessage());
        }
    }

    private void loadEac3toLanguages() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream("/eac3to-languages.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                eac3toLanguages.add(line);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadEac3toFormatExtensions() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream("/eac3to-format-extensions.json");
        final ObjectMapper jsonMapper = new ObjectMapper();
        final Map<String, Object> json = jsonMapper.readValue(inputStream, new TypeReference<Map<String, Object>>(){});

        for (final Map.Entry<String, Object> entry: json.entrySet()) {
            final FormatExtensionConfig formatExtensionConfig = new FormatExtensionConfig(entry.getKey());
            if (entry.getValue() instanceof String) {
                formatExtensionConfig.getConfigs().add(new FormatExtensionConfig.ExtensionConfig(entry.getValue().toString()));
            } else if (entry.getValue() instanceof List) {
                ((List) entry.getValue()).stream().forEach((final Object o) -> {
                    final Map<String, Object> c = (Map) o;
                    formatExtensionConfig.getConfigs().add(new FormatExtensionConfig.ExtensionConfig(c.get("extension").toString(), (List) c.get("flags")));
                });
            }
            formatExtensionConfigs.put(formatExtensionConfig.getFormat(), formatExtensionConfig);
        }
    }

    public String exec(final File file, final String... arguments) throws DemuxerException, IOException {
        final List<String> command = new ArrayList<>();
        command.add(executableLocation);
        command.add(file.getPath());
        Collections.addAll(command, arguments);
        final ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        final int exitValue;
        final StreamConsumer stdOut;
        final StreamConsumer stdErr;
        try {
            process = builder.start();
            stdOut = new StreamConsumer(process.getInputStream());
            stdErr = new StreamConsumer(process.getErrorStream());
            stdOut.start();
            stdErr.start();
            exitValue = process.waitFor();
        } catch (final InterruptedException ie) {
            process.destroy();
            logger.error(ie);// todo: propagate a wrapper exception or re-throw; if not re-throwing, re-interrupt
            return null;
        }

        // when piping eac3to output to a file or stream, each line is prepended by a bunch of backspace characters: \b
        // also: the first line has the eac3to progress bar "----" and some whitespace
        final String output = stdOut.getStreamContent().replaceAll("\b", "").replaceAll("^-+\\s+", "").trim();

        if (exitValue == 0) {
            return output;
        } else {
            final StringBuilder argumentsBuilder = new StringBuilder(file.getPath()).append(" ");
            for (final String s: arguments) { argumentsBuilder.append(s).append(" "); }
            if (output.contains("The format of the source file could not be detected.")) {
                // happens when a BD dir can be scanned, but the titles can't
                throw new CorruptBluRayStructureException(file.getName(), argumentsBuilder.toString().trim(), output);
            }
            if (output.startsWith("Error reading file ")) {
                // happens when trying to scan a file that isn't a video container (non-mkv/m2ts)
                throw new UnreadableFileException(file.getName(), argumentsBuilder.toString().trim(), output);
            }
            if (output.matches("This (subtitle|audio|video) conversion is not supported.")) {
                // happens when trying to e.g. demux an AC3 track to .sup
                throw new FormatConversionException(file.getName(), argumentsBuilder.toString().trim(), output);
            }
            if (output.contains("HD DVD / Blu-Ray disc structure not found.")) {
                throw new NotBluRayDirectoryException(file.getName(), argumentsBuilder.toString().trim(), output);
            }
            // todo: new exception type? file is partially copied; message = 'The source file "d:\videos\LICENCE_TO_KILL\BDMV\STREAM\00057.m2ts" could not be opened.'
            // generic/unknown:
            throw new DemuxerException(file.getName(), argumentsBuilder.toString().trim(), output);
        }
    }

    @Override
    public Video scanAndParseFile(final File file) throws DemuxerException, IOException {
        ensureDirectoryStatus(false, file);
        final String scanOutput = exec(file);
        try {
            return parseSingleTitleOrFile(scanOutput);
        } catch (final ParseException pe) {
            handleParseException(pe, file.getName(), scanOutput);
            return null;
        }
    }

    @Override
    public Set<Integer> scanBluRayDir(final File bluRayDirectory) throws DemuxerException, IOException {
        ensureDirectoryStatus(true, bluRayDirectory);
        return parseBluRayDisc(exec(bluRayDirectory));
    }

    @Override
    public Video scanAndParseBluRayTitle(final File bluRayDirectory, final int title) throws DemuxerException, IOException {
        ensureDirectoryStatus(true, bluRayDirectory);
        final String scanOutput = exec(bluRayDirectory, title + ")");
        try {
            return parseSingleTitleOrFile(scanOutput);
        } catch (final ParseException pe) {
            handleParseException(pe, bluRayDirectory.getName(), scanOutput);
            return null;
        }
    }

    @Override
    public Collection<String> demuxBluRayTitleByTracks(final File bluRayDirectory, final int title, final Collection<Integer> tracksToInclude) throws DemuxerException, IOException {
        ensureDirectoryStatus(true, bluRayDirectory);
        return demuxHelper(bluRayDirectory, title, tracksToInclude, null);
    }

    @Override
    public Collection<String> demuxBluRayTitleByLanguages(final File bluRayDirectory, final int title, final Collection<String> languagesToInclude) throws DemuxerException, IOException {
        ensureDirectoryStatus(true, bluRayDirectory);
        return demuxHelper(bluRayDirectory, title, null, languagesToInclude);
    }

    @Override
    public Collection<String> demuxFileByTracks(final File file, final Collection<Integer> tracksToInclude) throws DemuxerException, IOException {
        ensureDirectoryStatus(false, file);
        return demuxHelper(file, null, tracksToInclude, null);
    }

    @Override
    public Collection<String> demuxFileByLanguages(final File file, final Collection<String> languagesToInclude) throws DemuxerException, IOException {
        ensureDirectoryStatus(false, file);
        return demuxHelper(file, null, null, languagesToInclude);
    }

    private Video parseSingleTitleOrFile(final String content) throws ParseException {
        final Video video = new Video();
        Track currentTrack = null;
        final Iterable<String> iterator = new StringLineIterator(content);
        for (final String line: iterator) {
            final String trimmedLine = line.trim();
            if (singleTitleFirstLine.matcher(trimmedLine).matches() || trimmedLine.isEmpty()) {
                continue;
            }
            if (Character.isDigit(trimmedLine.charAt(0))) { // start of new track
                if (currentTrack != null) { // add last track
                    video.addTrack(currentTrack);
                }
                try {
                    currentTrack = parseTrackLine(trimmedLine);
                } catch (final FormatTypeParseException e) {
                    final int i = content.indexOf(e.getFormatTypeToken());
                    throw new ParseException(String.format("Couldn't determine format type (audio/video/etc) in line: %s", trimmedLine), i);
                }
            } else {
                // when the entire line is just the track name surrounded by quotes (video and audio tracks)
                if (trimmedLine.startsWith("\"") && trimmedLine.endsWith("\"") && currentTrack != null) {
                    currentTrack.setName(trimmedLine.substring(1, trimmedLine.length() - 1));
                }
            }
        }
        if (currentTrack != null) {
            video.addTrack(currentTrack);
        }
        return video;
    }

    private Track parseTrackLine(final String trackLine) throws FormatTypeParseException {
        int cursor;
        final List<String> tokens = new ArrayList<>();
        final int colon = trackLine.indexOf(':');
        if (colon != -1) {
            cursor = colon + 1;
            tokens.add(trackLine.substring(0, colon));
        } else {
            cursor = 0;
        }
        int start = cursor;
        Character c;
        // iterate over string parsing out tokens separated by commas and adding them to a list
        do {
            c = cursor < trackLine.length() ? trackLine.charAt(cursor) : null;
            final String token;
            if (c == null) {
                // cursor is at end-of-string: this is the last token
                final String t = trackLine.substring(start).trim();
                if (!t.isEmpty()) {
                    tokens.add(t);
                }
                cursor = trackLine.length();
                start = cursor;
            } else if (c == '"') {
                // first quote: start of name as last token in track line
                final int lastQuote = trackLine.lastIndexOf('"');
                token = trackLine.substring(cursor, lastQuote + 1);
                tokens.add(token);
                cursor = lastQuote + 1;
                start = cursor;
            } else if (c == ',') {
                token = trackLine.substring(start, cursor).trim();
                tokens.add(token);
                cursor++;
                start = cursor;
            } else {
                cursor++;
            }
        } while (c != null);

        return turnTokensIntoTrack(tokens);
    }

    private Set<Integer> parseBluRayDisc(final String content) {
        final Iterator<String> iterator = new StringLineIterator(content);
        final Set<Integer> titleNumbers = new HashSet<>();
        final Pattern titleHeaderPattern = Pattern.compile("(\\d+)\\)\\s.+$");
        while (iterator.hasNext()) {
            final String line = iterator.next();
            final Matcher matcher = titleHeaderPattern.matcher(line);
            if (matcher.matches()) {
                titleNumbers.add(Integer.parseInt(matcher.group(1)));
            }
        }
        return titleNumbers;
    }

    private Track turnTokensIntoTrack(final List<String> tokens) throws FormatTypeParseException {
        final Track track = new Track();
        final List<String> tokensCopy = new ArrayList<>(tokens);
        int badFormatTypeIndex = 0; // if the formatType can't be parsed, this will be the index of the first formatType token
        try {
            track.setNumber(Integer.parseInt(tokensCopy.get(0)));
            tokensCopy.remove(0);
            badFormatTypeIndex++;
        } catch (final NumberFormatException ignored) {}

        boolean formatFound = false, formatTypeFound = false, languageFound = false;
        String formatString = null;
        for (final String token: tokensCopy) {
            if (token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"') {
                track.setName(token.substring(1, token.length() - 1));
                continue;
            }
            if (!formatFound) {
                // this branch rests on the assumption that the first token after the number is the format name since:
                // a: so far, that's always been true
                // b: there's no way to determine if a string is a format name or extra format metadata without
                //    accounting for all of the different metadata string types (48kHz, 448kbps, etc)
                formatFound = true;
                if (token.equals("Chapters")) {
                    final Format matroskaChaptersFormat = new Format();
                    matroskaChaptersFormat.setFormatType("Chapters");
                    matroskaChaptersFormat.setName("Matroska");
                    track.setFormat(matroskaChaptersFormat);
                } else {
                    if (token.startsWith("Subtitle")) {
                        final String subtitleFormatName = parseSubtitleFormat(token); // PGS, VobSub, etc
                        final Format subtitleFormat = new Format();
                        subtitleFormat.setName(subtitleFormatName);
                        subtitleFormat.setFormatType("Subtitles");
                        track.setFormat(subtitleFormat);
                    } else {
                        // save for later, don't set track.format until we also have the formatType
                        formatString = token;
                    }
                }
                badFormatTypeIndex++;
                continue;
            }
            if (!formatTypeFound) {
                final String formatType = tryToParseFormatTypeIndicator(token);
                if (formatType != null) {
                    final Format format = new Format();
                    format.setName(formatString);
                    format.setFormatType(formatType);
                    track.setFormat(format);
                    formatTypeFound = true;
                    continue;
                }
            }
            if (!languageFound) {
                if (eac3toLanguages.contains(token)) {
                    track.setLanguage(token);
                    languageFound = true;
                    badFormatTypeIndex++;
                }
            }
        }
        if (!languageFound) {
            track.setLanguage("Undetermined");
        }
        if (track.getFormat() == null) {
            throw new FormatTypeParseException(null, tokens.get(badFormatTypeIndex));
        }
        return track;
    }

    private String parseSubtitleFormat(final CharSequence subtitleToken) {
        final Pattern subtitleTokenFormat = Pattern.compile("Subtitle\\s*\\(([^)]+)\\).*");
        final Matcher matcher = subtitleTokenFormat.matcher(subtitleToken);
        return matcher.matches() ? matcher.group(1) : null;
    }

    private String tryToParseFormatTypeIndicator(final CharSequence token) {
        final Map<Pattern, String> cachedFormatTypesByPattern = new HashMap<>();
        cachedFormatTypesByPattern.put(videoIndicator, "Video");
        cachedFormatTypesByPattern.put(audioIndicator, "Audio");
        cachedFormatTypesByPattern.put(subtitlesIndicator, "Subtitles");
        cachedFormatTypesByPattern.put(chaptersIndicator, "Chapters");
        for (final Pattern p: new Pattern[]{videoIndicator, audioIndicator, subtitlesIndicator, chaptersIndicator}) {
            final Matcher m = p.matcher(token);
            if (m.matches()) {
                return cachedFormatTypesByPattern.get(p);
            }
        }
        return null;
    }

    private Collection<String> demuxHelper(final File fileToDemux, final Integer title, final Collection<Integer> tracksToInclude,
            final Collection<String> languagesToInclude) throws DemuxerException, IOException {
        if (!(tracksToInclude != null ^ languagesToInclude != null)) {
            throw new IllegalArgumentException("demuxHelper must be called with exactly one of: tracksToInclude, languagesToInclude");
        }
        final String scanOutput;
        final Collection<String> demuxArguments = new ArrayList<>();
        if (title == null) {
            scanOutput = exec(fileToDemux);
        } else {
            scanOutput = exec(fileToDemux, title + ")");
            demuxArguments.add(title + ")");
        }
        final Video videoTitle;
        try {
            videoTitle = parseSingleTitleOrFile(scanOutput);
        } catch (final ParseException pe) {
            handleParseException(pe, fileToDemux.getName(), scanOutput);
            return null;
        }
        final Collection<String> filenames = new HashSet<>();
        videoTitle.getTracks().stream().filter((final Track track) -> {
            return (tracksToInclude != null && tracksToInclude.contains(track.getNumber())) ||
                (languagesToInclude != null && languagesToInclude.contains(track.getLanguage()));
        }).forEach((final Track track) -> {
            FormatExtensionConfig formatExtensionConfig = formatExtensionConfigs.get(track.getFormat().getName());
            if (formatExtensionConfig == null) {
                // for the unusual case of chapters being labeled "Chapters" by eac3to
                formatExtensionConfig = formatExtensionConfigs.get(track.getFormat().getFormatType());
            }
            if (formatExtensionConfig == null) {
                // skip track if format is not supported (VobSub)
                logger.warn("unable to find extension config for " + track.getFormat());
                return;
            }

            for (final FormatExtensionConfig.ExtensionConfig ec : formatExtensionConfig.getConfigs()) {
                demuxArguments.add(track.getNumber() + ":");

                final int lastDot = fileToDemux.getName().lastIndexOf('.');
                final String outputBase = lastDot != -1 ? fileToDemux.getName().substring(0, lastDot) : fileToDemux.getName();
                final StringBuilder outputFilenameBuilder = new StringBuilder(outputDirectory.getAbsolutePath())
                    .append(File.separatorChar)
                    .append(outputBase);
                // titlePiece will be empty when demuxing a file
                if (title != null) {
                    outputFilenameBuilder.append("_ti").append(title);
                }
                outputFilenameBuilder.append("_tr").append(track.getNumber()).append("_").append(track.getLanguage());
                if (!ec.getFlags().isEmpty()) {
                    // identifier for differentiating between multiple demuxed instances of a single track
                    outputFilenameBuilder.append("_").append(ec.getFlags().stream().reduce((final String s1, final String s2) -> {
                        return s1 + s2;
                    }).get().replaceAll("[\\-_\\s]*", ""));
                }
                outputFilenameBuilder.append(".").append(ec.getExtension());
                final File outputFile = new File(outputFilenameBuilder.toString());
                demuxArguments.add(outputFile.getAbsolutePath());
                filenames.add(outputFile.getName());
                demuxArguments.addAll(ec.getFlags());
            }
        });
        final String[] demuxArgumentsArray = new String[demuxArguments.size()];
        demuxArguments.toArray(demuxArgumentsArray);
        exec(fileToDemux, demuxArgumentsArray);
        return filenames;
    }

    /**
     * The ParseException should only ever get thrown if there's a code problem in this class. This isn't something that
     * the user of this API should have to worry about, which is why it's being rethrown unchecked.
     * @param pe
     * @param filename
     * @param content
     */
    private void handleParseException(final ParseException pe, final String filename, final String content) {
        logger.fatal(String.format("failed to parse eac3to output: filename=%s, content=%s", filename, content), pe);
        throw new RuntimeException(String.format("Possible bug in MkvScanner. Failed to parse eac3to output. filename=%s, content=%s, %s at index %d",
            filename, content, pe.getMessage(), pe.getErrorOffset()));
    }

    private void ensureDirectoryStatus(final boolean shouldBeDirectory, final File fileOrDirectory) {
        if (shouldBeDirectory ^ fileOrDirectory.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s is %sa directory. It should %sbe.",
                fileOrDirectory.getName(), fileOrDirectory.isDirectory() ? "" : "not ", shouldBeDirectory ? "" : "not "));
        }
    }
}
