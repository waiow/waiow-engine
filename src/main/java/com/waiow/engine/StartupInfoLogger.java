package com.waiow.engine;

import com.waiow.engine.system.ApplicationHome;
import com.waiow.engine.system.ApplicationPid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.util.concurrent.Callable;

final class StartupInfoLogger {

    private static final Log logger = LogFactory.getLog(StartupInfoLogger.class);
    private static final long HOST_NAME_RESOLVE_THRESHOLD = 200L;
    private final Class<?> sourceClass;

    StartupInfoLogger(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    void logStarting(Log applicationLog) {
        Assert.notNull(applicationLog, "Log must not be null");
        applicationLog.info(LogMessage.of(this::getStartingMessage));
        applicationLog.debug(LogMessage.of(this::getRunningMessage));
    }

    void logSpringStarted(Log applicationLog, Duration timeTakenToStartup) {
        if (applicationLog.isInfoEnabled()) {
            applicationLog.info(this.getSpringStartedMessage(timeTakenToStartup));
        }
    }

    void logEngineStarted(Log applicationLog, Duration timeTakenToStartup) {
        if (applicationLog.isInfoEnabled()) {
            applicationLog.info(this.getEngineStartedMessage(timeTakenToStartup));
        }
    }

    void logLoopStarted(Log applicationLog, Duration timeTakenToStartup) {
        if (applicationLog.isInfoEnabled()) {
            applicationLog.info(this.getLoopStartedMessage(timeTakenToStartup));
        }
    }

    private CharSequence getStartingMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Starting ");
        this.appendApplicationName(message);
        this.appendVersion(message, this.sourceClass);
        this.appendJavaVersion(message);
        this.appendOn(message);
        this.appendPid(message);
        this.appendContext(message);
        return message;
    }

    private CharSequence getRunningMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Running with Waiow Engine");
        this.appendVersion(message, this.getClass());
        message.append(", Spring");
        this.appendVersion(message, ApplicationContext.class);
        return message;
    }

    private CharSequence getSpringStartedMessage(Duration timeTakenToStartup) {
        StringBuilder message = new StringBuilder();
        message.append("Started Spring core in ");
        message.append((double) timeTakenToStartup.toMillis() / 1000.0);
        message.append(" seconds");
        return message;
    }

    private CharSequence getEngineStartedMessage(Duration timeTakenToStartup) {
        StringBuilder message = new StringBuilder();
        message.append("Started Waiow engine in ");
        message.append((double) timeTakenToStartup.toMillis() / 1000.0);
        message.append(" seconds");
        return message;
    }

    private CharSequence getLoopStartedMessage(Duration timeTakenToStartup) {
        StringBuilder message = new StringBuilder();
        message.append("Started ");
        this.appendApplicationName(message);
        message.append(" in TOTAL ");
        message.append((double) timeTakenToStartup.toMillis() / 1000.0);
        message.append(" seconds");

        try {
            double uptime = (double) ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
            message.append(" (JVM running for ").append(uptime).append(")");
        } catch (Throwable var5) {
        }

        message.append("\n Starting the Loop...");
        return message;
    }

    private void appendApplicationName(StringBuilder message) {
        String name = this.sourceClass != null ? ClassUtils.getShortName(this.sourceClass) : "application";
        message.append(name);
    }

    private void appendVersion(StringBuilder message, Class<?> source) {
        this.append(message, "v", () -> {
            return source.getPackage().getImplementationVersion();
        });
    }

    private void appendOn(StringBuilder message) {
        long startTime = System.currentTimeMillis();
        this.append(message, "on ", () -> {
            return InetAddress.getLocalHost().getHostName();
        });
        long resolveTime = System.currentTimeMillis() - startTime;
        if (resolveTime > 200L) {
            logger.warn(LogMessage.of(() -> {
                StringBuilder warning = new StringBuilder();
                warning.append("InetAddress.getLocalHost().getHostName() took ");
                warning.append(resolveTime);
                warning.append(" milliseconds to respond.");
                warning.append(" Please verify your network configuration");
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    warning.append(" (macOS machines may need to add entries to /etc/hosts)");
                }

                warning.append(".");
                return warning;
            }));
        }

    }

    private void appendPid(StringBuilder message) {
        this.append(message, "with PID ", ApplicationPid::new);
    }

    private void appendContext(StringBuilder message) {
        StringBuilder context = new StringBuilder();
        ApplicationHome home = new ApplicationHome(this.sourceClass);
        if (home.getSource() != null) {
            context.append(home.getSource().getAbsolutePath());
        }

        this.append(context, "started by ", () -> {
            return System.getProperty("user.name");
        });
        this.append(context, "in ", () -> {
            return System.getProperty("user.dir");
        });
        if (context.length() > 0) {
            message.append(" (");
            message.append(context);
            message.append(")");
        }

    }

    private void appendJavaVersion(StringBuilder message) {
        this.append(message, "using Java ", () -> {
            return System.getProperty("java.version");
        });
    }

    private void append(StringBuilder message, String prefix, Callable<Object> call) {
        this.append(message, prefix, call, "");
    }

    private void append(StringBuilder message, String prefix, Callable<Object> call, String defaultValue) {
        Object result = this.callIfPossible(call);
        String value = result != null ? result.toString() : null;
        if (!StringUtils.hasLength(value)) {
            value = defaultValue;
        }

        if (StringUtils.hasLength(value)) {
            message.append(message.length() > 0 ? " " : "");
            message.append(prefix);
            message.append(value);
        }

    }

    private Object callIfPossible(Callable<Object> call) {
        try {
            return call.call();
        } catch (Exception var3) {
            return null;
        }
    }

}
