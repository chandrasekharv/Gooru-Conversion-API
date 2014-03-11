/*******************************************************************************
 * PdfToImageRenderer.java
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

import java.util.List;


public class PdfToImageRenderer implements ConversionAppConstants{

	public List<String> process(String resourcePath, String destPath, boolean isSplit, String imageType,  String gooruResourceOid, String authXml) throws Exception {
		if (!isSplit) {
			destPath += SLASH_SLIDES;
		} else {
			destPath += SLASH_TEMP_ASSETS;
		}
		String fileNamePrefix = null;
		try {
			if (!isSplit) {
				fileNamePrefix = SLIDES;
			} else {
				fileNamePrefix = FileProcessor.getFileName(FileProcessor.getPathFileName(resourcePath)) + "_";
			}
		} catch (Exception e) {
			return null;
		}

		return new PdfToImageUsingImageMagic().process(resourcePath, destPath, fileNamePrefix, imageType, gooruResourceOid, authXml);

	}
}
