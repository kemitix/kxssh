package net.kemitix.kxssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Factory to create {@link FileInputStream}s and {@link FileOutputStream}s for
 * a given {@link File}.
 *
 * @author pcampbell
 */
public class SshIOFactory {

    public FileOutputStream createFileOutputStream(File output) throws FileNotFoundException {
        return new FileOutputStream(output);
    }

    public InputStream createFileInputStream(File input) throws FileNotFoundException {
        return new FileInputStream(input);
    }

}
