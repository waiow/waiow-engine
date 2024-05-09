package com.waiow.engine;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public final class WaiowEngineBanner implements Banner {

    private static final String[] BANNER = new String[]{
            "\n" +
                    " █     █░ ▄▄▄       ██▓ ▒█████   █     █░\n" +
                    "▓█░ █ ░█░▒████▄    ▓██▒▒██▒  ██▒▓█░ █ ░█░\n" +
                    "▒█░ █ ░█ ▒██  ▀█▄  ▒██▒▒██░  ██▒▒█░ █ ░█ \n" +
                    "░█░ █ ░█ ░██▄▄▄▄██ ░██░▒██   ██░░█░ █ ░█ \n" +
                    "░░██▒██▓  ▓█   ▓██▒░██░░ ████▓▒░░░██▒██▓ \n" +
                    "░ ▓░▒ ▒   ▒▒   ▓▒█░░▓  ░ ▒░▒░▒░ ░ ▓░▒ ▒  \n" +
                    "  ▒ ░ ░    ▒   ▒▒ ░ ▒ ░  ░ ▒ ▒░   ▒ ░ ░  \n" +
                    "  ░   ░    ░   ▒    ▒ ░░ ░ ░ ▒    ░   ░  \n" +
                    "    ░          ░  ░ ░      ░ ░      ░    \n" +
                    "                                         \n"};
    private static final String WAIOW_ENGINE = " :: Waiow Engine :: ";
    private static final int STRAP_LINE_SIZE = 37;

    WaiowEngineBanner() {
    }

    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        String[] var4 = BANNER;
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String line = var4[var6];
            printStream.println(line);
        }

        String version = WaiowEngineVersion.getVersion();
        version = " (v" + version + ")";
        StringBuilder padding = new StringBuilder();

        while (padding.length() < STRAP_LINE_SIZE - (version.length() + WAIOW_ENGINE.length())) {
            padding.append(" ");
        }

        printStream.println(AnsiOutput.toString(new Object[]{AnsiColor.YELLOW, WAIOW_ENGINE, AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT, version}));
        printStream.println();
    }

}
