package com.adashrod.mkvscanner.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ISO 639-2 languages. Each constant carries a canonical English {@link #getName() name}, a 3-letter
 * {@link #getCode() code}, and any number of alternate names ({@link #getAliases() aliases}) by which the same
 * language is known. {@link #fromToken(String)} resolves a language token (as emitted by eac3to) to a constant,
 * accepting either a plain name/alias (e.g. {@code "English"}, {@code "Flemish"}) or a bracketed ISO code
 * (e.g. {@code "[eng]"}), case-insensitively.
 */
public enum Iso639Language {
    ABKHAZIAN("Abkhazian", "abk"),
    AFAR("Afar", "aar"),
    AFRIKAANS("Afrikaans", "afr"),
    AKAN("Akan", "aka"),
    ALBANIAN("Albanian", "sqi"),
    AMHARIC("Amharic", "amh"),
    ARABIC("Arabic", "ara"),
    ARAGONESE("Aragonese", "arg"),
    ARMENIAN("Armenian", "hye"),
    ASSAMESE("Assamese", "asm"),
    AVARIC("Avaric", "ava"),
    AVESTAN("Avestan", "ave"),
    AYMARA("Aymara", "aym"),
    AZERBAIJANI("Azerbaijani", "aze"),
    BAMBARA("Bambara", "bam"),
    BASHKIR("Bashkir", "bak"),
    BASQUE("Basque", "eus"),
    BELARUSIAN("Belarusian", "bel"),
    BENGALI("Bengali", "ben"),
    BISLAMA("Bislama", "bis"),
    BOSNIAN("Bosnian", "bos"),
    BRETON("Breton", "bre"),
    BULGARIAN("Bulgarian", "bul"),
    BURMESE("Burmese", "mya"),
    CATALAN("Catalan", "cat", "Valencian"),
    CHAMORRO("Chamorro", "cha"),
    CHECHEN("Chechen", "che"),
    CHICHEWA("Chichewa", "nya", "Chewa", "Nyanja"),
    CHINESE("Chinese", "zho"),
    CHURCH_SLAVONIC("Church Slavonic", "chu", "Old Slavonic", "Old Church Slavonic"),
    CHUVASH("Chuvash", "chv"),
    CORNISH("Cornish", "cor"),
    CORSICAN("Corsican", "cos"),
    CREE("Cree", "cre"),
    CROATIAN("Croatian", "hrv"),
    CZECH("Czech", "ces"),
    DANISH("Danish", "dan"),
    DIVEHI("Divehi", "div", "Dhivehi", "Maldivian"),
    DUTCH("Dutch", "nld", "Flemish"),
    DZONGKHA("Dzongkha", "dzo"),
    ENGLISH("English", "eng"),
    ESPERANTO("Esperanto", "epo"),
    ESTONIAN("Estonian", "est"),
    EWE("Ewe", "ewe"),
    FAROESE("Faroese", "fao"),
    FIJIAN("Fijian", "fij"),
    FINNISH("Finnish", "fin"),
    FRENCH("French", "fra"),
    WESTERN_FRISIAN("Western Frisian", "fry"),
    FULAH("Fulah", "ful"),
    GAELIC("Gaelic", "gla", "Scottish Gaelic"),
    GALICIAN("Galician", "glg"),
    GANDA("Ganda", "lug"),
    GEORGIAN("Georgian", "kat"),
    GERMAN("German", "deu"),
    GREEK("Greek", "ell"),
    KALAALLISUT("Kalaallisut", "kal", "Greenlandic"),
    GUARANI("Guarani", "grn"),
    GUJARATI("Gujarati", "guj"),
    HAITIAN("Haitian", "hat", "Haitian Creole"),
    HAUSA("Hausa", "hau"),
    HEBREW("Hebrew", "heb"),
    HERERO("Herero", "her"),
    HINDI("Hindi", "hin"),
    HIRI_MOTU("Hiri Motu", "hmo"),
    HUNGARIAN("Hungarian", "hun"),
    ICELANDIC("Icelandic", "isl"),
    IDO("Ido", "ido"),
    IGBO("Igbo", "ibo"),
    INDONESIAN("Indonesian", "ind"),
    INTERLINGUA("Interlingua", "ina"),
    INTERLINGUE("Interlingue", "ile", "Occidental"),
    INUKTITUT("Inuktitut", "iku"),
    INUPIAQ("Inupiaq", "ipk"),
    IRISH("Irish", "gle"),
    ITALIAN("Italian", "ita"),
    JAPANESE("Japanese", "jpn"),
    JAVANESE("Javanese", "jav"),
    KANNADA("Kannada", "kan"),
    KANURI("Kanuri", "kau"),
    KASHMIRI("Kashmiri", "kas"),
    KAZAKH("Kazakh", "kaz"),
    CENTRAL_KHMER("Central Khmer", "khm"),
    KIKUYU("Kikuyu", "kik", "Gikuyu"),
    KINYARWANDA("Kinyarwanda", "kin"),
    KYRGYZ("Kyrgyz", "kir", "Kirghiz"),
    KOMI("Komi", "kom"),
    KONGO("Kongo", "kon"),
    KOREAN("Korean", "kor"),
    KUANYAMA("Kuanyama", "kua", "Kwanyama"),
    KURDISH("Kurdish", "kur"),
    LAO("Lao", "lao"),
    LATIN("Latin", "lat"),
    LATVIAN("Latvian", "lav"),
    LIMBURGAN("Limburgan", "lim", "Limburger", "Limburgish"),
    LINGALA("Lingala", "lin"),
    LITHUANIAN("Lithuanian", "lit"),
    LUBA_KATANGA("Luba-Katanga", "lub"),
    LUXEMBOURGISH("Luxembourgish", "ltz", "Letzeburgesch"),
    MACEDONIAN("Macedonian", "mkd"),
    MALAGASY("Malagasy", "mlg"),
    MALAY("Malay", "msa"),
    MALAYALAM("Malayalam", "mal"),
    MALTESE("Maltese", "mlt"),
    MANX("Manx", "glv"),
    MAORI("Maori", "mri"),
    MARATHI("Marathi", "mar"),
    MARSHALLESE("Marshallese", "mah"),
    MONGOLIAN("Mongolian", "mon"),
    NAURU("Nauru", "nau"),
    NAVAJO("Navajo", "nav", "Navaho"),
    NORTH_NDEBELE("North Ndebele", "nde"),
    SOUTH_NDEBELE("South Ndebele", "nbl"),
    NDONGA("Ndonga", "ndo"),
    NEPALI("Nepali", "nep"),
    NORWEGIAN("Norwegian", "nor"),
    NORWEGIAN_BOKMAL("Norwegian Bokmål", "nob"),
    NORWEGIAN_NYNORSK("Norwegian Nynorsk", "nno"),
    OCCITAN("Occitan", "oci"),
    OJIBWA("Ojibwa", "oji"),
    ORIYA("Oriya", "ori"),
    OROMO("Oromo", "orm"),
    OSSETIAN("Ossetian", "oss", "Ossetic"),
    PALI("Pali", "pli"),
    PASHTO("Pashto", "pus", "Pushto"),
    PERSIAN("Persian", "fas"),
    POLISH("Polish", "pol"),
    PORTUGUESE("Portuguese", "por"),
    PUNJABI("Punjabi", "pan", "Panjabi"),
    QUECHUA("Quechua", "que"),
    ROMANIAN("Romanian", "ron", "Moldavian", "Moldovan"),
    ROMANSH("Romansh", "roh"),
    RUNDI("Rundi", "run"),
    RUSSIAN("Russian", "rus"),
    NORTHERN_SAMI("Northern Sami", "sme"),
    SAMOAN("Samoan", "smo"),
    SANGO("Sango", "sag"),
    SANSKRIT("Sanskrit", "san"),
    SARDINIAN("Sardinian", "srd"),
    SERBIAN("Serbian", "srp"),
    SHONA("Shona", "sna"),
    SINDHI("Sindhi", "snd"),
    SINHALA("Sinhala", "sin", "Sinhalese"),
    SLOVAK("Slovak", "slk"),
    SLOVENIAN("Slovenian", "slv"),
    SOMALI("Somali", "som"),
    SOUTHERN_SOTHO("Southern Sotho", "sot"),
    SPANISH("Spanish", "spa", "Castilian"),
    SUNDANESE("Sundanese", "sun"),
    SWAHILI("Swahili", "swa"),
    SWATI("Swati", "ssw"),
    SWEDISH("Swedish", "swe"),
    TAGALOG("Tagalog", "tgl"),
    TAHITIAN("Tahitian", "tah"),
    TAJIK("Tajik", "tgk"),
    TAMIL("Tamil", "tam"),
    TATAR("Tatar", "tat"),
    TELUGU("Telugu", "tel"),
    THAI("Thai", "tha"),
    TIBETAN("Tibetan", "bod"),
    TIGRINYA("Tigrinya", "tir"),
    TONGA("Tonga", "ton"),
    TSONGA("Tsonga", "tso"),
    TSWANA("Tswana", "tsn"),
    TURKISH("Turkish", "tur"),
    TURKMEN("Turkmen", "tuk"),
    TWI("Twi", "twi"),
    UIGHUR("Uighur", "uig", "Uyghur"),
    UKRAINIAN("Ukrainian", "ukr"),
    URDU("Urdu", "urd"),
    UZBEK("Uzbek", "uzb"),
    VENDA("Venda", "ven"),
    VIETNAMESE("Vietnamese", "vie"),
    VOLAPUK("Volapük", "vol"),
    WALLOON("Walloon", "wln"),
    WELSH("Welsh", "cym"),
    WOLOF("Wolof", "wol"),
    XHOSA("Xhosa", "xho"),
    SICHUAN_YI("Sichuan Yi", "iii", "Nuosu"),
    YIDDISH("Yiddish", "yid"),
    YORUBA("Yoruba", "yor"),
    ZHUANG("Zhuang", "zha", "Chuang"),
    ZULU("Zulu", "zul"),
    /** Sentinel for a language that could not be resolved from eac3to output. */
    UNDETERMINED("Undetermined", "und");

    private static final Map<String, Iso639Language> BY_NAME = new HashMap<>();
    private static final Map<String, Iso639Language> BY_CODE = new HashMap<>();
    static {
        for (final Iso639Language language : values()) {
            BY_NAME.put(language.name.toLowerCase(), language);
            for (final String alias : language.aliases) {
                BY_NAME.put(alias.toLowerCase(), language);
            }
            BY_CODE.put(language.code.toLowerCase(), language);
        }
        // ISO 639-2/B (bibliographic) codes as fallback aliases for the 20 languages where /T and /B differ.
        // getCode() returns the /T code (modern standard); these widen what fromToken(...) accepts for backwards compatibility.
        BY_CODE.put("alb", ALBANIAN);
        BY_CODE.put("arm", ARMENIAN);
        BY_CODE.put("baq", BASQUE);
        BY_CODE.put("bur", BURMESE);
        BY_CODE.put("chi", CHINESE);
        BY_CODE.put("cze", CZECH);
        BY_CODE.put("dut", DUTCH);
        BY_CODE.put("fre", FRENCH);
        BY_CODE.put("geo", GEORGIAN);
        BY_CODE.put("ger", GERMAN);
        BY_CODE.put("gre", GREEK);
        BY_CODE.put("ice", ICELANDIC);
        BY_CODE.put("mac", MACEDONIAN);
        BY_CODE.put("mao", MAORI);
        BY_CODE.put("may", MALAY);
        BY_CODE.put("per", PERSIAN);
        BY_CODE.put("rum", ROMANIAN);
        BY_CODE.put("slo", SLOVAK);
        BY_CODE.put("tib", TIBETAN);
        BY_CODE.put("wel", WELSH);
    }

    private final String name;
    private final String code;
    private final String[] aliases;

    Iso639Language(final String name, final String code, final String... aliases) {
        this.name = name;
        this.code = code;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public java.util.List<String> getAliases() {
        return Collections.unmodifiableList(java.util.Arrays.asList(aliases));
    }

    /**
     * Resolves a language token from scanner output to a language. The token may be a plain name or alias
     * (e.g. {@code "English"}, {@code "Flemish"}) or an ISO 639-2 code with or without brackets (e.g. {@code "[eng]"}
     * or {@code "eng"}); matching is case-insensitive.
     * @param token the language token from scanner output
     * @return the matching language, or {@code null} if none matches
     */
    public static Iso639Language fromToken(final String token) {
        if (token == null) {
            return null;
        }
        final String trimmed = token.trim();
        if (trimmed.length() >= 2 && trimmed.charAt(0) == '[' && trimmed.charAt(trimmed.length() - 1) == ']') {
            return BY_CODE.get(trimmed.substring(1, trimmed.length() - 1).trim().toLowerCase());
        }
        if (trimmed.length() == 3) {
            return BY_CODE.get(trimmed.toLowerCase());
        }
        return BY_NAME.get(trimmed.toLowerCase());
    }
}
