/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

import java.util.UUID;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsflyEngine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LyricsflyEngineTest {

    private LyricsflyEngine testedObject;

    @Before
    public void init() {
        testedObject = new LyricsflyEngine(null);
    }

    @Test
    public void testGetLyricsFor() {
        Lyrics lyrics = testedObject.getLyricsFor("Louis Armstrong", "We Have All The Time In The World");
        Assert.assertNotNull(lyrics);
        Assert.assertTrue(lyrics.getUrl().contains("lyricsfly.com"));
        Assert.assertTrue(lyrics.getLyrics().toLowerCase().contains("To unfold all the precious things".toLowerCase()));
    }

    @Test
    public void testCaseInsensitivity() throws InterruptedException {
        Thread.sleep(1000);
        Lyrics lyrics1 = testedObject.getLyricsFor("Louis Armstrong", "We Have All The Time In The World");
        Thread.sleep(1000);
        Lyrics lyrics2 = testedObject.getLyricsFor("Louis armstrong", "We Have All The Time In The World");
        Thread.sleep(1000);
        Lyrics lyrics3 = testedObject.getLyricsFor("Louis Armstrong", "We Have All the Time In the world");

        Assert.assertEquals(lyrics1, lyrics2);
        Assert.assertEquals(lyrics2, lyrics3);
        Assert.assertEquals(lyrics3, lyrics1);
    }

    @Test
    public void testGetLyricsForWhereArtistAndTitleNotExist() throws InterruptedException {
        Thread.sleep(1000);
        Lyrics lyrics = testedObject.getLyricsFor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Assert.assertEquals(null, lyrics);
    }
}