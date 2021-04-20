package de.zebrajaeger.sphere2cube.viewer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ViewerUtils.class);

    public static EmbeddedFile download(URL url, File targetFolder) throws IOException {
        String name = FilenameUtils.getName(url.getFile());
        File localFile = new File(targetFolder, name);
        LOG.info("Download url '{}' to '{}'", url, localFile.getAbsolutePath());
        FileUtils.copyURLToFile(url, localFile);
        return new EmbeddedFile(localFile, name);
    }

    public static List<EmbeddedFile> download(Collection<URL> urls, File targetFolder) throws IOException {
        List<EmbeddedFile> result = new ArrayList<>(urls.size());
        for (URL url : urls) {
            result.add(download(url, targetFolder));
        }
        return result;
    }
}
