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

    /**
     * Creates a {@link FileOutputStream} for writing to the output file.
     *
     * @param output the file to be written to by the stream
     *
     * @return the output stream
     *
     * @throws FileNotFoundException if the file doesn't exist, can't be created
     *                               or can't be written to
     */
    public FileOutputStream createFileOutputStream(File output) throws FileNotFoundException {
        return new FileOutputStream(output);
    }

    /**
     * Creates a {@link FileInputStream} for reading from the input file.
     *
     * @param input the file to be read from by the stream
     *
     * @return the input stream
     *
     * @throws FileNotFoundException if the file doesn't exist, can't be opened
     *                               or can't be read from
     */
    public InputStream createFileInputStream(File input) throws FileNotFoundException {
        return new FileInputStream(input);
    }

}
