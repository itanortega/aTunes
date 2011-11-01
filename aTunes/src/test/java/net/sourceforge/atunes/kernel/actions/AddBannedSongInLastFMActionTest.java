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

package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.BackgroundWorkerMock;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;

import org.junit.Assert;
import org.junit.Test;

public class AddBannedSongInLastFMActionTest {

	@Test
	public void test() {
		AddBannedSongInLastFMAction sut = new AddBannedSongInLastFMAction();
		IWebServicesHandler webServicesHandler = mock(IWebServicesHandler.class);
		IContextHandler contextHandler = mock(IContextHandler.class);
		IAudioObject ao = mock(IAudioObject.class);
		when(contextHandler.getCurrentAudioObject()).thenReturn(ao);
		IState state = mock(IState.class);
		when(state.isLastFmEnabled()).thenReturn(true);
		sut.setState(state);
		sut.setWebServicesHandler(webServicesHandler);
		sut.setContextHandler(contextHandler);
		sut.setBackgroundWorker(new BackgroundWorkerMock());
		
		sut.initialize();
		
		Assert.assertTrue(sut.isEnabled());
		
		sut.executeAction();
		
		verify(webServicesHandler).addBannedSong(ao);
	}
}
