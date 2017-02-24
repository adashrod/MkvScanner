package com.adashrod.mkvscanner.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by aaron on 2016-01-11.
 */
public class FormatExtensionConfig {
    private final String format;
    private final Collection<ExtensionConfig> configs = new HashSet<>();

    public FormatExtensionConfig(final String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
    public Collection<ExtensionConfig> getConfigs() {
        return configs;
    }

    public static class ExtensionConfig {
        private final String extension;
        private final Collection<String> flags = new HashSet<>();

        public ExtensionConfig(final String extension) {
            this.extension = extension;
        }

        public ExtensionConfig(final String extension, final Iterable flags) {
            this.extension = extension;
            if (flags != null) {
                for (final Object o: flags) {
                    this.flags.add(o.toString());
                }
            }
        }

        public String getExtension() {
            return extension;
        }

        public Collection<String> getFlags() {
            return flags;
        }
    }
}
