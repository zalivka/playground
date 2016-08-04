package com.zalivka.commons.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ALL")
public class ZipUtils {

	private static final String TAG = "ZipUtils";

	/**
	 * Creates your object from an input stream
	 */
	public static interface ICreator {
		Object create(InputStream is);
	}

	public static Object fromZip(final String zipfile, final String entryname,
			ICreator creator) throws IOException {
		ZipFile zip = new ZipFile(zipfile);
		Object obj = null;
		Enumeration entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry e = (ZipEntry) entries.nextElement();
			if (e.getName().equalsIgnoreCase(entryname)) {
				InputStream io = zip.getInputStream(e);
				obj = creator.create(io);
				io.close();
			}
		}
		return obj;
	}

    public static Object fromZip(final InputStream zipstream, final String entryname,
                                 ICreator creator) throws IOException {
        ZipInputStream zis = new ZipInputStream(zipstream);
        Object obj = null;
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().equalsIgnoreCase(entryname)) {
                    obj = creator.create(zis);
                }
            }
        } finally {
            zis.close();
        }
        return obj;
    }

    public static Set<String> findWithExtension(final String zipfile, final String ext) throws IOException {
		Set<String> filesExt = new HashSet<String>();
		ZipFile zip = new ZipFile(zipfile);
		Enumeration entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry e = (ZipEntry) entries.nextElement();
			if (e.getName().endsWith(ext)) {
				filesExt.add(e.getName());
			}
		}
		return filesExt;
	}

	public static Set<String> findWithExtension(final InputStream zipstream, final String ext) throws IOException {
		Set<String> filesExt = new HashSet<String>();
		ZipInputStream zis = new ZipInputStream(zipstream);
		try {
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().endsWith(ext)) {
					filesExt.add(ze.getName());
				}
			}
		} finally {
			zis.close();
		}
		return filesExt;
	}
	
	public static Object fromZip(final File zipfile,
			final String entryname, ICreator creator) throws IOException {
		return fromZip(zipfile.getAbsolutePath(), entryname, creator);
	}
	
	public static Bitmap getBitmap(final File zipfile, final String entryname, final BitmapFactory.Options opts) throws IOException {
		return (Bitmap) ZipUtils.fromZip(zipfile, entryname, new ZipUtils.ICreator() {

			@Override
			public Object create(InputStream is) {
				return BitmapFactory.decodeStream(is, null, opts);
			}
		});
	}

	public static Bitmap getBitmap(final File zipfile, final String entryname) throws IOException {
		return (Bitmap) ZipUtils.fromZip(zipfile, entryname, new ZipUtils.ICreator() {

			@Override
			public Object create(InputStream is) {
				return BitmapFactory.decodeStream(is);
			}
		});
	}

    public static Bitmap getBitmap(final InputStream is, final String entryname) throws IOException {
        return (Bitmap) ZipUtils.fromZip(is, entryname, new ZipUtils.ICreator() {

            @Override
            public Object create(InputStream is) {
                return BitmapFactory.decodeStream(is);
            }
        });
    }

	public static Bitmap getBitmap(final InputStream is, final String entryname, final BitmapFactory.Options opts) throws IOException {
		return (Bitmap) ZipUtils.fromZip(is, entryname, new ZipUtils.ICreator() {

			@Override
			public Object create(InputStream is) {
				return BitmapFactory.decodeStream(is, null, opts);
			}
		});
	}
	
	public static Bitmap getBitmap(final String zipfile, final String entryname) throws IOException {
		return getBitmap(new File(zipfile), entryname);
	}

	public static Bitmap getBitmap(final String zipfile, final String entryname, final BitmapFactory.Options opts) throws IOException {
		return getBitmap(new File(zipfile), entryname, opts);
	}
	
	public static ByteArrayOutputStream fetchFileAsByteArray(String archName,
			String fileName) throws IOException {
		ByteArrayOutputStream bos = (ByteArrayOutputStream) ZipUtils.fromZip(
				archName, fileName, new ICreator() {

					@Override
					public Object create(InputStream is) {
						ByteArrayOutputStream b = new ByteArrayOutputStream();
						try {
							IOUtils.copy(is, b);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return b;
					}
				});
		return bos;
	}

    public static ByteArrayOutputStream fetchFileAsByteArray(InputStream is,
                                                             String fileName) throws IOException {
        ByteArrayOutputStream bos = (ByteArrayOutputStream) ZipUtils.fromZip(
                is, fileName, new ICreator() {

            @Override
            public Object create(InputStream is) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                try {
                    IOUtils.copy(is, b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return b;
            }
        });
        return bos;
    }
	
	public static File unpack(String archName, final String nameInArch, final String destDir) throws IOException {
		final File destFile = new File(destDir, nameInArch);
		ZipUtils.fromZip(archName, nameInArch, new ICreator() {

			@Override
			public Object create(InputStream is) {
				try {
					destFile.delete();
					destFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(destFile);
					IOUtils.copy(is, fos);
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				return destFile;
			}
		});

		if (!destFile.exists())
			throw new IOException("Cannot unpack file " + nameInArch + " from ZIP " + archName);

		return destFile;
	}

	public static File unpack(InputStream inputStream, final String nameInArch, final String destDir) throws IOException {
		final File destFile = new File(destDir, nameInArch);
		ZipUtils.fromZip(inputStream, nameInArch, new ICreator() {

			@Override
			public Object create(InputStream is) {
				try {
					destFile.delete();
					destFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(destFile);
					IOUtils.copy(is, fos);
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				return destFile;
			}
		});

		if (!destFile.exists())
			throw new IOException("Cannot unpack file " + nameInArch + " from stream");

		return destFile;
	}
	
	public static boolean hasFile(String zipfile, String fname) throws IOException {
		ZipFile zip = new ZipFile(zipfile);
		return zip.getEntry(fname) != null;
	}

    public static void unpackAll(String filename, String targetDir) throws IOException {
		new File(targetDir).mkdirs();
        ZipFile zip = new ZipFile(filename);
        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = (ZipEntry) entries.nextElement();
            File file = new File(targetDir, e.getName());
            if (e.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                ByteArrayOutputStream bos = ZipUtils.fetchFileAsByteArray(filename, e.getName());
                IOUtils.copy(new ByteArrayInputStream(bos.toByteArray()), fos);
                IOUtils.closeQuietly(fos);
            }
        }
    }
}
