package com.zerofall.ezstorage.jei;

import com.zerofall.ezstorage.config.EZConfig;

/** Things to help with JEI integration */
public class JEIUtils {
	
	static boolean jeiLoaded = false;
	static String searchTextOld;
	
	/** Query JEI availability */
	public static boolean isJEIAvailable() {
		return jeiLoaded;
	}
	
	/** Get the JEI search text */
	public static String getSearchText() {
		if(EZStoragePlugin.jeiOverlay != null) {
			return EZStoragePlugin.jeiOverlay.getFilterText();
		}
		return "";
	}
	
	/** Set the JEI search text */
	public static void setSearchText(String text) {
		if(EZStoragePlugin.jeiOverlay != null) EZStoragePlugin.jeiOverlay.setFilterText(text);
	}
	
	/** Returns true when the search text differs from the previous queried value */
	public static boolean jeiSearchTextChanged() {
		boolean flag = false;
		if(EZStoragePlugin.jeiOverlay != null) {
			String text = EZStoragePlugin.jeiOverlay.getFilterText();
			if(!text.equals(searchTextOld)) flag = true;
			searchTextOld = text;
		}
		return flag;
	}

}
