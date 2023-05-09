package de.zebrajaeger.sphere2cube.runconfig;

import de.zebrajaeger.sphere2cube.util.Stringable;
import de.zebrajaeger.sphere2cube.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class PanoDirectory extends Stringable {
    public static final String DEFAULT_CONFIG_FILENAME = "sphere2cube.json";
    public static final String DEFAULT_DIR_FILENAME = ".sphere2cube";
    public static final String DEFAULT_DIR_RUN_FILENAME = "lastrun.json";

    private File root;

    private File configFile;
    private Config config;

    private File lastRunFile;
    private LastRun lastRun;

    public static PanoDirectory of(File directory) throws IOException {

        PanoDirectory result = new PanoDirectory();
        result.root = directory;

        File[] rootFiles = directory.listFiles();
        if (rootFiles != null) {

            Optional<File> configFile = Arrays.stream(rootFiles).filter(file -> DEFAULT_CONFIG_FILENAME.equals(file.getName())).findFirst();
            if (configFile.isPresent()) {
                result.config = Config.of(configFile.get());
                result.configFile = configFile.get();
            }

            // hidden config directory
            Optional<File> runConfigDir = Arrays.stream(rootFiles).filter(file -> DEFAULT_DIR_FILENAME.equals(file.getName())).findFirst();
            if (runConfigDir.isPresent()) {
                File[] runConfigDirFiles = runConfigDir.get().listFiles();
                if (runConfigDirFiles != null) {
                    Optional<File> lastRun = Arrays.stream(runConfigDirFiles).filter(file -> DEFAULT_DIR_RUN_FILENAME.equals(file.getName())).findFirst();
                    if (lastRun.isPresent()) {
                        result.lastRun = LastRun.of(lastRun.get());
                        result.lastRunFile = lastRun.get();
                    }
                }
            }

        }
        return result;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Config getConfig() {
        return config;
    }

    public File getLastRunFile() {
        return lastRunFile;
    }

    public LastRun getLastRun() {
        return lastRun;
    }

    public File getRoot() {
        return root;
    }
}
