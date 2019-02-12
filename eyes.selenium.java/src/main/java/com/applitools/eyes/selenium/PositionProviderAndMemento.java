package com.applitools.eyes.selenium;

import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.frames.FrameChain;

public class PositionProviderAndMemento {

    public PositionProvider provider;
    public PositionMemento memento;
    public FrameChain frames;


    public PositionProviderAndMemento(PositionProvider positionProvider, PositionMemento positionMemento, FrameChain frames) {
        provider = positionProvider;
        memento = positionMemento;
        this.frames = frames;
    }


    public void RestoreState() {
        provider.restoreState(memento);
    }

    public PositionProvider getProvider() {
        return provider;
    }

    public void setProvider(PositionProvider provider) {
        this.provider = provider;
    }

    public PositionMemento getMemento() {
        return memento;
    }

    public void setMemento(PositionMemento memento) {
        this.memento = memento;
    }

    public FrameChain getFrames() {
        return frames;
    }

    public void setFrames(FrameChain frames) {
        this.frames = frames;
    }
}

