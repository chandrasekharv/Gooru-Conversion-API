package org.ednovo.gooru.converter.service;

import java.util.List;

public interface ConversionService {

	List<String> resizeImageByDimensions(String srcFilePath, String targetFolderPath, String dimensions, String resourceGooruOid, String sessionToken, String thumbnail, String apiEndPoint);

	void scribdUpload(String apiKey, String dockey, String filepath, String gooruOid, String authXml);

	void convertPdfToImage(String resourceFilePath, String gooruOid, String authXml);

	void resizeImage(String command, String logFile);
	
	String  convertHtmlToPdf(String htmlContent, String targetPath, String sourceHtmlUrl, String filename);
	
	String convertJsonToCsv(String jsonString, String targetFolderPath, String filename);
}
