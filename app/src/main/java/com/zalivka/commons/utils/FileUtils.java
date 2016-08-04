package com.zalivka.commons.utils;

import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.*;

public class FileUtils {
	
	private static final String[] ILLEGAL_CHARACTERS = { "/", "\n", "\r", "\t", "\0", "\f", "`", "?", "*", "\\", "<", ">", "|", "\"", ":" };
	
	public static boolean isGoodFileName(String name) {
		if (TextUtils.isEmpty(name)) {
			return false;
		}
		for (String s : ILLEGAL_CHARACTERS) {
			if (name.contains(s)) {
				return false;
			}
		}
		return true;
	}

    public static String cleanFilename(String raw) {
        for (String s : ILLEGAL_CHARACTERS) {
            raw = raw.replace(s, "_");
        }
        raw = raw.replace(' ', '_');
        return raw;
    }

    public static boolean exists(final String parent, final String child) {
        File file = new File(parent, child);

        return file.exists();
    }

    public static boolean exists(final String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void cleanDirectory(String directory) throws IOException {
    	cleanDirectory(new File(directory));
    }
    
    public static void cleanDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            return;
//            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent){
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);
        if (!directory.delete()) {
            String message =
                "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    public static void moveDirectory(final File srcDir, final File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        }
        if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' is not a directory");
        }
        if (destDir.exists()) {
            throw new IOException("Destination '" + destDir + "' already exists");
        }
        boolean rename = srcDir.renameTo(destDir);
        if (!rename) {
                throw new IOException("Failed to move directory '" + srcDir +
                        "' to '" + destDir + "'");
        }
    }

    public static void moveDirectoryToDirectory(final File src, final File destDir, final boolean createDestDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        }
        if (!destDir.exists() && createDestDir) {
            destDir.mkdirs();
        }
        if (!destDir.exists()) {
            throw new FileNotFoundException("Destination directory '" + destDir +
                    "' does not exist [createDestDir=" + createDestDir +"]");
        }
        if (!destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' is not a directory");
        }
        moveDirectory(src, new File(destDir, src.getName()));

    }

    public static void moveDirectoryContentToDirectory(final File tempDest, final File dest) throws IOException {
        File[] files = tempDest.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    moveDirectoryToDirectory(file, dest, true);
                } else {
                    File parentDir = file.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    moveFileToDirectory(file, dest, true, false);
                }
            }
        }
    }

    public static void moveFile(final File srcFile, final File destFile, final boolean overwrite) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            if (overwrite) {
                forceDelete(destFile);
            } else {
                throw new IOException("Destination '" + destFile + "' already exists");
            }
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            throw new IOException("Failed to delete original file '" + srcFile +"' after copy to '" + destFile + "'");
        }
    }

    public static void moveFileToDirectory(final File srcFile, final File destDir, final boolean createDestDir, final boolean overwrite) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        }
        if (!destDir.exists() && createDestDir) {
            destDir.mkdirs();
        }
        if (!destDir.exists()) {
            throw new FileNotFoundException("Destination directory '" + destDir +
                    "' does not exist [createDestDir=" + createDestDir +"]");
        }
        if (!destDir.isDirectory()) {
            throw new IOException("Destination '" + destDir + "' is not a directory");
        }
        moveFile(srcFile, new File(destDir, srcFile.getName()), overwrite);
    }

    public static void copyFile(final File srcFile, final File destFile, final boolean overwrite) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            if (overwrite) {
                forceDelete(destFile);
            } else {
                throw new IOException("Destination '" + destFile + "' already exists");
            }
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(srcFile);
            os = new FileOutputStream(destFile);
            byte[] data = new byte[4096];
            int read;
            while((read = is.read(data)) != -1) {
                os.write(data, 0, read);
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

	public static void copyFile(String filename, String resFile,
			boolean overwrite) throws IOException {
		copyFile(new File(filename), new File(resFile), overwrite);
	}
	
	public static String getStringFromAssets(final AssetManager assets, final String filename) {
		String res = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open(filename)));
			String line = null;
			while (( line = reader.readLine()) != null){
				res += line;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		return res;
	}
}
