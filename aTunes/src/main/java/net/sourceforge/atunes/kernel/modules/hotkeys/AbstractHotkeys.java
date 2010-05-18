/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.hotkeys;

import java.lang.reflect.Constructor;

import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

public abstract class AbstractHotkeys {

    private HotkeyListener hotkeyListener;

    protected AbstractHotkeys(HotkeyListener hotkeyListener) {
        this.hotkeyListener = hotkeyListener;
    }

    /**
     * Tries to register a hotkey in the system
     * 
     * @param hotkey
     * @return <code>true</code> if hotkey was registered, <code>false</code>
     *         otherwise
     */
    public abstract boolean registerHotkey(Hotkey hotkey);

    public abstract void unregisterHotkey(Hotkey hotkey);

    public abstract void deactivate();

    public abstract void activate();

    public abstract void cleanUp();

    public static AbstractHotkeys createInstance(HotkeyListener hotkeyListener) {
        try {
            if (SystemProperties.OS == OperatingSystem.WINDOWS && !SystemProperties.OS.is64Bit() && Win32Hotkeys.isSupported()) {
                Class<?> clazz = Class.forName("net.sourceforge.atunes.kernel.modules.hotkeys.Win32Hotkeys");
                Constructor<?> constructor = clazz.getConstructor(HotkeyListener.class);
                return (AbstractHotkeys) constructor.newInstance(hotkeyListener);
            } else if (SystemProperties.OS == OperatingSystem.LINUX) {
                Class<?> clazz = Class.forName("net.sourceforge.atunes.kernel.modules.hotkeys.X11Hotkeys");
                Constructor<?> constructor = clazz.getConstructor(HotkeyListener.class);
                return (AbstractHotkeys) constructor.newInstance(hotkeyListener);
            } else {
                return null;
            }
        } catch (Throwable e) {
            new Logger().info(LogCategories.HOTKEYS, "No hotkeys supported");
            return null;
        }
    }

    protected HotkeyListener getHotkeyListener() {
        return hotkeyListener;
    }
}