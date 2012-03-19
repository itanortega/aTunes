/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Properties of current Java Virtual Machine
 */
public final class JVMProperties {

    /**
     * Checks if java 6 update 10 or later is used.
     * 
     * @return true, if java 6 update 10 or later is used
     */
    public boolean isJava6Update10OrLater() {
        return isJava6Update10OrLater(System.getProperty("java.version"));
    }
    
    /**
     * Checks if java 6 update 10 or later is used.
     * @param javaVersion
     * @return
     */
    public boolean isJava6Update10OrLater(String javaVersion) {
    	if (javaVersion != null) {
    		Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+).*");
    		Matcher matcher = pattern.matcher(javaVersion);
    		if (matcher.find()) {
    			try {
    				int v1 = Integer.parseInt(matcher.group(1));
    				int v2 = Integer.parseInt(matcher.group(2));
    				int v3 = Integer.parseInt(matcher.group(3));
    				int update = Integer.parseInt(matcher.group(4));
    				return isVersionEqualOrLater(v1, v2, v3, update);
    			} catch (NumberFormatException e) {
    				return false;
    			}
    		}
    	}
    	return false;
    }

	/**
	 * Returns if version is equal or later to 1.6.0_10
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param update
	 * @return
	 */
	private boolean isVersionEqualOrLater(int v1, int v2, int v3, int update) {
		if (v1 >= 2) {
			return true;
		}
		if (v1 == 1 && v2 > 6) {
			return true;
		}
		if (v1 == 1 && v2 == 6 && v3 > 0) {
			return true;
		}
		if (v1 == 1 && v2 == 6 && v3 == 0 && update >= 10) {
			return true;
		}
		return false;
	}
}
