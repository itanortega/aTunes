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

import junit.framework.Assert;

import org.junit.Test;

public class JVMPropertiesTest {

	private JVMProperties sut = new JVMProperties();
	
	@Test
	public void test0() {
		Assert.assertTrue(sut.isJava6Update10OrLater("2.0.0_0"));
	}

	@Test
	public void test1() {
		Assert.assertTrue(sut.isJava6Update10OrLater("1.7.0_0"));
	}
	
	@Test
	public void test2() {
		Assert.assertTrue(sut.isJava6Update10OrLater("1.6.0_26"));
	}
	
	@Test
	public void test3() {
		Assert.assertTrue(sut.isJava6Update10OrLater("1.6.0_10"));
	}
	
	@Test
	public void test4() {
		Assert.assertFalse(sut.isJava6Update10OrLater("1.6.0_9"));
	}
	
	@Test
	public void test5() {
		Assert.assertFalse(sut.isJava6Update10OrLater("1.5.0_11"));
	}
	
	@Test
	public void test6() {
		Assert.assertFalse(sut.isJava6Update10OrLater("1.5"));
	}
	
	@Test
	public void test7() {
		Assert.assertFalse(sut.isJava6Update10OrLater(null));
	}
}
