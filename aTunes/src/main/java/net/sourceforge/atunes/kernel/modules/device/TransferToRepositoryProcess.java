/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.device;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.AbstractAudioFileTransferProcess;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;

public class TransferToRepositoryProcess extends AbstractAudioFileTransferProcess {

    public TransferToRepositoryProcess(List<ILocalAudioObject> files, IState state, IFrame frame, IOSManager osManager) {
        super(files, state, frame, osManager);
    }

    @Override
    public String getProgressDialogTitle() {
        return I18nUtils.getString("COPYING_TO_REPOSITORY");
    }

    @Override
    protected String getDestination() {
        return RepositoryHandler.getInstance().getRepositoryPath();
    }
}
