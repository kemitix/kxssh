package net.kemitix.kxssh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SshIOFactory {

    public FileOutputStream createFileOutputStream(File output) throws FileNotFoundException {
        return new FileOutputStream(output);
    }

}
