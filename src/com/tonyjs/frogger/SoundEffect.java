package com.tonyjs.frogger;

import javafx.scene.media.AudioClip;

/**
 * Created by tonysaavedra on 6/26/16.
 */
public class SoundEffect {
    private AudioClip soundEffect;

    public SoundEffect(String filePath) {
        soundEffect = new AudioClip(getClass().getResource(filePath).toExternalForm());
    }

    public void playClip() {
        soundEffect.play();
    }
}
