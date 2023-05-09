package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.runconfig.PanoDirectory;
import de.zebrajaeger.sphere2cube.runconfig.PanoSearcher;
import de.zebrajaeger.sphere2cube.util.HashUtils;
import de.zebrajaeger.sphere2cube.util.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args)
      throws IOException, InterruptedException, ParseException, ExecutionException {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    Config config;
    File srcConfigFile = null;

    if (args.length == 0) {
      File src = new File(Defaults.CONFIG_FILE_NAME);
      if (!src.exists()) {
        Config.help();
        System.exit(-1);
        return;
      } else {
        srcConfigFile = src;
        config = Config.of(src);
      }
    } else if (args.length == 1) {
      File src = new File(args[0]);
      if (!src.exists()) {
        Config.help();
        System.exit(-1);
        return;
      } else {
        if (src.isFile()) {
          srcConfigFile = src;
          config = Config.of(src);
        } else if (src.isDirectory()) {

          renderAll(src);
          return;
        } else {
          return;
        }
      }
    } else {
      config = Config.of(args);
    }

    if (config.getSaveConfig().isSaveConfig()) {
      File t = new File(config.getSaveConfig().getSaveConfigTarget());
      FileUtils.forceMkdirParent(t);
      JsonUtils.saveJson(t, config);
    }
    final Sphere2CubeRenderer renderer = new Sphere2CubeRenderer();

    PanoProcessState panoProcessState = renderer.renderPano(
        new File("."),
        config,
        srcConfigFile,
        Defaults.BACKGROUND_COLOR);

    System.out.println(JsonUtils.toJson(panoProcessState));
  }

  /**
   * everything in directory (recursive)
   */
  private static void renderAll(File root)
      throws IOException, ExecutionException, InterruptedException {
    List<PanoDirectory> panoDirectories = PanoSearcher.scanRecursive(root);
    for (PanoDirectory p : panoDirectories) {

      Log.info("Pano Dir: '{}'", p.getRoot().getAbsolutePath());
      boolean rerender = false;
      if (p.getLastRun() == null) {
        Log.debug("No last run: force rendering.");
        rerender = true;
      } else {
        if (!Objects.equals(HashUtils.hashFile(p.getConfigFile()),
            p.getLastRun().getConfigHash())) {
          rerender = true;
          Log.debug("Hast of config doesn't match: force rendering.");
        }

        if (!p.getLastRun().getPanoProcessState().isFinished()) {
          rerender = true;
          Log.debug("Last run unfinished: force rendering.");
        }
      }

      if (rerender) {
        Log.info("Rendering required....");

        final Sphere2CubeRenderer renderer = new Sphere2CubeRenderer();
        renderer.renderPano(
            p.getRoot(),
            p.getConfig(),
            p.getConfigFile(),
            Defaults.BACKGROUND_COLOR);
        // TODO log
      } else {
        Log.info("No rendering required. Ignore.");
        // TODO log
      }
    }
  }

}
