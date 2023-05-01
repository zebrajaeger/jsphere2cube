package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.panodescription.PanoDescription;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;


@Slf4j
public class Sphere2CubeUi extends JFrame implements DropTargetListener {

  public static void main(String[] args) {
    Sphere2CubeUi app = new Sphere2CubeUi();
    app.setVisible(true);
  }

  public Sphere2CubeUi() {
    setTitle("PSD File Drag and Drop");
    setSize(200, 200);
    setPreferredSize(new Dimension(400, 400));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setAlwaysOnTop(true);

    new DropTarget(this, DnDConstants.ACTION_COPY, this);
  }

  @Override
  public void dragEnter(DropTargetDragEvent e) {
    checkAcceptance(e);
  }

  @Override
  public void dragExit(DropTargetEvent dte) {
  }

  @Override
  public void dragOver(DropTargetDragEvent e) {
  }


  @Override
  public void drop(DropTargetDropEvent e) {
    e.acceptDrop(DnDConstants.ACTION_COPY);
    List<File> files = getFilesFromEvent(e);
    if (files.size() == 1 && isFileSupported(files.get(0))) {
      File file = files.get(0);
      try {
        processPanoImage(file);
      } catch (IOException | ExecutionException | InterruptedException ex) {
        log.error("Could not render pano: {}", file.getAbsolutePath(), ex);
      }
    } else {
      e.dropComplete(false);
    }
  }

  private void processPanoImage(File panoImageFile)
      throws IOException, ExecutionException, InterruptedException {
    final String name = FileNameUtils.getBaseName(panoImageFile.getName());
    File configFile = new File( panoImageFile.getParentFile(),name + ".config.json");
    Config config;

    if (configFile.exists()) {
      // provided
      config = Config.of(configFile);
    } else {

      // default
      InputStream configStream = Objects.requireNonNull(
          Config.class.getResourceAsStream("sphere2cube.default.json"),
          "sphere2cube.default.json not found");
      config = Config.of(IOUtils.toString(configStream, StandardCharsets.UTF_8));

      config.getInputConfig().setInputImageFile("./" + panoImageFile.getName());
      config.setOutputFolder(FileNameUtils.getBaseName(panoImageFile.getName()));

      //create config file
      JsonUtils.saveJson(configFile, config);
    }

    File descriptionFile = new File(panoImageFile.getParentFile(), name + ".description.json");
    if (!descriptionFile.exists()) {
      PanoDescription panoDescription = new PanoDescription();
      String title = FileNameUtils.getBaseName(panoImageFile.getName());
      panoDescription.setTitle(title);
      panoDescription.setDescription(title);
      JsonUtils.saveJson(descriptionFile, panoDescription);
    }

    final Sphere2CubeRenderer renderer = new Sphere2CubeRenderer();

    PanoProcessState panoProcessState = renderer.renderPano(
        panoImageFile.getParentFile(),
        config,
        configFile,
        Defaults.BACKGROUND_COLOR);

    System.out.println(JsonUtils.toJson(panoProcessState));
  }

private Path getRelativePath(File baseFile, File fullFile){
  Path basePath = baseFile.toPath();
  Path absolutePath = fullFile.toPath();
  return basePath.relativize(absolutePath).normalize();
}

  @Override
  public void dropActionChanged(DropTargetDragEvent dtde) {
  }

  private void checkAcceptance(DropTargetDragEvent e) {
    if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
      List<File> files = getFilesFromEvent(e);
      if (files.size() == 1 && isFileSupported(files.get(0))) {
        e.acceptDrag(DnDConstants.ACTION_COPY);
        return;
      }
    }
    e.rejectDrag();
  }

  private boolean isFileSupported(File file) {
    String fileName = file.getName().toLowerCase();
//    return fileName.endsWith(".jpg");
    return fileName.endsWith(".psd") || fileName.endsWith(".psb");
  }

  private List<File> getFilesFromEvent(DropTargetEvent e) {
    List<File> files = new ArrayList<>();
    try {
      Transferable transferable = null;
      if (DropTargetDragEvent.class.equals(e.getClass())) {
        transferable = ((DropTargetDragEvent) e).getTransferable();
      }
      if (DropTargetDropEvent.class.equals(e.getClass())) {
        transferable = ((DropTargetDropEvent) e).getTransferable();
      }

      if (transferable != null && transferable.isDataFlavorSupported(
          DataFlavor.javaFileListFlavor)) {
        @SuppressWarnings("unchecked")
        List<File> fileList = (List<File>) transferable.getTransferData(
            DataFlavor.javaFileListFlavor);
        for (File file : fileList) {
          if (file.isFile()) {
            files.add(file);
          }
        }
      }
    } catch (UnsupportedFlavorException | IOException ex) {
      ex.printStackTrace();
    }
    return files;
  }
}
