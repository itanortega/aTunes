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

package net.sourceforge.atunes.gui.frame;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IFrameSize;
import net.sourceforge.atunes.model.IStateUI;

import org.junit.Assert;
import org.junit.Test;

public class SaveFrameSizeTaskTest {

	@Test
	public void test() {
		AbstractSingleFrame frame = mock(AbstractSingleFrame.class);
		when(frame.getExtendedState()).thenReturn(java.awt.Frame.MAXIMIZED_BOTH);
		IStateUI stateUI = mock(IStateUI.class);
		IFrameSize frameSize = new FrameSize();
		when(stateUI.getFrameSize()).thenReturn(frameSize);
		SaveFrameSizeTask sut = new SaveFrameSizeTask(frame, stateUI, 1, 2);
		
		sut.run();
		
		verify(stateUI).setFrameSize(frameSize);
		Assert.assertEquals(1, frameSize.getWindowWidth());
		Assert.assertEquals(2, frameSize.getWindowHeight());
		Assert.assertTrue(frameSize.isMaximized());
	}
}