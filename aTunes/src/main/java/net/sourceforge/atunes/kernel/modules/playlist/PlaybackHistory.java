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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

class PlaybackHistory {

    static class Heap {

        private List<AudioObject> heap = new ArrayList<AudioObject>();

        AudioObject pop() {
            if (heap.isEmpty()) {
                return null;
            }
            AudioObject ao = heap.get(heap.size() - 1);
            heap.remove(heap.size() - 1);
            return ao;
        }

        void push(AudioObject ao) {
            heap.add(ao);
        }

        AudioObject get(int index) {
            int position = heap.size() - index;
            if (!heap.isEmpty() && position >= 0 && position < heap.size()) {
                return heap.get(position);
            }
            return null;
        }

        void remove(AudioObject ao) {
            // Remove all occurrences
            while (heap.contains(ao)) {
                heap.remove(ao);
            }
        }

        void clear() {
            heap.clear();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (AudioObject ao : heap) {
                sb.append(ao).append(" <- ");
            }
            sb.append("]");
            return sb.toString();
        }

    }

    private Heap previousHeap = new Heap();

    private AudioObject currentAudioObject;

    private Heap nextHeap = new Heap();

    AudioObject getPreviousInHistory(int index) {
        return previousHeap.get(index);
    }

    AudioObject getNextInHistory(int index) {
        return nextHeap.get(index);
    }

    AudioObject moveToPreviousInHistory() {
        AudioObject ao = previousHeap.pop();
        if (ao != null) {
            if (currentAudioObject != null) {
                nextHeap.push(currentAudioObject);
            }
            currentAudioObject = ao;
            return ao;
        }
        return null;
    }

    AudioObject moveToNextInHistory() {
        AudioObject ao = nextHeap.pop();
        if (ao != null) {
            if (currentAudioObject != null) {
                previousHeap.push(currentAudioObject);
                currentAudioObject = ao;
            }
            return ao;
        }
        return null;
    }

    void addToHistory(AudioObject audioObject) {
        if (currentAudioObject != null && currentAudioObject != audioObject) {
            if (previousHeap.get(1) == audioObject) {
                currentAudioObject = previousHeap.pop();
            } else if (nextHeap.get(1) == audioObject) {
                currentAudioObject = nextHeap.pop();
            } else {
                previousHeap.push(currentAudioObject);
                currentAudioObject = audioObject;
            }
        }
    }

    void remove(List<AudioObject> audioObjectsToRemove) {
        for (AudioObject ao : audioObjectsToRemove) {
            previousHeap.remove(ao);
            nextHeap.remove(ao);
        }
    }

    void clear() {
        previousHeap.clear();
        nextHeap.clear();
        currentAudioObject = null;
    }

    @Override
    public String toString() {
        return StringUtils.getString(previousHeap.toString(), currentAudioObject, nextHeap.toString());
    }

}
