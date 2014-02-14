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