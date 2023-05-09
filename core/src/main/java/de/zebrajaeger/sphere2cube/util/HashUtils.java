package de.zebrajaeger.sphere2cube.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestException;
import java.security.MessageDigest;

public class HashUtils {
    public static String hashFile(@NotNull File f) throws IOException {
        MessageDigest digest = DigestUtils.getSha3_256Digest();
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(f))) {
            byte[] buffer = new byte[1024];
            int l;
            try {
                while ((l = is.read(buffer)) > 0) {
                    digest.digest(buffer, 0, l);
                }
            } catch (DigestException e) {
                throw new IOException("Has error", e);
            }
        }
        return Hex.encodeHexString(digest.digest());
    }
}
