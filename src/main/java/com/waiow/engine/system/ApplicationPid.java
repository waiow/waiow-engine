package com.waiow.engine.system;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class ApplicationPid {

    private static final Log logger = LogFactory.getLog(ApplicationPid.class);
    private static final PosixFilePermission[] WRITE_PERMISSIONS;
    private static final long JVM_NAME_RESOLVE_THRESHOLD = 200L;
    private final String pid;

    public ApplicationPid() {
        this.pid = this.getPid();
    }

    protected ApplicationPid(String pid) {
        this.pid = pid;
    }

    private String getPid() {
        try {
            String jvmName = this.resolveJvmName();
            return jvmName.split("@")[0];
        } catch (Throwable var2) {
            return null;
        }
    }

    private String resolveJvmName() {
        long startTime = System.currentTimeMillis();
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 200L) {
            logger.warn(LogMessage.of(() -> {
                StringBuilder warning = new StringBuilder();
                warning.append("ManagementFactory.getRuntimeMXBean().getName() took ");
                warning.append(elapsed);
                warning.append(" milliseconds to respond.");
                warning.append(" This may be due to slow host name resolution.");
                warning.append(" Please verify your network configuration");
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    warning.append(" (macOS machines may need to add entries to /etc/hosts)");
                }

                warning.append(".");
                return warning;
            }));
        }

        return jvmName;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof ApplicationPid ? ObjectUtils.nullSafeEquals(this.pid, ((ApplicationPid) obj).pid) : false;
        }
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.pid);
    }

    public String toString() {
        return this.pid != null ? this.pid : "???";
    }

    public void write(File file) throws IOException {
        Assert.state(this.pid != null, "No PID available");
        this.createParentDirectory(file);
        if (file.exists()) {
            this.assertCanOverwrite(file);
        }

        FileWriter writer = new FileWriter(file);
        Throwable var3 = null;

        try {
            writer.append(this.pid);
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if (writer != null) {
                if (var3 != null) {
                    try {
                        writer.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    writer.close();
                }
            }

        }

    }

    private void createParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

    }

    private void assertCanOverwrite(File file) throws IOException {
        if (!file.canWrite() || !this.canWritePosixFile(file)) {
            throw new FileNotFoundException(file.toString() + " (permission denied)");
        }
    }

    private boolean canWritePosixFile(File file) throws IOException {
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file.toPath());
            PosixFilePermission[] var3 = WRITE_PERMISSIONS;
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                PosixFilePermission permission = var3[var5];
                if (permissions.contains(permission)) {
                    return true;
                }
            }

            return false;
        } catch (UnsupportedOperationException var7) {
            return true;
        }
    }

    static {
        WRITE_PERMISSIONS = new PosixFilePermission[]{PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE};
    }

}
