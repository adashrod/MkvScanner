package com.adashrod.mkvscanner;

import com.adashrod.mkvscanner.model.Video;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * A wrapper API for external programs that can be used to scan and demux Blu-ray disc directories, their titles, and
 * video container files (mkv, m2ts, etc). exec() is a low-level function that gives direct access to the CLI of the
 * mkvscanner executable and the other functions in this API should be preferred.
 * todo: provide a demuxBluRayTitle (all tracks) function?
 */
public interface FileScanner {
    /**
     * Runs the mkvscanner executable with the supplied arguments and returns the stdout
     * @param file the target file to run the mkvscanner on
     * @return string output of the executable: stdout for successful exits and stderr for errors
     * @throws NotBluRayDirectoryException scanned a directory that wasn't a BD dir
     * @throws CorruptBluRayStructureException BD dir can be scanned, but the titles can't
     * @throws UnreadableFileException trying to scan a file that isn't a video container (non-mkv/m2ts)
     * @throws FormatConversionException happens when trying to e.g. demux an AC3 track to .sup
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    String exec(File file, String... arguments) throws DemuxerException, IOException;

    /**
     * Scans and parses a file, returning an object representing the metadata of that video.
     * @param file the target file to run the mkvscanner on
     * @return an object representing the video and track data of the given file
     * @throws UnreadableFileException trying to scan a file that isn't a video container (non-mkv/m2ts)
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    Video scanAndParseFile(File file) throws DemuxerException, IOException;

    /**
     * Runs the mkvscanner on a BD dir and returns a set of title numbers found on the disc. This doesn't parse the individual
     * titles, see {@link FileScanner#scanAndParseBluRayTitle(java.io.File, int)} to do that.
     * @param bluRayDirectory a BD dir to scan
     * @return a set of title numbers of the blu-ray that can be individually scanned
     * @throws NotBluRayDirectoryException scanned a directory that wasn't a BD dir
     * @throws CorruptBluRayStructureException BD dir can be scanned, but the titles can't
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    Set<Integer> scanBluRayDir(File bluRayDirectory) throws DemuxerException, IOException;

    /**
     * Scans and parses an individual title on a BD dir, returning an object representing the metadata of the title.
     * @param bluRayDirectory the target BD dir
     * @param title           which title on the BD structure to scan
     * @return an object representing the video and track data of the given blu-ray title
     * @throws NotBluRayDirectoryException scanned a directory that wasn't a BD dir
     * @throws CorruptBluRayStructureException BD dir can be scanned, but the titles can't
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    Video scanAndParseBluRayTitle(File bluRayDirectory, int title) throws DemuxerException, IOException;

    /**
     * Runs the mkvscanner on a specific title on a BD dir to demux specific tracks. Tracks are included in demuxing if
     * their track number is included in tracksToInclude
     * @param bluRayDirectory the target BD dir
     * @param title           which title on the BD structure to scan
     * @param tracksToInclude which track numbers of the title to include in the demuxing
     * @return output filenames created by the demuxing (just the names, not the fully qualified absolute names)
     * @throws NotBluRayDirectoryException scanned a directory that wasn't a BD dir
     * @throws CorruptBluRayStructureException BD dir can be scanned, but the titles can't
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    Collection<String> demuxBluRayTitleByTracks(File bluRayDirectory, int title, Collection<Integer> tracksToInclude) throws DemuxerException, IOException;

    /**
     * Runs the mkvscanner on a specific title on a BD dir to demux specific tracks. Tracks are included in demuxing if
     * their language is included in languagesToInclude
     * @param bluRayDirectory    the target BD dir
     * @param title              which title on the BD structure to scan
     * @param languagesToInclude tracks are included in demuxing if their language is included here
     * @return output filenames created by the demuxing (just the names, not the fully qualified absolute names)
     */
    Collection<String> demuxBluRayTitleByLanguages(File bluRayDirectory, int title, Collection<String> languagesToInclude) throws DemuxerException, IOException;

    /**
     * Runs the mkvscanner on a file, demuxing specific tracks. Tracks are included in demuxing if their track number is
     * included in tracksToInclude
     * @param file            the target file to run the mkvscanner on
     * @param tracksToInclude which track numbers of the title to include in the demuxing
     * @return output filenames created by the demuxing (just the names, not the fully qualified absolute names)
     * @throws UnreadableFileException trying to scan a file that isn't a video container (non-mkv/m2ts)
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    Collection<String> demuxFileByTracks(File file, Collection<Integer> tracksToInclude) throws DemuxerException, IOException;

    /**
     * Runs the mkvscanner on a file, demuxing specific tracks.  Tracks are included in demuxing if their language is
     * included in languagesToInclude
     * @param file               the target file to run the mkvscanner on
     * @param languagesToInclude tracks are included in demuxing if their language is included here
     * @return output filenames created by the demuxing (just the names, not the fully qualified absolute names)
     * @throws UnreadableFileException trying to scan a file that isn't a video container (non-mkv/m2ts)
     * @throws DemuxerException error output from mkvscanner couldn't be parsed
     */
    Collection<String> demuxFileByLanguages(File file, Collection<String> languagesToInclude) throws DemuxerException, IOException;
}
