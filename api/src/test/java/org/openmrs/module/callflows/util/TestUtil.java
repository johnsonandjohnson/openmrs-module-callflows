package com.janssen.connectforlife.callflows.util;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test Util
 *
 * @author bramak09
 */
public final class TestUtil {

    private TestUtil() {

    }

    public static String loadFile(String filename) throws IOException {
        try (InputStream in = TestUtil.class.getClassLoader().getResourceAsStream(filename)) {
            return IOUtils.toString(in).replace("\r\n", "\n");
        }
    }
}
