package com.adashrod.mkvscanner.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Iso639LanguageTest {
    @Test
    public void fromToken_resolvesCanonicalName() {
        assertEquals(Iso639Language.ENGLISH, Iso639Language.fromToken("English"));
        assertEquals(Iso639Language.GERMAN, Iso639Language.fromToken("German"));
    }

    @Test
    public void fromToken_isCaseInsensitiveAndTrims() {
        assertEquals(Iso639Language.ENGLISH, Iso639Language.fromToken("  english  "));
        assertEquals(Iso639Language.SPANISH, Iso639Language.fromToken("SPANISH"));
    }

    @Test
    public void fromToken_resolvesAliases() {
        assertEquals(Iso639Language.DUTCH, Iso639Language.fromToken("Flemish"));
        assertEquals(Iso639Language.SPANISH, Iso639Language.fromToken("Castilian"));
    }

    @Test
    public void fromToken_resolvesBracketedIsoCode() {
        assertEquals(Iso639Language.ENGLISH, Iso639Language.fromToken("[eng]"));
        assertEquals(Iso639Language.SPANISH, Iso639Language.fromToken("[spa]"));
        assertEquals(Iso639Language.UNDETERMINED, Iso639Language.fromToken("[und]"));
        // case-insensitive inside the brackets too
        assertEquals(Iso639Language.FRENCH, Iso639Language.fromToken("[FRE]"));
    }

    @Test
    public void fromToken_resolvesUnbracketedIsoCode() {
        assertEquals(Iso639Language.ENGLISH, Iso639Language.fromToken("eng"));
        assertEquals(Iso639Language.SPANISH, Iso639Language.fromToken("spa"));
        assertEquals(Iso639Language.UNDETERMINED, Iso639Language.fromToken("und"));
        assertEquals(Iso639Language.FRENCH, Iso639Language.fromToken("FRE"));
    }

    @Test
    public void fromToken_resolvesIso639_2TCodes() {
        // eac3to emits ISO 639-2/T (terminological) codes; the enum now uses /T as the canonical code.
        // Both bracketed and unbracketed forms resolve via fromToken
        assertEquals(Iso639Language.GERMAN, Iso639Language.fromToken("[deu]"));
        assertEquals(Iso639Language.FRENCH, Iso639Language.fromToken("[fra]"));
        assertEquals(Iso639Language.DUTCH, Iso639Language.fromToken("[nld]"));
        assertEquals(Iso639Language.CHINESE, Iso639Language.fromToken("zho"));
        // getCode() returns the /T code (modern standard)
        assertEquals("deu", Iso639Language.GERMAN.getCode());
        assertEquals("fra", Iso639Language.FRENCH.getCode());
        assertEquals("nld", Iso639Language.DUTCH.getCode());
        assertEquals("zho", Iso639Language.CHINESE.getCode());
    }

    @Test
    public void fromToken_throwsForUnknownOrMalformed() {
        assertThrows(IllegalArgumentException.class, () -> Iso639Language.fromToken("Klingon"));
        assertThrows(IllegalArgumentException.class, () -> Iso639Language.fromToken("[zzz]"));   // not a real code
        assertThrows(IllegalArgumentException.class, () -> Iso639Language.fromToken(null));
    }

    @Test
    public void accessors_exposeNameCodeAndAliases() {
        assertEquals("English", Iso639Language.ENGLISH.getName());
        assertEquals("eng", Iso639Language.ENGLISH.getCode());
        assertEquals("nld", Iso639Language.DUTCH.getCode());  // ISO 639-2/T code
        assertTrue(Iso639Language.DUTCH.getAliases().contains("Flemish"));
    }
}
