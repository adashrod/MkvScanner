package com.adashrod.mkvscanner;

import com.adashrod.mkvscanner.model.FormatType;
import com.adashrod.mkvscanner.model.Iso639Language;
import com.adashrod.mkvscanner.model.Track;
import com.adashrod.mkvscanner.model.Video;
import com.adashrod.mkvscanner.util.ProcessResult;
import com.adashrod.mkvscanner.util.ProcessRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Eac3toScannerTest {
    @Mock
    private ProcessRunner processRunner;

    // hard-coded stdout of running eac3to on a blu-ray dir
    private static final String BLU_RAY_DISC_LEGACY = String.join("\n",
        "1) 00000.mpls, 00000.m2ts, 2:28:52",
        "   - Chapters, 34 chapters",
        "   - VC-1, 1080p24 /1.001 (16:9)",
        "   - AC3, English, multi-channel, 48kHz",
        "   - RAW/PCM, English, multi-channel, 48kHz",
        "   - AC3, French, multi-channel, 48kHz",
        "   - AC3, Spanish, multi-channel, 48kHz",
        "   - AC3, German, multi-channel, 48kHz",
        "   - AC3, Italian, multi-channel, 48kHz",
        "   - AC3, Spanish, multi-channel, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "",
        "2) 00033.mpls, 00049.m2ts, 1:16:31",
        "   - Chapters, 15 chapters",
        "   - VC-1, 1080i60 /1.001 (16:9)",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "",
        "3) 00003.mpls, 00004.m2ts, 0:43:08",
        "   - MPEG2, 480i60 /1.001 (16:9)",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "",
        "4) 00006.mpls, 00007.m2ts, 0:23:11",
        "   - MPEG2, 480i60 /1.001 (4:3)",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "",
        "5) 00005.mpls, 00006.m2ts, 0:21:31",
        "   - MPEG2, 480i60 /1.001 (16:9)",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "",
        "6) 00004.mpls, 00005.m2ts, 0:21:25",
        "   - MPEG2, 480i60 /1.001 (16:9)",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "",
        "7) 00007.mpls, 00008.m2ts, 0:20:42",
        "   - MPEG2, 480i60 /1.001 (16:9)",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "   - AC3, English, stereo, 48kHz",
        "");

    // hard-coded stdout of running eac3to on a single blu-ray title
    private static final String BLU_RAY_TITLE_LEGACY = String.join("\n",
        "M2TS, 1 video track, 8 audio tracks, 18 subtitle tracks, 2:28:52, 24p /1.001",
        "1: Chapters, 34 chapters",
        "2: VC-1, 1080p24 /1.001 (16:9)",
        "3: AC3, English, 5.1 channels, 448kbps, 48kHz, dialnorm: -27dB",
        "4: RAW/PCM, English, 5.1 channels, 16 bits, 48kHz",
        "5: AC3, French, 5.1 channels, 448kbps, 48kHz, dialnorm: -27dB",
        "6: AC3, Spanish, 5.1 channels, 448kbps, 48kHz, dialnorm: -27dB",
        "7: AC3, German, 5.1 channels, 448kbps, 48kHz, dialnorm: -27dB",
        "8: AC3, Italian, 5.1 channels, 448kbps, 48kHz, dialnorm: -27dB",
        "9: AC3, Spanish, 5.1 channels, 448kbps, 48kHz, dialnorm: -27dB",
        "10: AC3 Surround, English, 2.0 channels, 192kbps, 48kHz, dialnorm: -27dB",
        "11: Subtitle (PGS), English",
        "12: Subtitle (PGS), English",
        "13: Subtitle (PGS), French",
        "14: Subtitle (PGS), German",
        "15: Subtitle (PGS), German",
        "16: Subtitle (PGS), Italian",
        "17: Subtitle (PGS), Italian",
        "18: Subtitle (PGS), Spanish",
        "19: Subtitle (PGS), Dutch",
        "20: Subtitle (PGS), Chinese",
        "21: Subtitle (PGS), Danish",
        "22: Subtitle (PGS), Finnish",
        "23: Subtitle (PGS), Korean",
        "24: Subtitle (PGS), Norwegian",
        "25: Subtitle (PGS), Portuguese",
        "26: Subtitle (PGS), Portuguese",
        "27: Subtitle (PGS), Spanish",
        "28: Subtitle (PGS), Swedish");

    // hard-coded stdout of running eac3to on an mkv file with a TrueHD track; has quoted track names, both on their
    // own line and inline
    private static final String MKV_FILE_TRUEHD_LEGACY = String.join("\n",
        "MKV, 1 video track, 3 audio tracks, 2 subtitle tracks, 2:12:06, 24p/1.001",
        "1: h264/AVC, English, 1080p24/1.001 (16:9)",
        "2: TrueHD, English, 7.1 channels, 48kHz, dialnorm: -31dB",
        "   \"TrueHD\"",
        "3: AC3, English, 5.1 channels, 640kbps, 48kHz, dialnorm: -31dB",
        "4: AC3, Spanish, 5.1 channels, 640kbps, 48kHz, dialnorm: -31dB",
        "   \"Spanish\"",
        "5: Subtitle (PGS), English",
        "6: Subtitle (PGS), Spanish, \"Spanish\"");

    // hard-coded stdout of running eac3to on an mkv file with a DTS-HD MA track; includes a parenthetical "(core: ...)"
    // metadata line that the parser must ignore
    private static final String MKV_FILE_DTSHD_LEGACY = String.join("\n",
        "MKV, 1 video track, 2 audio tracks, 2 subtitle tracks, 2:44:34, 24p/1.001",
        "1: h264/AVC, English, 1080p24/1.001 (16:9)",
        "2: DTS-HD Master Audio, English, 5.1 channels, 24 bits, 48kHz, dialnorm: 0dB",
        "   (core: DTS, 5.1 channels, 1509kbps, 48kHz, dialnorm: 0dB)",
        "   \"DTS-HD\"",
        "3: AC3, Spanish, 5.1 channels, 448kbps, 48kHz, dialnorm: -31dB",
        "   \"Spanish\"",
        "4: Subtitle (PGS), English",
        "5: Subtitle (PGS), Spanish, \"Spanish\"");

    // a track line whose format type (audio/video/subtitle/chapters) can't be determined: triggers the
    // FormatTypeParseException -> ParseException -> RuntimeException path in parseSingleTitleOrFile
    private static final String MALFORMED_TRACK_LEGACY = String.join("\n",
        "MKV, 1 video track, 0 audio tracks, 0 subtitle tracks, 1:00:00, 24p/1.001",
        "1: BogusFormat, gibberish, more gibberish");

    // VobSub has no entry in eac3to-format-extensions.json, so demuxing track 2 is skipped while track 1 (AC3) is not
    private static final String MKV_FILE_VOBSUB_LEGACY = String.join("\n",
        "MKV, 1 video track, 1 audio track, 1 subtitle track, 1:00:00, 24p/1.001",
        "1: AC3, English, 5.1 channels, 448kbps, 48kHz",
        "2: Subtitle (VobSub), English");

    // newer eac3to format: a blu-ray dir scan using bracketed ISO 639-2 codes and multi-segment m2ts
    private static final String BD_NEW = String.join("\n",
        "1) 00000.mpls, 00007.m2ts+00003.m2ts, 1:33:50",
        "   - Chapters, 2 chapters",
        "   - h264/AVC, 1080p24/1.001 (16:9)",
        "   - AC3, [eng], stereo, 48kHz",
        "   - Subtitle (PGS), [eng]",
        "   - Subtitle (PGS), [dan]",
        "   - Subtitle (PGS), [deu]",
        "   - Subtitle (PGS), [spa]",
        "   - Subtitle (PGS), [fra]",
        "   - Subtitle (PGS), [ita]",
        "   - Subtitle (PGS), [jpn]",
        "   - Subtitle (PGS), [nld]",
        "   - Subtitle (PGS), [nor]",
        "   - Subtitle (PGS), [fin]",
        "   - Subtitle (PGS), [swe]",
        "",
        "2) 00004.mpls, 00051.m2ts+00005.m2ts, 0:42:54",
        "   - Chapters, 8 chapters",
        "   - h264/AVC, 1080p24/1.001 (16:9)",
        "   - DTS Master Audio, [eng], multi-channel, 48kHz",
        "   - AC3, [deu], multi-channel, 48kHz",
        "   - AC3, [spa], stereo, 48kHz",
        "   - AC3, [fra], stereo, 48kHz",
        "   - AC3, [ita], multi-channel, 48kHz",
        "   - AC3, [jpn], stereo, 48kHz",
        "   - AC3, [eng], stereo, 48kHz",
        "   - Subtitle (PGS), [eng]",
        "   - Subtitle (PGS), [dan]",
        "   - Subtitle (PGS), [deu]",
        "   - Subtitle (PGS), [spa]",
        "   - Subtitle (PGS), [fra]",
        "   - Subtitle (PGS), [ita]",
        "   - Subtitle (PGS), [jpn]",
        "   - Subtitle (PGS), [nld]",
        "   - Subtitle (PGS), [nor]",
        "   - Subtitle (PGS), [fin]",
        "   - Subtitle (PGS), [swe]",
        "",
        "3) 00003.mpls, 00050.m2ts+00064.m2ts, 0:42:54",
        "   - Chapters, 8 chapters",
        "   - h264/AVC, 1080p24/1.001 (16:9)",
        "   - DTS Master Audio, [eng], multi-channel, 48kHz",
        "   - AC3, [deu], multi-channel, 48kHz",
        "   - AC3, [spa], stereo, 48kHz",
        "   - AC3, [fra], stereo, 48kHz",
        "   - AC3, [ita], multi-channel, 48kHz",
        "   - AC3, [jpn], stereo, 48kHz",
        "   - Subtitle (PGS), [eng]",
        "   - Subtitle (PGS), [dan]",
        "   - Subtitle (PGS), [deu]",
        "   - Subtitle (PGS), [spa]",
        "   - Subtitle (PGS), [fra]",
        "   - Subtitle (PGS), [ita]",
        "   - Subtitle (PGS), [jpn]",
        "   - Subtitle (PGS), [nld]",
        "   - Subtitle (PGS), [nor]",
        "   - Subtitle (PGS), [fin]",
        "   - Subtitle (PGS), [swe]",
        "",
        "4) 00001.mpls, 00057.m2ts+00064.m2ts, 0:42:53",
        "   - Chapters, 8 chapters",
        "   - h264/AVC, 1080p24/1.001 (16:9)",
        "   - DTS Master Audio, [eng], multi-channel, 48kHz",
        "   - AC3, [deu], multi-channel, 48kHz",
        "   - AC3, [spa], stereo, 48kHz",
        "   - AC3, [fra], stereo, 48kHz",
        "   - AC3, [ita], multi-channel, 48kHz",
        "   - AC3, [jpn], stereo, 48kHz",
        "   - Subtitle (PGS), [eng]",
        "   - Subtitle (PGS), [dan]",
        "   - Subtitle (PGS), [deu]",
        "   - Subtitle (PGS), [spa]",
        "   - Subtitle (PGS), [fra]",
        "   - Subtitle (PGS), [ita]",
        "   - Subtitle (PGS), [jpn]",
        "   - Subtitle (PGS), [nld]",
        "   - Subtitle (PGS), [nor]",
        "   - Subtitle (PGS), [fin]",
        "   - Subtitle (PGS), [swe]",
        "",
        "5) 00002.mpls, 00058.m2ts+00003.m2ts, 0:42:51",
        "   - Chapters, 8 chapters",
        "   - h264/AVC, 1080p24/1.001 (16:9)",
        "   - DTS Master Audio, [eng], multi-channel, 48kHz",
        "   - AC3, [deu], multi-channel, 48kHz",
        "   - AC3, [spa], stereo, 48kHz",
        "   - AC3, [fra], stereo, 48kHz",
        "   - AC3, [ita], multi-channel, 48kHz",
        "   - AC3, [jpn], stereo, 48kHz",
        "   - AC3, [eng], stereo, 48kHz",
        "   - Subtitle (PGS), [eng]",
        "   - Subtitle (PGS), [dan]",
        "   - Subtitle (PGS), [deu]",
        "   - Subtitle (PGS), [spa]",
        "   - Subtitle (PGS), [fra]",
        "   - Subtitle (PGS), [ita]",
        "   - Subtitle (PGS), [jpn]",
        "   - Subtitle (PGS), [nld]",
        "   - Subtitle (PGS), [nor]",
        "   - Subtitle (PGS), [fin]",
        "   - Subtitle (PGS), [swe]",
        "   - Subtitle (PGS), [jpn]",
        "",
        "6) 00014.mpls, 00054.m2ts+00065.m2ts, 0:19:12",
        "   - Chapters, 2 chapters",
        "   - MPEG2, 480i60/1.001 (4:3)",
        "   - AC3, [eng], stereo, 48kHz",
        "   - Subtitle (PGS), [eng]",
        "   - Subtitle (PGS), [dan]",
        "   - Subtitle (PGS), [deu]",
        "   - Subtitle (PGS), [spa]",
        "   - Subtitle (PGS), [fra]",
        "   - Subtitle (PGS), [ita]",
        "   - Subtitle (PGS), [jpn]",
        "   - Subtitle (PGS), [nld]",
        "   - Subtitle (PGS), [nor]",
        "   - Subtitle (PGS), [fin]",
        "   - Subtitle (PGS), [swe]");

    // newer eac3to format: a single blu-ray title scan with preamble lines and bracketed ISO 639-2 codes
    // (incl. /T codes deu/fra/nld);
    private static final String BD_TITLE_NEW = String.join("\n",
        "Fast mode enabled",
        "Keeping dialnorm",
        "analyze: 100%",
        "M2TS, 1 video track, 1 audio track, 11 subtitle tracks, 1:33:49",
        "1: Chapters, 2 chapters",
        "2: h264/AVC, 1080p24/1.001 (16:9), SDR",
        "3: AC3, [eng], 2.0 channels, 192kbps, 48kHz, dialnorm: -31dB",
        "4: Subtitle (PGS), [eng]",
        "5: Subtitle (PGS), [dan]",
        "6: Subtitle (PGS), [deu]",
        "7: Subtitle (PGS), [spa]",
        "8: Subtitle (PGS), [fra]",
        "9: Subtitle (PGS), [ita]",
        "10: Subtitle (PGS), [jpn]",
        "11: Subtitle (PGS), [nld]",
        "12: Subtitle (PGS), [nor]",
        "13: Subtitle (PGS), [fin]",
        "14: Subtitle (PGS), [swe]");

    // newer eac3to format: mkv file with a TrueHD track, bracketed codes, and quoted names
    private static final String MKV_FILE_TRUEHD_NEW = String.join("\n",
        "Fast mode enabled",
        "Keeping dialnorm",
        "analyze: 100%",
        "MKV, 1 video track, 3 audio tracks, 2 subtitle tracks, 2:12:06, 24p/1.001",
        "1: h264/AVC, [eng], 1080p24/1.001 (16:9), SDR",
        "2: TrueHD, [eng], 7.1 channels, 48kHz, dialnorm: -31dB",
        "   \"TrueHD\"",
        "3: AC3, [eng], 5.1 channels, 640kbps, 48kHz, dialnorm: -31dB",
        "4: AC3, [spa], 5.1 channels, 640kbps, 48kHz, dialnorm: -31dB",
        "   \"Spanish\"",
        "5: Subtitle (PGS), [eng]",
        "6: Subtitle (PGS), [spa], \"Spanish\"");

    // newer eac3to format: mkv file with a DTS-HD MA track, a "(core: ...)" line, bracketed codes
    private static final String MKV_FILE_DTSHD_NEW = String.join("\n",
        "Fast mode enabled",
        "Keeping dialnorm",
        "analyze: 100%",
        "MKV, 1 video track, 2 audio tracks, 2 subtitle tracks, 2:44:34, 24p/1.001",
        "1: h264/AVC, [eng], 1080p24/1.001 (16:9), SDR",
        "2: DTS-HD Master Audio, [eng], 5.1 channels, 24 bits, 48kHz, dialnorm: 0dB",
        "   (core: DTS, 5.1 channels, 1509kbps, 48kHz, dialnorm: 0dB)",
        "   \"DTS-HD\"",
        "3: AC3, [spa], 5.1 channels, 448kbps, 48kHz, dialnorm: -31dB",
        "   \"Spanish\"",
        "4: Subtitle (PGS), [eng]",
        "5: Subtitle (PGS), [spa], \"Spanish\"");

    @Test
    public void scanBluRayDir_returnsAllTitleNumbers(@TempDir final File bluRayDir) throws Exception {
        final FileScanner scanner = scannerReturning(0, BLU_RAY_DISC_LEGACY);
        final Set<Integer> titles = scanner.scanBluRayDir(bluRayDir);

        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)), titles);
    }

    @Test
    public void scanAndParseBluRayTitle_parsesEveryTrack(@TempDir final File bluRayDir) throws Exception {
        final FileScanner scanner = scannerReturning(0, BLU_RAY_TITLE_LEGACY);

        final Video video = scanner.scanAndParseBluRayTitle(bluRayDir, 1);
        final List<Track> tracks = video.getTracks();

        // the leading "M2TS, 1 video track, ..." summary line is not a track; the 28 numbered lines are
        assertEquals(28, tracks.size());
        assertTrack(tracks.get(0), 1, "OGM Chapters", FormatType.CHAPTERS, Iso639Language.UNDETERMINED);
        assertTrack(tracks.get(1), 2, "VC-1", FormatType.VIDEO, Iso639Language.UNDETERMINED);
        assertTrack(tracks.get(2), 3, "AC3", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(3), 4, "RAW/PCM", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(4), 5, "AC3", FormatType.AUDIO, Iso639Language.FRENCH);
        assertTrack(tracks.get(5), 6, "AC3", FormatType.AUDIO, Iso639Language.SPANISH);
        assertTrack(tracks.get(6), 7, "AC3", FormatType.AUDIO, Iso639Language.GERMAN);
        assertTrack(tracks.get(7), 8, "AC3", FormatType.AUDIO, Iso639Language.ITALIAN);
        assertTrack(tracks.get(8), 9, "AC3", FormatType.AUDIO, Iso639Language.SPANISH);
        assertTrack(tracks.get(9), 10, "AC3 Surround", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(10), 11, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(11), 12, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(12), 13, "PGS", FormatType.SUBTITLES, Iso639Language.FRENCH);
        assertTrack(tracks.get(13), 14, "PGS", FormatType.SUBTITLES, Iso639Language.GERMAN);
        assertTrack(tracks.get(14), 15, "PGS", FormatType.SUBTITLES, Iso639Language.GERMAN);
        assertTrack(tracks.get(15), 16, "PGS", FormatType.SUBTITLES, Iso639Language.ITALIAN);
        assertTrack(tracks.get(16), 17, "PGS", FormatType.SUBTITLES, Iso639Language.ITALIAN);
        assertTrack(tracks.get(17), 18, "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);
        assertTrack(tracks.get(18), 19, "PGS", FormatType.SUBTITLES, Iso639Language.DUTCH);
        assertTrack(tracks.get(19), 20, "PGS", FormatType.SUBTITLES, Iso639Language.CHINESE);
        assertTrack(tracks.get(20), 21, "PGS", FormatType.SUBTITLES, Iso639Language.DANISH);
        assertTrack(tracks.get(21), 22, "PGS", FormatType.SUBTITLES, Iso639Language.FINNISH);
        assertTrack(tracks.get(22), 23, "PGS", FormatType.SUBTITLES, Iso639Language.KOREAN);
        assertTrack(tracks.get(23), 24, "PGS", FormatType.SUBTITLES, Iso639Language.NORWEGIAN);
        assertTrack(tracks.get(24), 25, "PGS", FormatType.SUBTITLES, Iso639Language.PORTUGUESE);
        assertTrack(tracks.get(25), 26, "PGS", FormatType.SUBTITLES, Iso639Language.PORTUGUESE);
        assertTrack(tracks.get(26), 27, "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);
        assertTrack(tracks.get(27), 28, "PGS", FormatType.SUBTITLES, Iso639Language.SWEDISH);
    }

    @Test
    public void scanAndParseFile_parsesTracksAndQuotedNames() throws Exception {
        final FileScanner scanner = scannerReturning(0, MKV_FILE_TRUEHD_LEGACY);

        final List<Track> tracks = scanner.scanAndParseFile(new File("movie.mkv")).getTracks();

        // the leading "MKV, 1 video track, ..." summary line is not a track; the 6 numbered lines are
        assertEquals(6, tracks.size());
        assertTrack(tracks.get(0), 1, null, "h264/AVC", FormatType.VIDEO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(1), 2, "TrueHD", "TrueHD", FormatType.AUDIO, Iso639Language.ENGLISH);    // name on its own line
        assertTrack(tracks.get(2), 3, null, "AC3", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(3), 4, "Spanish", "AC3", FormatType.AUDIO, Iso639Language.SPANISH);      // name on its own line
        assertTrack(tracks.get(4), 5, null, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(5), 6, "Spanish", "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);  // name inline on the track line
    }

    @Test
    public void scanAndParseFile_ignoresParentheticalMetadataLines() throws Exception {
        final FileScanner scanner = scannerReturning(0, MKV_FILE_DTSHD_LEGACY);

        final List<Track> tracks = scanner.scanAndParseFile(new File("movie.mkv")).getTracks();

        assertEquals(5, tracks.size());
        assertTrack(tracks.get(0), 1, null, "h264/AVC", FormatType.VIDEO, Iso639Language.ENGLISH);
        // the "(core: ...)" line is ignored; the following "DTS-HD" line sets this track's name
        assertTrack(tracks.get(1), 2, "DTS-HD", "DTS-HD Master Audio", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(2), 3, "Spanish", "AC3", FormatType.AUDIO, Iso639Language.SPANISH);
        assertTrack(tracks.get(3), 4, null, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(4), 5, "Spanish", "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);
    }

    @Test
    public void scanBluRayDir_new_returnsAllTitleNumbers(@TempDir final File bluRayDir) throws Exception {
        final FileScanner scanner = scannerReturning(0, BD_NEW);
        final Set<Integer> titles = scanner.scanBluRayDir(bluRayDir);

        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6)), titles);
    }

    @Test
    public void scanAndParseBluRayTitle_new_parsesBracketedLanguageCodes(@TempDir final File bluRayDir) throws Exception {
        final FileScanner scanner = scannerReturning(0, BD_TITLE_NEW);

        final List<Track> tracks = scanner.scanAndParseBluRayTitle(bluRayDir, 1).getTracks();

        // preamble + summary lines are skipped; the 14 numbered lines are tracks. languages are bracketed ISO codes,
        // including the /T codes [deu]/[fra]/[nld]
        assertEquals(14, tracks.size());
        assertTrack(tracks.get(0), 1, "OGM Chapters", FormatType.CHAPTERS, Iso639Language.UNDETERMINED);
        assertTrack(tracks.get(1), 2, "h264/AVC", FormatType.VIDEO, Iso639Language.UNDETERMINED);
        assertTrack(tracks.get(2), 3, "AC3", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(3), 4, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(4), 5, "PGS", FormatType.SUBTITLES, Iso639Language.DANISH);
        assertTrack(tracks.get(5), 6, "PGS", FormatType.SUBTITLES, Iso639Language.GERMAN);      // [deu] (ISO 639-2/T)
        assertTrack(tracks.get(6), 7, "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);
        assertTrack(tracks.get(7), 8, "PGS", FormatType.SUBTITLES, Iso639Language.FRENCH);      // [fra] (ISO 639-2/T)
        assertTrack(tracks.get(8), 9, "PGS", FormatType.SUBTITLES, Iso639Language.ITALIAN);
        assertTrack(tracks.get(9), 10, "PGS", FormatType.SUBTITLES, Iso639Language.JAPANESE);
        assertTrack(tracks.get(10), 11, "PGS", FormatType.SUBTITLES, Iso639Language.DUTCH);     // [nld] (ISO 639-2/T)
        assertTrack(tracks.get(11), 12, "PGS", FormatType.SUBTITLES, Iso639Language.NORWEGIAN);
        assertTrack(tracks.get(12), 13, "PGS", FormatType.SUBTITLES, Iso639Language.FINNISH);
        assertTrack(tracks.get(13), 14, "PGS", FormatType.SUBTITLES, Iso639Language.SWEDISH);
    }

    @Test
    public void scanAndParseFile_new_truehd_parsesBracketedCodesAndQuotedNames() throws Exception {
        final FileScanner scanner = scannerReturning(0, MKV_FILE_TRUEHD_NEW);

        final List<Track> tracks = scanner.scanAndParseFile(new File("movie.mkv")).getTracks();

        // same tracks as the *_LEGACY fixture, but languages arrive as bracketed [eng]/[spa] codes
        assertEquals(6, tracks.size());
        assertTrack(tracks.get(0), 1, null, "h264/AVC", FormatType.VIDEO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(1), 2, "TrueHD", "TrueHD", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(2), 3, null, "AC3", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(3), 4, "Spanish", "AC3", FormatType.AUDIO, Iso639Language.SPANISH);
        assertTrack(tracks.get(4), 5, null, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(5), 6, "Spanish", "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);
    }

    @Test
    public void scanAndParseFile_new_dtshd_parsesBracketedCodesAndIgnoresCoreLine() throws Exception {
        final FileScanner scanner = scannerReturning(0, MKV_FILE_DTSHD_NEW);

        final List<Track> tracks = scanner.scanAndParseFile(new File("movie.mkv")).getTracks();

        assertEquals(5, tracks.size());
        assertTrack(tracks.get(0), 1, null, "h264/AVC", FormatType.VIDEO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(1), 2, "DTS-HD", "DTS-HD Master Audio", FormatType.AUDIO, Iso639Language.ENGLISH);
        assertTrack(tracks.get(2), 3, "Spanish", "AC3", FormatType.AUDIO, Iso639Language.SPANISH);
        assertTrack(tracks.get(3), 4, null, "PGS", FormatType.SUBTITLES, Iso639Language.ENGLISH);
        assertTrack(tracks.get(4), 5, "Spanish", "PGS", FormatType.SUBTITLES, Iso639Language.SPANISH);
    }

    @Test
    public void scanAndParseFile_whenTrackFormatTypeUnparseable_throwsRuntimeException() throws Exception {
        final FileScanner scanner = scannerReturning(0, MALFORMED_TRACK_LEGACY);

        final RuntimeException ex = assertThrows(RuntimeException.class,
            () -> scanner.scanAndParseFile(new File("movie.mkv")));
        assertTrue(ex.getMessage().contains("Possible bug in MkvScanner"),
            "unexpected message: " + ex.getMessage());
    }

    @Test
    public void demuxFileByTracks_buildsArgsAndIgnoresTrackNames() throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, MKV_FILE_TRUEHD_LEGACY);
        final File file = new File("movie.mkv");

        // tracks 4 and 6 carry quoted names ("Spanish") that must NOT leak into the output filenames
        final Collection<String> filenames = scanner.demuxFileByTracks(file, Arrays.asList(2, 4, 6));

        final List<List<String>> commands = capturedCommands(2);
        assertEquals(Arrays.asList("eac3to", "movie.mkv"), commands.get(0));
        assertEquals(Arrays.asList("eac3to", "movie.mkv",
            "2:", out(outputDir, "movie_tr2_eng_TrueHD.thd+ac3"),
            "4:", out(outputDir, "movie_tr4_spa_AC3.ac3"),
            "6:", out(outputDir, "movie_tr6_spa_PGS.sup")), commands.get(1));
        assertEquals(new HashSet<>(Arrays.asList("movie_tr2_eng_TrueHD.thd+ac3",
            "movie_tr4_spa_AC3.ac3", "movie_tr6_spa_PGS.sup")), new HashSet<>(filenames));
    }

    @Test
    public void demuxFileByTracks_emitsEveryConfigForMultiConfigFormat() throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, MKV_FILE_DTSHD_LEGACY);
        final File file = new File("movie.mkv");

        // DTS-HD Master Audio maps to two ExtensionConfigs: {dtshd} and {dts, -core}, so track 2 demuxes to two files,
        // the second of which appends the "-core" flag as an argument (and "core" into its filename)
        final Collection<String> filenames = scanner.demuxFileByTracks(file, Arrays.asList(2, 3));

        final List<List<String>> commands = capturedCommands(2);
        assertEquals(Arrays.asList("eac3to", "movie.mkv"), commands.get(0));

        final List<String> demux = commands.get(1);
        assertEquals(Arrays.asList("eac3to", "movie.mkv"), demux.subList(0, 2));
        // the two configs come from a HashSet, so their relative order isn't guaranteed: compare order-insensitively
        assertEquals(sorted(Arrays.asList(
            "2:", out(outputDir, "movie_tr2_eng_DTS-HD_Master_Audio.dtshd"),
            "2:", out(outputDir, "movie_tr2_eng_DTS-HD_Master_Audio_core.dts"), "-core",
            "3:", out(outputDir, "movie_tr3_spa_AC3.ac3"))),
            sorted(demux.subList(2, demux.size())));

        assertEquals(new HashSet<>(Arrays.asList(
            "movie_tr2_eng_DTS-HD_Master_Audio.dtshd",
            "movie_tr2_eng_DTS-HD_Master_Audio_core.dts",
            "movie_tr3_spa_AC3.ac3")), new HashSet<>(filenames));
    }

    @Test
    public void demuxFileByTracks_skipsTrackWithNoFormatExtensionConfig() throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, MKV_FILE_VOBSUB_LEGACY);
        final File file = new File("movie.mkv");

        // track 2 (VobSub) has no entry in eac3to-format-extensions.json, so it is skipped; only track 1 (AC3) demuxes
        final Collection<String> filenames = scanner.demuxFileByTracks(file, Arrays.asList(1, 2));

        final List<List<String>> commands = capturedCommands(2);
        assertEquals(Arrays.asList("eac3to", "movie.mkv"), commands.get(0));
        assertEquals(Arrays.asList("eac3to", "movie.mkv",
            "1:", out(outputDir, "movie_tr1_eng_AC3.ac3")), commands.get(1));
        assertEquals(Collections.singleton("movie_tr1_eng_AC3.ac3"), new HashSet<>(filenames));
    }

    private static void assertTrack(final Track track, final int number, final String formatName,
            final FormatType formatType, final Iso639Language language) {
        assertTrack(track, number, null, formatName, formatType, language);
    }

    private static void assertTrack(final Track track, final int number, final String name, final String formatName,
            final FormatType formatType, final Iso639Language language) {
        assertEquals(number, track.getNumber());
        assertEquals(name, track.getName());
        assertNotNull(track.getFormat());
        assertEquals(formatName, track.getFormat().getName());
        assertEquals(formatType, track.getFormat().getFormatType());
        assertEquals(language, track.getLanguage());
    }

    /**
     * Builds a scanner whose (mocked) executable returns the given exit code and stdout. exec() doesn't touch the
     * filesystem, so the output directory is just a dummy and the scanned file need not exist.
     */
    private FileScanner scannerReturning(final int exitValue, final String stdout) throws Exception {
        when(processRunner.run(anyList())).thenReturn(new ProcessResult(exitValue, stdout, ""));
        return new Eac3toScanner("eac3to", new File("out"), processRunner);
    }

    @Test
    public void exec_whenFormatNotDetected_throwsCorruptBluRayStructureException() throws Exception {
        final FileScanner scanner = scannerReturning(1, "The format of the source file could not be detected.");

        final CorruptBluRayStructureException ex = assertThrows(CorruptBluRayStructureException.class,
            () -> scanner.exec(new File("movie.m2ts")));
        assertEquals("movie.m2ts", ex.getFilename());
        assertEquals("The format of the source file could not be detected.", ex.getDemuxerOutput());
    }

    @Test
    public void exec_whenFileUnreadable_throwsUnreadableFileException() throws Exception {
        final FileScanner scanner = scannerReturning(1, "Error reading file \"movie.txt\".");

        assertThrows(UnreadableFileException.class, () -> scanner.exec(new File("movie.txt")));
    }

    @Test
    public void exec_whenConversionUnsupported_throwsFormatConversionException() throws Exception {
        final FileScanner scanner = scannerReturning(1, "This audio conversion is not supported.");

        assertThrows(FormatConversionException.class, () -> scanner.exec(new File("movie.m2ts")));
    }

    @Test
    public void exec_whenNotBluRayDirectory_throwsNotBluRayDirectoryException() throws Exception {
        final FileScanner scanner = scannerReturning(1, "HD DVD / Blu-Ray disc structure not found.");

        assertThrows(NotBluRayDirectoryException.class, () -> scanner.exec(new File("not-a-bd-dir")));
    }

    @Test
    public void exec_whenUnrecognizedError_throwsGenericDemuxerException() throws Exception {
        final FileScanner scanner = scannerReturning(1, "some unrecognized eac3to failure");

        final DemuxerException ex = assertThrows(DemuxerException.class, () -> scanner.exec(new File("movie.m2ts")));
        // a non-zero exit with no recognized message yields the base type, not one of the subclasses
        assertEquals(DemuxerException.class, ex.getClass());
    }

    @Test
    public void demuxFileByTracks_demuxesSelectedTrackNumbers() throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, BLU_RAY_TITLE_LEGACY);
        final File file = new File("movie.m2ts");

        final Collection<String> filenames = scanner.demuxFileByTracks(file, Arrays.asList(3, 11));

        // demuxHelper scans first, then runs the demux; the scan of a plain file passes no extra args
        final List<List<String>> commands = capturedCommands(2);
        assertEquals(Arrays.asList("eac3to", "movie.m2ts"), commands.get(0));
        assertEquals(Arrays.asList("eac3to", "movie.m2ts",
            "3:", out(outputDir, "movie_tr3_eng_AC3.ac3"),
            "11:", out(outputDir, "movie_tr11_eng_PGS.sup")), commands.get(1));
        assertEquals(new HashSet<>(Arrays.asList(
            "movie_tr3_eng_AC3.ac3", "movie_tr11_eng_PGS.sup")), new HashSet<>(filenames));
    }

    @Test
    public void demuxFileByLanguages_demuxesEveryTrackInLanguage() throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, BLU_RAY_TITLE_LEGACY);
        final File file = new File("movie.m2ts");

        // German (ISO 639-2/T "deu") appears on tracks 7 (AC3), 14 (PGS) and 15 (PGS)
        final Collection<String> filenames = scanner.demuxFileByLanguages(file, Collections.singletonList(Iso639Language.GERMAN));

        final List<List<String>> commands = capturedCommands(2);
        assertEquals(Arrays.asList("eac3to", "movie.m2ts"), commands.get(0));
        assertEquals(Arrays.asList("eac3to", "movie.m2ts",
            "7:", out(outputDir, "movie_tr7_deu_AC3.ac3"),
            "14:", out(outputDir, "movie_tr14_deu_PGS.sup"),
            "15:", out(outputDir, "movie_tr15_deu_PGS.sup")), commands.get(1));
        assertEquals(new HashSet<>(Arrays.asList("movie_tr7_deu_AC3.ac3",
            "movie_tr14_deu_PGS.sup", "movie_tr15_deu_PGS.sup")), new HashSet<>(filenames));
    }

    @Test
    public void demuxBluRayTitleByTracks_buildsArgsForTitleAndMixedFormats(@TempDir final File tempDir) throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, BLU_RAY_TITLE_LEGACY);
        final File bluRayDir = new File(tempDir, "MOVIE_BD");
        assertTrue(bluRayDir.mkdir());

        // track 1 = chapters (looked up by format type, no friendly-name suffix), 2 = VC-1 video, 4 = RAW/PCM audio
        final Collection<String> filenames = scanner.demuxBluRayTitleByTracks(bluRayDir, 1, Arrays.asList(1, 2, 4));

        final List<List<String>> commands = capturedCommands(2);
        // scanning a title passes the "<title>)" selector to the executable
        assertEquals(Arrays.asList("eac3to", bluRayDir.getPath(), "1)"), commands.get(0));
        assertEquals(Arrays.asList("eac3to", bluRayDir.getPath(), "1)",
            "1:", out(outputDir, "MOVIE_BD_ti1_tr1_Chapters.txt"),
            "2:", out(outputDir, "MOVIE_BD_ti1_tr2_und_VC-1.mkv"),
            "4:", out(outputDir, "MOVIE_BD_ti1_tr4_eng_RAW_PCM.pcm")), commands.get(1));
        assertEquals(new HashSet<>(Arrays.asList("MOVIE_BD_ti1_tr1_Chapters.txt",
            "MOVIE_BD_ti1_tr2_und_VC-1.mkv", "MOVIE_BD_ti1_tr4_eng_RAW_PCM.pcm")),
            new HashSet<>(filenames));
    }

    @Test
    public void demuxBluRayTitleByLanguages_buildsArgsForTitleAndLanguage(@TempDir final File tempDir) throws Exception {
        final File outputDir = new File("out");
        final FileScanner scanner = scannerWithOutputDir(outputDir, BLU_RAY_TITLE_LEGACY);
        final File bluRayDir = new File(tempDir, "MOVIE_BD");
        assertTrue(bluRayDir.mkdir());

        // French (ISO 639-2/T "fra") appears on tracks 5 (AC3) and 13 (PGS)
        final Collection<String> filenames = scanner.demuxBluRayTitleByLanguages(bluRayDir, 1, Collections.singletonList(Iso639Language.FRENCH));

        final List<List<String>> commands = capturedCommands(2);
        assertEquals(Arrays.asList("eac3to", bluRayDir.getPath(), "1)"), commands.get(0));
        assertEquals(Arrays.asList("eac3to", bluRayDir.getPath(), "1)",
            "5:", out(outputDir, "MOVIE_BD_ti1_tr5_fra_AC3.ac3"),
            "13:", out(outputDir, "MOVIE_BD_ti1_tr13_fra_PGS.sup")), commands.get(1));
        assertEquals(new HashSet<>(Arrays.asList(
            "MOVIE_BD_ti1_tr5_fra_AC3.ac3", "MOVIE_BD_ti1_tr13_fra_PGS.sup")), new HashSet<>(filenames));
    }

    private FileScanner scannerWithOutputDir(final File outputDir, final String scanOutput) throws Exception {
        when(processRunner.run(anyList())).thenReturn(new ProcessResult(0, scanOutput, ""));
        return new Eac3toScanner("eac3to", outputDir, processRunner);
    }

    /** Captures the command list of every {@code processRunner.run(...)} call, asserting there were exactly {@code n}. */
    @SuppressWarnings("unchecked")
    private List<List<String>> capturedCommands(final int n) throws Exception {
        final ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(processRunner, times(n)).run(captor.capture());
        return captor.getAllValues();
    }

    /** The absolute demux output path the scanner would build for a file named {@code name} in {@code outputDir}. */
    private static String out(final File outputDir, final String name) {
        return outputDir.getAbsolutePath() + File.separator + name;
    }

    /** Returns a sorted copy of the given list, for order-insensitive comparison of command arguments. */
    private static List<String> sorted(final List<String> values) {
        final List<String> copy = new ArrayList<>(values);
        Collections.sort(copy);
        return copy;
    }
}
