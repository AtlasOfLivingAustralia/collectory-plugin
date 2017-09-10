package au.org.ala.util

import org.apache.commons.io.IOUtils

/**
 * Test utilities mixin
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * @copyright Copyright (c) 2017 CSIRO
 */
trait TestUtil {
    /**
     * Read a UTF-8 resource file into a string
     *
     * @param resource The resource path, relative to the current class
     *
     * @return The resource as a string
     */
    def resourceAsString(String resource) {
        def reader = new InputStreamReader(this.class.getResourceAsStream(resource), "UTF-8")
        def writer = new StringWriter()
        IOUtils.copy(reader, writer)
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

    /**
     * Copy an internal resource to an external temporary file.
     * <p>
     * The created file is cleaned up on exit
     *
     * @param resource The resource path, relative to the current class
     *
     * @return A temporary file containing the resource
     */
    File copyToTempFile(String resource) {
        File tmp = File.createTempFile("test", resource)
        tmp.deleteOnExit()
        def is = this.class.getResourceAsStream(resource)
        def os = new FileOutputStream(tmp)
        IOUtils.copy(is, os)
        os.close()
        is.close()
        return tmp
    }
}
