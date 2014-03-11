/*******************************************************************************
 * PdfToImagePedelRenderer.java
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jpedal.PdfDecoder;
import org.jpedal.fonts.FontMappings;

public class PdfToImagePedelRenderer implements ConversionAppConstants {
	static BufferedImage image;

	public List<String> process(String resourcePath, String destPath, String fileNamePrefix, String imageType) throws Exception {
		PdfDecoder decode_pdf = new PdfDecoder(true);

		/** set mappings for non-embedded fonts to use */
		FontMappings.setFontReplacements();

		File dir = new File(destPath + SLASH_SLIDES);
		if (!dir.exists())
			dir.mkdir();

		/** open the PDF file - can also be a URL or a byte array */
		decode_pdf.openPdfFile(resourcePath);
		/** get page 1 as an image */
		// page range if you want to extract all pages with a loop
		int start = 1, pageCount = decode_pdf.getPageCount();
		List<String> imagePaths = new ArrayList<String>();
		for (int pageIndex = start; pageIndex <= pageCount; pageIndex++) {
			BufferedImage image = decode_pdf.getPageAsImage(pageIndex);
			File srcFile = new File(destPath + File.separator + fileNamePrefix + pageIndex + "." + imageType);
			ImageIO.write(image, PNG, srcFile);
			imagePaths.add(srcFile.getPath());

			BufferedImage thumbnailImage = GooruImageUtil.scaleImage(image, 80, 60);
			ImageIO.write(thumbnailImage, PNG, new File(destPath + File.separator + THUMBNAIL + pageIndex + "." + imageType));

		}

		/** close the pdf file */
		decode_pdf.closePdfFile();
		return imagePaths;
	}
}
