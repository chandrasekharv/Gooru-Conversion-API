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