package org.ednovo.gooru.application.converter;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfToImagePdfviewRenderer implements ConversionAppConstants{

	public List<String> process(String resourcePath, String destPath, String fileNamePrefix, String imageType) throws Exception {

		File pdfFile = new File(resourcePath);

		List<String> images = new ArrayList<String>();

		RandomAccessFile raf = new RandomAccessFile(pdfFile, R);
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdffile = new PDFFile(buf);

		File dir = new File(destPath);
		if (!dir.exists())
			dir.mkdir();

		int numPgs = pdffile.getNumPages();

		for (int pageIndex = 0; pageIndex < numPgs; pageIndex++) {
			PDFPage page = pdffile.getPage(pageIndex + 1);

			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

			Image img = page.getImage(rect.width, rect.height, rect, null, true, true);

			BufferedImage bImg = toBufferedImage(img);
			File destImageFile = new File(destPath + "/"+fileNamePrefix + (pageIndex + 1) + "." + imageType);
			ImageIO.write(bImg, PNG, destImageFile);
			images.add(destImageFile.getPath());
			BufferedImage thumbnailImage = GooruImageUtil.scaleImage(bImg, 80, 60);
			ImageIO.write(thumbnailImage, PNG, new File(destPath + File.separator + THUMBNAIL + (pageIndex + 1) + "." + imageType));
		}
		return images;
	}

	public BufferedImage toBufferedImage(Image image) throws Exception {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		int transparency = Transparency.OPAQUE;
		int type = BufferedImage.TYPE_INT_RGB;
		boolean hasAlpha = hasAlpha(image);
		if (hasAlpha) {
			transparency = Transparency.BITMASK;
			type = BufferedImage.TYPE_INT_ARGB;
		}

		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		if (bimage == null) {
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		Graphics2D g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	public boolean hasAlpha(Image image) throws Exception {
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		pg.grabPixels();
		ColorModel cm = pg.getColorModel();

		return cm.hasAlpha();
	}

	public static String getResourceNum(int id) {
		String prefix = TRIPLE_ZERO;
		return prefix.substring(0, 3 - String.valueOf(id).length()) + id;
	}

}
