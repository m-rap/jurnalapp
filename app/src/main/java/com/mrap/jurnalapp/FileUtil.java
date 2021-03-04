package com.mrap.jurnalapp;

import java.io.File;

public class FileUtil {
    public boolean deleteRecursive(File f) {
        if (!f.isDirectory()) {
            return f.delete();
        }

        File[] files = f.listFiles();
        for (File child : files) {
            if (!deleteRecursive(child)) {
                return false;
            }
        }

        return f.delete();
    }
}
