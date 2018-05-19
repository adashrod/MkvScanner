# MkvScanner

MkvScanner provides an API for scanning Blu-ray directories and container files (such as MKVs), and for demuxing tracks from them as well.

The interface FileScanner defines the API. The available implementation is Eac3toScanner, which is a wrapper for the Windows tool eac3to.exe, and requires the executable to function.

## Example
### scanning:
~~~~
FileScanner scanner = new Eac3toScanner("c:\\Program Files\\eac3to\\eac3to.exe", "c:\\demuxedTracks");
Video metadata = scanner.scanAndParseFile(new File("c:\\mkvs\\MyMovie.mkv"));
~~~~

`metadata` now contains a list of the tracks found in the mkv file. Each track object (in `metadata.getTracks()`) contains information about the track, such as:
- name, plaintext label, if it has one
- format name (AC-3, H-264, PGS, etc)
- format type (Audio, Video, Subtitles, Chapters)
- language

### demuxing:
~~~~
scanner.demuxFileByTracks(new File("c:\\mkvs\\MyMovie.mkv"), Arrays.asList(1, 2, 3));
// demuxes only the first three tracks
scanner.demuxFileByLanguages(new File("c:\\mkvs\\MyMovie.mkv"), Arrays.asList("English", "Spanish", "Japanese"));
// demuxes all tracks in the video with the matching languages
~~~~
demuxed tracks are output into the directory specified in the Eac3toScanner constructor

### adding as a dependency

Page on [search.maven.org](http://search.maven.org/#artifactdetails%7Ccom.adashrod.mkvscanner%7Cmkvscanner%7C1.0.0%7Cpom)

##### Maven:
~~~~
<dependency>
    <groupId>com.adashrod.mkvscanner</groupId>
    <artifactId>mkvscanner</artifactId>
    <version>1.0.0</version>
</dependency>
~~~~
##### Ivy:
~~~~
<dependency org="com.adashrod.mkvscanner" name="mkvscanner" rev="1.0.0" />
~~~~
