package com.waiow.engine;

import com.waiow.engine.graphic.AbstractPanel;
import com.waiow.engine.graphic.FrameRate;
import com.waiow.engine.graphic.Window;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.util.Optional;

public class WaiowEngine extends SpringApplication implements Runnable {

    private static final Log log = LogFactory.getLog(WaiowEngine.class);
    private final String[] args;
    private boolean logStartupInfo;
    private Class<?> mainApplicationClass;
    private ConfigurableApplicationContext context;
    private FrameRate frameRate;
    private Window window;

    public WaiowEngine(Class<?> primarySource, String... args) {
        super(primarySource);
        super.setBanner(new WaiowEngineBanner());
        super.setLogStartupInfo(false);
        this.args = args;
        this.logStartupInfo = true;
        this.mainApplicationClass = primarySource;
    }

    @Override
    public void run() {
        final long startTime = System.nanoTime();

        this.context = runSpring();
        logSpringStartupDetails(this.context, startTime);

        runEngine();
        logEngineStartupDetails(startTime);
    }

    private ConfigurableApplicationContext runSpring() {
        return super.run(this.args);
    }

    private void runEngine() {
        this.frameRate = createFrameRateInstance();
        this.window = createWindowInstance();
    }

    private FrameRate createFrameRateInstance() {
        return new FrameRate(
                Integer.parseInt(
                        this.context.getEnvironment().getProperty("pixel.engine.frame-rate", "60")
                )
        );
    }

    private Window createWindowInstance() {
        final String title = this.context.getEnvironment().getProperty("pixel.window.title", this.mainApplicationClass.getSimpleName());
        final boolean undecorated = this.context.getEnvironment().getProperty("pixel.window.undecorated", Boolean.class, false);

        if (this.context.getEnvironment().getProperty("pixel.window.fullscreen", Boolean.class, false)) {
            return new Window(title, undecorated);
        }

        final Integer width = this.context.getEnvironment().getProperty("pixel.window.width", Integer.class, 400);
        final Integer height = this.context.getEnvironment().getProperty("pixel.window.height", Integer.class, 400);

        return new Window(title, undecorated, width, height);
    }

    private void logSpringStartupDetails(ConfigurableApplicationContext context, long startTime) {
        if (this.logStartupInfo) {
            this.logStartupInfo(context.getParent() == null);
            this.logStartupProfileInfo(context);
            this.logSpringStarted(getTimeTakenToStartup(startTime));
        }
    }

    private void logSpringStarted(Duration timeTakenToStartup) {
        (new StartupInfoLogger(this.mainApplicationClass))
                .logSpringStarted(this.getApplicationLog(), timeTakenToStartup);
    }

    private void logEngineStartupDetails(long startTime) {
        if (this.logStartupInfo) {
            this.logEngineStarted(getTimeTakenToStartup(startTime));
        }
    }

    private void logEngineStarted(Duration timeTakenToStartup) {
        (new StartupInfoLogger(this.mainApplicationClass))
                .logEngineStarted(this.getApplicationLog(), timeTakenToStartup);
    }

    private static Duration getTimeTakenToStartup(long startTime) {
        return Duration.ofNanos(System.nanoTime() - startTime);
    }
}
