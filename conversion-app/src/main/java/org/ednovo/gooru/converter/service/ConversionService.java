/*******************************************************************************
 * ConversionService.java
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
package org.ednovo.gooru.converter.service;

import java.io.IOException;
import java.util.List;

import org.ednovo.gooru.converter.controllers.Conversion;

public interface ConversionService {

	List<String> resizeImageByDimensions(String srcFilePath, String targetFolderPath, String dimensions, String resourceGooruOid, String sessionToken, String thumbnail, String apiEndPoint);

	void scribdUpload(String apiKey, String dockey, String filepath, String gooruOid, String authXml);

	void convertPdfToImage(String resourceFilePath, String gooruOid, String authXml);

	void resizeImage(String command, String logFile);
	
	String  convertHtmlToPdf(String htmlContent, String targetPath, String sourceHtmlUrl, String filename);
	
	String convertJsonToCsv(String jsonString, String targetFolderPath, String filename);
	
	void resourceImageUpload(String folderInBucket, String gooruBucket , String fileName,  String callBackUrl, String sourceFilePath) throws Exception;
	
	void convertDocumentToPdf(Conversion conversion) throws IOException;

	String convertHtmlToExcel(String htmlContent, String targetPath, String filename);

}
