package au.org.ala.util

import jline.internal.InputStreamReader

/**
 * Test utilities mixin
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
class TestUtil {
    /**
     * Read a resource file into a string
     *
     * @param resource The resource path, relative to the
     *
     * @return The resource as a string
     */
    def resourceAsString(String resource) {
        def reader = new InputStreamReader(this.class.getResourceAsStream(resource))
        def writer = new StringWriter()
        def buffer = new char[1024]
        def n
        while ((n = reader.read(buffer)) >= 0) {
            writer.write(buffer, 0, n)
            Thread.yield()
        }
        return writer.toString()
    }

    /**
     * Remove a directory and (recursively) it's contents.
     * <p>
     * This also works on individual files.
     *
     * @param dir The directory to remove
     *
     */
    def removeDir(File dir) {
        if (dir.isDirectory()) {
            for (File entry : dir.listFiles()) {
                removeDir(entry)
            }
        }
        dir.delete()
    }
}
