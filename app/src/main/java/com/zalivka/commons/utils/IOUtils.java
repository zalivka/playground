package com.zalivka.commons.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.*;

public class IOUtils {
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 32;

    public void copyAssetDirToSd(Context ctx, String assets, String sddir) throws Exception {
        File dir = new File(sddir, assets);
        if (dir.exists()) {
            return;
        }

        AssetManager assetMan = ctx.getAssets();
        String[] files = assetMan.list(assets);

    }

    public static void closeQuietly(final InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void closeQuietly(final OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static long copyFromAssets(final AssetManager assets, final String assetFile, final String output) throws IOException {
        File foutput = new File(output);
        foutput.delete();
        foutput.createNewFile();
        return copyLarge(assets.open(assetFile), new FileOutputStream(foutput));
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static String getFileAsString(String filename) {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            return "";
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = toByteArray(fis);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static void copy(InputStream input, Writer output)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    public static long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
