package com.susion.boring.music.event;

import com.susion.boring.music.model.Song;

/**
 * Created by susion on 17/3/6.
 */
public class SongDeleteFromPlayQueueEvent {
    public SongDeleteFromPlayQueueEvent(Song song) {
        this.song = song;
    }

    public Song song;
}
