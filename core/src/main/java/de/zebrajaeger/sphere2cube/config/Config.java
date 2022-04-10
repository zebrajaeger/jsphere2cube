package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.JsonUtils;
import de.zebrajaeger.sphere2cube.Stringable;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Config extends Stringable {
    public static final String SPHERE_2_CUBE_SCHEMA_JSON = "sphere2cube.schema.json";

    private boolean debug;
    @JsonIgnore
    private File configFile = null;
    @JsonIgnore
    private SaveConfig saveConfig = new SaveConfig();
    @JsonProperty("source")
    private InputConfig inputConfig = new InputConfig();
    @JsonProperty("target")
    private String outputFolder = "build";
    @JsonProperty("preview")
    PreviewsConfig previewsConfig = new PreviewsConfig();
    @JsonProperty("cubemap")
    CubeMapConfig cubeMapConfig = new CubeMapConfig();
    @JsonProperty("viewer")
    ViewerConfig viewerConfig = new ViewerConfig();
    @JsonProperty("archive")
    ArchiveConfig archiveConfig = new ArchiveConfig();

    public static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("\n  sphere2cube <nothing> | <config-file> | <options>  \nOptions:", createOptions());
    }

    public static Config of(File configFile) throws IOException {
        return JsonUtils.loadJson(configFile, Config.class, SPHERE_2_CUBE_SCHEMA_JSON);
    }

    public static Config of(String[] args) throws ParseException, IOException {
        Config config = new Config();

        Options options = createOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // debug
        if (cmd.hasOption("d")) {
            config.setDebug(cmd.hasOption("d"));
        }

        // common
        if (cmd.hasOption("sc")) {
            config.getSaveConfig().setSaveConfig(cmd.hasOption("sc"));
        }
        if (cmd.hasOption("sct")) {
            config.getSaveConfig().setSaveConfigTarget(cmd.getOptionValue("sct"));
        }

        // target
        if (cmd.hasOption("t")) {
            config.setOutputFolder(cmd.getOptionValue("t"));
        }

        // source
        config.getInputConfig().setInputImageFile(cmd.getOptionValue("s"));
        if (cmd.hasOption("w")) {
            config.getInputConfig().setInputImageHorizontalAngel(Double.parseDouble(cmd.getOptionValue("w")));
        }

        // Preview - CubeMap
        if (cmd.hasOption("pc")) {
            config.getPreviewsConfig().getCubeMapPreview().setEnabled(cmd.hasOption("pc"));
        }
        if (cmd.hasOption("pce")) {
            config.getPreviewsConfig().getCubeMapPreview().setEdge(Integer.parseInt(cmd.getOptionValue("pce")));
        }
        if (cmd.hasOption("pct")) {
            config.getPreviewsConfig().getCubeMapPreview().setTarget(cmd.getOptionValue("pct"));
        }

        // Preview - Equirectangular
        if (cmd.hasOption("pe")) {
            config.getPreviewsConfig().getEquirectangularPreview().setEnabled(cmd.hasOption("pe"));
        }
        if (cmd.hasOption("pee")) {
            config.getPreviewsConfig().getEquirectangularPreview().setEdge(Integer.parseInt(cmd.getOptionValue("pee")));
        }
        if (cmd.hasOption("pet")) {
            config.getPreviewsConfig().getEquirectangularPreview().setTarget(cmd.getOptionValue("pet"));
        }

        // Preview - Scaled
        if (cmd.hasOption("ps")) {
            config.getPreviewsConfig().getScaledPreview().setEnabled(cmd.hasOption("ps"));
        }
        if (cmd.hasOption("pse")) {
            config.getPreviewsConfig().getScaledPreview().setEdge(Integer.parseInt(cmd.getOptionValue("pse")));
        }
        if (cmd.hasOption("psm")) {
            config.getPreviewsConfig().getScaledPreview().setMaxSize(Integer.parseInt(cmd.getOptionValue("psm")));
        }
        if (cmd.hasOption("pst")) {
            config.getPreviewsConfig().getScaledPreview().setTarget(cmd.getOptionValue("pst"));
        }

        // Cube faces
        if (cmd.hasOption("cf")) {
            config.getCubeMapConfig().getFaces().setEnabled(cmd.hasOption("cf"));
        }
        if (cmd.hasOption("cft")) {
            config.getCubeMapConfig().getFaces().setTarget(cmd.getOptionValue("cft"));
        }

        if (cmd.hasOption("ct")) {
            config.getCubeMapConfig().getTiles().setEnabled(cmd.hasOption("ct"));
        }
        if (cmd.hasOption("ctt")) {
            config.getCubeMapConfig().getTiles().setTarget(cmd.getOptionValue("ctt"));
        }
        if (cmd.hasOption("cte")) {
            config.getCubeMapConfig().getTiles().setTileEdge(Integer.parseInt(cmd.getOptionValue("ctt")));
        }

        // viewer
        if (cmd.hasOption("vp")) {
            config.getViewerConfig().getPannellum().setEnabled(cmd.hasOption("vp"));
        }
        if (cmd.hasOption("vpt")) {
            config.getViewerConfig().getPannellum().setTarget(cmd.getOptionValue("vpt"));
        }

        // Archive
        if (cmd.hasOption("a")) {
            config.getArchiveConfig().setEnabled(cmd.hasOption("a"));
        }
        if (cmd.hasOption("at")) {
            config.getArchiveConfig().setTarget(cmd.getOptionValue("at"));
        }

        return config;
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();

        // debug
        options.addOption("d", "debug", false, "Render debug infos. Default: false");

        // common
        options.addOption("sc", "save-config", false, "Store config");
        options.addOption("sct", "save-config-target", true, "Store config target");

        // target
        options.addOption("t", "target", true, "Target folder. Default: '.'");

        // source
        options.addRequiredOption("s", "source", true, "Source image");
        options.addOption("w", "width", true, "Horizontal angel of source image. Default is 360.0");

        // Preview - CubeMap
        options.addOption("pc", "preview-cube", false, "Render cube preview image. Default true");
        options.addOption("pce", "preview-cube-edge", true, "Edge size of cube preview image.");
        options.addOption("pct", "preview-cube-target", true, "Path of cube-preview image.");

        // Preview - Equirectangular
        options.addOption("pe", "preview-equirectangular", false, "Render equirectangular preview image. Default true");
        options.addOption("pee", "preview-equirectangular-edge", true, "Edge size of equirectangular preview image.");
        options.addOption("pet", "preview-equirectangular-target", true, "Path of equirectangular-preview image.");

        // Preview - Scaled
        options.addOption("ps", "preview-scaled", false, "Render scaled preview image. Default true");
        options.addOption("pse", "preview-scaled-edge", true, "Edge size of scaled preview image.");
        options.addOption("psm", "preview-scaled-maxsize", true, "Maximum files size of scaled preview image.");
        options.addOption("pst", "preview-scaled-target", true, "Path of scaled-preview image.");

        // Cube faces
        options.addOption("cf", "cube-faces", false, "Render cube faces");
        // TODO add template options
        options.addOption("cft", "cube-faces-target", true, "Cube faces image path template. ");

        options.addOption("ct", "cube-tiles", false, "Render cube tiles");
        // TODO add template options
        options.addOption("ctt", "cube-tiles-target", true, "Cube tiles image path template.");
        options.addOption("cte", "cube-tiles-edge", true, "Cube tiles edge size.");

        // Viewer
        options.addOption("vp", "viewer-pannellum", false, "Render pannellum html file.");
        options.addOption("vpt", "viewer-pannellum-target", true, "Pannellum target file path.");

        // Archive
        options.addOption("a", "archive", false, "Create archive.");
        options.addOption("at", "archive-target", true, "Archive target file path.");

        return options;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public PreviewsConfig getPreviewsConfig() {
        return previewsConfig;
    }

    public void setPreviewsConfig(PreviewsConfig previewsConfig) {
        this.previewsConfig = previewsConfig;
    }

    public InputConfig getInputConfig() {
        return inputConfig;
    }

    public void setInputConfig(InputConfig inputConfig) {
        this.inputConfig = inputConfig;
    }

    public CubeMapConfig getCubeMapConfig() {
        return cubeMapConfig;
    }

    public void setCubeMapConfig(CubeMapConfig cubeMapConfig) {
        this.cubeMapConfig = cubeMapConfig;
    }

    public ViewerConfig getViewerConfig() {
        return viewerConfig;
    }

    public void setViewerConfig(ViewerConfig viewerConfig) {
        this.viewerConfig = viewerConfig;
    }

    public SaveConfig getSaveConfig() {
        return saveConfig;
    }

    public void setSaveConfig(SaveConfig saveConfig) {
        this.saveConfig = saveConfig;
    }

    public ArchiveConfig getArchiveConfig() {
        return archiveConfig;
    }

    public void setArchiveConfig(ArchiveConfig archiveConfig) {
        this.archiveConfig = archiveConfig;
    }
}
