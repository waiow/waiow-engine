package com.waiow.engine.graphic;

public final class FrameRate {

    private short FPS;
    private double LastTimeCounter;
    private double lastTimeRender;
    private short frameCount;
    private double period;

    public FrameRate(int frequency) {
        this.period = 1000000000D / (double) frequency;
    }

    public void init() {
        this.LastTimeCounter = System.nanoTime();
        this.lastTimeRender = System.nanoTime();
        this.FPS = 0;
    }

    public void calculate() {
        double currentTime = System.nanoTime();
        frameCount++;
        if (currentTime - LastTimeCounter > 1000000000) {
            LastTimeCounter = currentTime;
            FPS = frameCount;
            frameCount = 0;
        }
    }

    public boolean shouldRender() {
        double currentTime = System.nanoTime();
        if (currentTime - lastTimeRender > period) {
            lastTimeRender = currentTime;
            return true;
        }
        return false;
    }

    public int getFPS() {
        return FPS;
    }
}
