package com.zerofall.ezstorage.util;

import net.minecraft.util.text.translation.LanguageMap;

/** A translator to use in case of exceptions in sorting */
public class FallbackTranslator {

	/** A StringTranslate instance using the hardcoded default locale (en_US). Used as a fallback in case the shared StringTranslate singleton instance fails to translate a key. */
	private static final LanguageMap fallbackTranslator = new LanguageMap();

	/** Translates a Stat name */
	public static String translate(String key) {
		return fallbackTranslator.translateKey(key);
	}

	/** Translates a Stat name with format args */
	public static String translateFormatted(String key, Object... format) {
		return fallbackTranslator.translateKeyFormat(key, format);
	}

	/** Determines whether or not translateToLocal will find a translation for the given key. */
	public static boolean canTranslate(String key) {
		return fallbackTranslator.isKeyTranslated(key);
	}

	/** Gets the time, in milliseconds since epoch, that the translation mapping was last updated */
	public static long getLastTranslationUpdateTimeInMilliseconds() {
		return fallbackTranslator.getLastUpdateTimeInMilliseconds();
	}
}