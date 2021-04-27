package de.zebrajaeger.sphere2cube.runconfig;

import de.zebrajaeger.sphere2cube.FileUtils;
import de.zebrajaeger.sphere2cube.Stringable;
import de.zebrajaeger.sphere2cube.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class PanoDirectory extends Stringable {
    public static final String DEFAULT_CONFIG_FILENAME = "sphere2cube.json";
    public static final String DEFAULT_DIR_FILENAME = ".sphere2cube";
    public static final String DEFAULT_DIR_RUN_FILENAME = "lastrun.json";

    private Config config;
    private File configFile;
    private String configHash;

    private RunConfig runConfig;
    private File runConfigFile;


    public static PanoDirectory of(File directory) throws IOException {
        PanoDirectory result = new PanoDirectory();

        File[] rootFiles = directory.listFiles();
        if (rootFiles != null) {

            Optional<File> configFile = Arrays.stream(rootFiles).filter(file -> DEFAULT_CONFIG_FILENAME.equals(file.getName())).findFirst();
            if (configFile.isPresent()) {
                result.config = Config.of(configFile.get());
                result.configHash = FileUtils.hashFile(configFile.get());
            }

            // hidden config directory
            Optional<File> runConfigDir = Arrays.stream(rootFiles).filter(file -> DEFAULT_DIR_FILENAME.equals(file.getName())).findFirst();
            if (runConfigDir.isPresent()) {
                File[] runConfigDirFiles = runConfigDir.get().listFiles();
                if (runConfigDirFiles != null) {
                    Optional<File> lastRun = Arrays.stream(runConfigDirFiles).filter(file -> DEFAULT_DIR_RUN_FILENAME.equals(file.getName())).findFirst();
                    if (lastRun.isPresent()) {
                        result.runConfig = RunConfig.of(lastRun.get());
                        result.runConfigFile = lastRun.get();
                    }
                }
            }

        }
        return result;
    }

    public boolean isPanoDir() {
        return config != null;
    }

    public boolean matchesHash() {
        if (runConfig != null && runConfig.getConfigHash() != null) {
            return runConfig.getConfigHash().equals(configHash);
        }
        return false;
    }

    public Config getConfig() {
        return config;
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getConfigHash() {
        return configHash;
    }

    public RunConfig getRunConfig() {
        return runConfig;
    }

    public File getRunConfigFile() {
        return runConfigFile;
    }
}
