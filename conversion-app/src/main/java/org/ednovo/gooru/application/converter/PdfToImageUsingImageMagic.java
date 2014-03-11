/*******************************************************************************
 * PdfToImageUsingImageMagic.java
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfToImageUsingImageMagic implements ConversionAppConstants {

	private static final Logger logger = LoggerFactory.getLogger(PdfToImageUsingImageMagic.class);

	public List<String> process(String resourcePath, String destPath, String fileNamePrefix, String imageType, String gooruResourceOid, String authXMl) throws Exception {

		List<String> images = new ArrayList<String>();

		String[] convertPdfFirstSlideCommand = new String[] { "/usr/bin/convert", "-quality", "100", "-density", "100x100", "-trim", resourcePath + "[0]", "+adjoin", "-scene", "1", destPath + "/" + fileNamePrefix + "%d." + imageType };

		String[] convertPdfToImageSlideCommand = new String[] { "/usr/bin/convert", "-quality", "100", "-density", "100x100", "-trim", resourcePath, "+adjoin", "-scene", "1", destPath + "/" + fileNamePrefix + "%d." + imageType };

		File dir = new File(destPath);
		if (!dir.exists())
			dir.mkdir();
		try {

			logger.debug("Starting PDF slide generation using ImageMagic ........." + new Date());
			Integer status = null;
			Process slideProcess = null;
			if (destPath.contains(SLIDES)) {

				slideProcess = Runtime.getRuntime().exec(convertPdfFirstSlideCommand);

			} else {

				slideProcess = Runtime.getRuntime().exec(convertPdfToImageSlideCommand);

			}
			status = slideProcess.waitFor();
			logger.debug("Completed PDF slide generation using ImageMagic with status : " + status + " ........." + new Date());

		} catch (Exception e) {
			e.printStackTrace();
		}

		String files;
		File folder = new File(destPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.contains(SLIDES)) {
					images.add(listOfFiles[i].getPath());
				}
			}
		}

		return images;
	}

}
