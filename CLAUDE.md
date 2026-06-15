# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Project Is

A Java library that wraps [eac3to](https://www.videohelp.com/software/eac3to), a Windows CLI tool for scanning and demuxing MKV/M2TS files and Blu-ray directories. The library is Windows-only at runtime (requires `eac3to.exe`), but builds and tests run on any platform since tests mock the process runner.

## Build System

Gradle with wrapper. Source/target compatibility is Java 8. Set `JAVA_HOME` to Java 8 when building.

```bash
# Build the jar (compiles and runs tests)
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew build

# Run all tests
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew test

# Run a subset of tests (partial class-name match)
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew test --tests "*Iso639*"

# Clean all build artifacts
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew clean
```

## Publishing

Publishing to Maven Central (Sonatype) requires GPG signing and Sonatype credentials. Tokens are generated at central.sonatype.com.

```bash
# Publish snapshot (version must end in -SNAPSHOT)
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew publishToSonatype \
  -Ppublish.user=<token-user> \
  -Ppublish.password=<token-password> \
  -Psigning.gnupg.passphrase="<gpg-passphrase>"

# Publish release (remove -SNAPSHOT from version in build.gradle first)
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository \
  -Ppublish.user=<token-user> \
  -Ppublish.password=<token-password> \
  -Psigning.gnupg.passphrase="<gpg-passphrase>"

# Test publishing locally (no upload to Sonatype)
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64 ./gradlew publishToMavenLocal
```

Repository URLs configured in `build.gradle`:
- Snapshots: `https://central.sonatype.com/repository/maven-snapshots/`
- Staging: `https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/`

## Architecture

### Entry Point

`FileScanner` (interface) → `Eac3toScanner` (sole implementation). Consumers instantiate `Eac3toScanner` with the path to `eac3to.exe` and an output directory.

### Parsing Flow

`Eac3toScanner` invokes eac3to via `ProcessRunner` (seam interface), then parses its stdout line-by-line using `StringLineIterator`. Parsed output is assembled into a `Video` containing a list of `Track` objects.

Eac3to output comes in two formats ("legacy" plain-English language names, "new" ISO 639-2 bracketed codes). `Iso639Language.fromToken()` normalizes both.

### Model

- `Video` → `List<Track>`
- `Track`: number, name (nullable quoted string), `Format`, `Iso639Language`
- `Format`: name string + `FormatType` enum (AUDIO/VIDEO/SUBTITLES/CHAPTERS)
- `Iso639Language`: full ISO 639-2 enum; `fromToken(String)` accepts plain names, aliases, bracketed codes (`[eng]`), bare codes — case-insensitive, whitespace-trimmed; `UNDETERMINED` sentinel for absent/unknown language

### Format→Extension Mapping

`src/main/resources/eac3to-format-extensions.json` maps format names to output file extensions and optional flags. Loaded by `FormatExtensionConfig`. Formats with multiple configs (DTS-HD Master Audio, TrueHD/AC3) produce multiple output files per track. VobSub tracks are silently skipped during demux (extension unknown).

### Testing

Tests mock `ProcessRunner` with fixture strings representing real eac3to output, so no eac3to binary is needed. `Eac3toScanner` exposes a package-private constructor accepting a `ProcessRunner` for this purpose.
