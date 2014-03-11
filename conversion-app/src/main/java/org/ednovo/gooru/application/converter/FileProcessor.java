/*******************************************************************************
 * FileProcessor.java
 *  conversion-app
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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
