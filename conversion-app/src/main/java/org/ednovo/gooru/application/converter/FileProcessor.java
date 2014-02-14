package org.ednovo.gooru.application.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileProcessor {

	public static String getFileName(String fileName) {
		return fileName.substring(0, fileName.indexOf("."));
	}

	public static String getParentPath(String path) {
		String parentPath = "";
		if (path != null) {
			String currentPath = "";
			for (int i = 0; i < path.length(); i++) {
				currentPath += path.charAt(i);
				if (path.charAt(i) == '/') {
					parentPath += currentPath;
					currentPath = "";
				}
			}
		}
		return parentPath.substring(0, parentPath.length() - 1);
	}

	public static String getPathFileName(String path) {
		if (path != null) {
			String currentPath = "";
			for (int i = 0; i < path.length(); i++) {
				currentPath += path.charAt(i);
				if (path.charAt(i) == '/') {
					currentPath = "";
				}
			}
			if (currentPath.length() > 0) {
				return currentPath;
			}
		}
		return null;
	}

	public static File writeFile(String path, String fileName, byte[] data) {

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}

		File resourceFile = new File(path + "/" + fileName);

		try {
			OutputStream out = new FileOutputStream(resourceFile);
			out.write(data);
			out.close();
		} catch (IOException e) {

		}
		return resourceFile;
	}

	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.indexOf(".") + 1);
	}

}
