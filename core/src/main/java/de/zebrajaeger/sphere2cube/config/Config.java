package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Config {
    private boolean debug;
    @JsonIgnore
    private SaveConfig saveConfig = new SaveConfig();

    @JsonProperty("source")
    private InputConfig inputConfig = new InputConfig();

    @JsonProperty("target")
    private File outputFolder = new File(".");

    @JsonProperty("preview")
    PreviewsConfig previewsConfig = new PreviewsConfig();

    @JsonProperty("cubemap")
    CubeMapConfig cubeMapConfig = new CubeMapConfig();

    @JsonProperty("viewer")
    ViewerConfig viewerConfig = new ViewerConfig();

    public static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("\n  sphere2cube <nothing> | <config-file> | <options>  \nOptions:", createOptions());
    }

    public static Config of(File configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(configFile, Config.class);
    }

    public static Config of(String[] args) throws ParseException, IOException {
        Config config = new Config();

        Options options = createOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // debug
        config.setDebug(cmd.hasOption("d"));

        // common
        config.getSaveConfig().setSaveConfig(cmd.hasOption("sc"));
        if (cmd.hasOption("sct")) {
            config.getSaveConfig().setSaveConfigTarget(cmd.getOptionValue("sct"));
        }

        // target
        if (cmd.hasOption("t")) {
            config.setOutputFolder(new File(cmd.getOptionValue("t")).getCanonicalFile());
        } else {
            config.setOutputFolder(new File(".").getCanonicalFile());
        }

        // source
        config.getInputConfig().setInputImageFile(new File(cmd.getOptionValue("s")));
        if (cmd.hasOption("w")) {
            config.getInputConfig().setInputImageHorizontalAngel(Double.parseDouble(cmd.getOptionValue("w")));
        }

        // Preview - CubeMap
        config.getPreviewsConfig().getCubeMapPreview().setEnabled(cmd.hasOption("pc"));
        if (cmd.hasOption("pce")) {
            config.getPreviewsConfig().getCubeMapPreview().setEdge(Integer.parseInt(cmd.getOptionValue("pce")));
        }
        if (cmd.hasOption("pct")) {
            config.getPreviewsConfig().getCubeMapPreview().setTarget(cmd.getOptionValue("pct"));
        }

        // Preview - Equirectangular
        config.getPreviewsConfig().getEquirectangularPreview().setEnabled(cmd.hasOption("pe"));
        if (cmd.hasOption("pee")) {
            config.getPreviewsConfig().getEquirectangularPreview().setEdge(Integer.parseInt(cmd.getOptionValue("pee")));
        }
        if (cmd.hasOption("pet")) {
            config.getPreviewsConfig().getEquirectangularPreview().setTarget(cmd.getOptionValue("pet"));
        }

        // Preview - Scaled
        config.getPreviewsConfig().getScaledPreview().setEnabled(cmd.hasOption("ps"));
        if (cmd.hasOption("pse")) {
            config.getPreviewsConfig().getScaledPreview().setEdge(Integer.parseInt(cmd.getOptionValue("pse")));
        }
        if (cmd.hasOption("pst")) {
            config.getPreviewsConfig().getScaledPreview().setTarget(cmd.getOptionValue("pst"));
        }

        // Cube faces
        config.getCubeMapConfig().getFaces().setEnabled(cmd.hasOption("cf"));
        if (cmd.hasOption("cft")) {
            config.getCubeMapConfig().getFaces().setTarget(cmd.getOptionValue("cft"));
        }

        config.getCubeMapConfig().getTiles().setEnabled(cmd.hasOption("ct"));
        if (cmd.hasOption("ctt")) {
            config.getCubeMapConfig().getTiles().setTarget(cmd.getOptionValue("ctt"));
        }
        if (cmd.hasOption("cte")) {
            config.getCubeMapConfig().getTiles().setTileEdge(Integer.parseInt(cmd.getOptionValue("ctt")));
        }

        // viewer
        config.getViewerConfig().getPannellum().setEnabled(cmd.hasOption("vp"));
        if (cmd.hasOption("vpt")) {
            config.getViewerConfig().getPannellum().setTarget(cmd.getOptionValue("vpt"));
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
        options.addOption("pst", "preview-scaled-target", true, "Path of scaled-preview image.");

        // Cube faces
        options.addOption("cf", "cube-faces", false, "Render cube faces");
        // TODO add template options
        options.addOption("cft", "cube-faces-target", true, "Cube faces image path template. ");

        options.addOption("ct", "cube-tiles", false, "Render cube tiles");
        // TODO add template options
        options.addOption("ctt", "cube-tiles-target", true, "Cube tiles image path template.");
        options.addOption("cte", "cube-tiles-edge", true, "Cube tiles edge size.");

        // viewer
        options.addOption("vp", "viewer-pannellum", false, "Render pannellum html file.");
        options.addOption("vpt", "viewer-pannellum-target", false, "Pannellum target file path.");
        return options;
    }

    public String toJson() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
        previewsConfig.setOutputFolder(outputFolder);
        cubeMapConfig.setOutputFolder(outputFolder);
        viewerConfig.setOutputFolder(outputFolder);
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
}
