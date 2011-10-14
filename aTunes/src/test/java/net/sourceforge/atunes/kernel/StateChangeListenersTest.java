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

package net.sourceforge.atunes.kernel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateChangeListener;

import org.junit.Before;
import org.junit.Test;


public class StateChangeListenersTest {

	private StateChangeListeners sut;	
	private IStateChangeListener mock1;
	private IStateChangeListener mock2;
	private IState state;
	
	@Before
	public void init() {
		sut = new StateChangeListeners();
		List<IStateChangeListener> listeners = new ArrayList<IStateChangeListener>();
		mock1 = mock(IStateChangeListener.class);
		mock2 = mock(IStateChangeListener.class);
		state = mock(IState.class);
		listeners.add(mock1);
		listeners.add(mock2);
		sut.setListeners(listeners);
		sut.setState(state);
	}
	
	@Test
	public void notifyApplicationStateChanged() {
		sut.notifyApplicationStateChanged();
		
		verify(mock1).applicationStateChanged(state);
		verify(mock2).applicationStateChanged(state);
	}
}