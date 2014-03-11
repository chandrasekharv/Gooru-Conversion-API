/*******************************************************************************
 * GooruImageUtil.java
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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

@Component
public class GooruImageUtil implements ConversionAppConstants{

	private Map<String,String> propertyMap;
	
	private static final Logger logger = LoggerFactory.getLogger(GooruImageUtil.class);

	public static boolean scaleImage(String srcFilePath, String targetFolderPath, String... dimensions) {
		try {

			ByteArrayInputStream sourceImageStream = getByteArrayInputStream(srcFilePath);
			String filenamePrefix = StringUtils.substringAfterLast(srcFilePath, "/");
			for (String dimension : dimensions) {
				String[] xy = dimension.split(X);
				int width = Integer.valueOf(xy[0]);
				int height = Integer.valueOf(xy[1]);
				File thumbnailFile = new File(targetFolderPath + "/" + filenamePrefix + "-" + dimension + "." + getFileExtenstion(srcFilePath));
				scaleImage(sourceImageStream, width, height, thumbnailFile);
			}
			return true;
		} catch (Exception ex) {
			logger.warn("Multiple scaling of image failed : ", ex);
			return false;
		}
	}

	public boolean resizeImageByDimensions(String srcFilePath, String targetFolderPath, String dimensions, String resourceGooruOid, String sessionToken, String thumbnail) {
		return resizeImageByDimensions(srcFilePath, targetFolderPath, null, dimensions, resourceGooruOid, sessionToken, thumbnail);
	}

	public boolean resizeImageByDimensions(String srcFilePath, String targetFolderPath, String filenamePrefix, String dimensions, String resourceGooruOid, String sessionToken, String thumbnail) {
		try {
			logger.debug(" src : {} /= target : {}", srcFilePath , targetFolderPath);
			String[] imageDimensions = dimensions.split(",");
			if (filenamePrefix == null) {
				filenamePrefix = StringUtils.substringAfterLast(srcFilePath, "/");
				if (filenamePrefix.contains(".")) {
					filenamePrefix = StringUtils.substringBeforeLast(filenamePrefix, ".");
				}
			}

			for (String dimension : imageDimensions) {
				String[] xy = dimension.split(X);
				int width = Integer.valueOf(xy[0]);
				int height = Integer.valueOf(xy[1]);
				resizeImageByDimensions(srcFilePath, width, height, targetFolderPath + filenamePrefix + "-" + dimension + "." + getFileExtenstion(srcFilePath));
			}
			
		    try {
			    HttpClient client = new HttpClient();
			    HttpMethod method = new PutMethod("http://"+propertyMap.get(SERVER_PATH) + "/" + propertyMap.get(REST_END_POINT)+ "/media/resource/thumbnail?sessionToken="+sessionToken+"&resourceGooruOid="+resourceGooruOid+"&thumbnail="+thumbnail);
			    client.executeMethod(method);
		    }
		    catch (Exception ex){
		    	logger.error("something went wrong while making rest api call", ex);
		    }

			return true;
		} catch (Exception ex) {
			logger.error("Multiple scaling of image failed for src : " + srcFilePath + " : ", ex);
			return false;
		}
	}
	
	public void resizeImageByDimensions(String srcFilePath, int width, int height, String destFilePath) throws Exception {
		try {
			logger.debug(" src : {} /= target : {}", srcFilePath , destFilePath);
			File destFile = new File(destFilePath);
			if (new File(srcFilePath).exists() && destFile.exists()) {
				destFile.delete();
			}
			scaleImageUsingImageMagick(srcFilePath, width, height, destFilePath);
		} catch (Exception ex) {
				logger.error("Error while scaling image", ex);
				throw ex;
		}
	}
	
	public void scaleImageUsingImageMagick(String srcFilePath, int width, int height, String destFilePath) throws Exception {
		try{
			String resizeCommand = new String( "/usr/bin/gm@convert@" + srcFilePath + "@-resize@"+ width + X + height + "@" +destFilePath);
			String cmdArgs[] = resizeCommand.split("@");
			Process thumsProcess = Runtime.getRuntime().exec(cmdArgs);
			thumsProcess.waitFor();
	
			String line;
			StringBuffer sb = new StringBuffer();
			
			BufferedReader in = new BufferedReader(
		               new InputStreamReader(thumsProcess.getInputStream()) );
		       while ((line = in.readLine()) != null) {
		         sb.append(line).append("\n");
		       }
		       in.close();
		       
		    logger.info("output : {} -  Status : {} - Command : "+StringUtils.join(cmdArgs," ") ,sb.toString() , thumsProcess.exitValue() + "" );

	} catch (Exception e) {
		logger.error("something went wrong while converting image", e);
	}

	}

	
	public static void cropImage(String path, int x, int y, int width, int height) throws Exception {
		BufferedImage srcImg = ImageIO.read(new File(path));
		srcImg = srcImg.getSubimage(x, y, width, height);
		ImageIO.write(srcImg, PNG, new File(path));
	}

	public static BufferedImage scaleImage(BufferedImage originalImage, int width, int height) throws Exception {
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		BufferedImage rescaledImage = resampleOp.filter(originalImage, null);
		return rescaledImage;
	}

	public static boolean scaleImage(String srcFilePath, int width, int height, File destFile) {
		try {
			scaleImage(getByteArrayInputStream(srcFilePath), width, height, destFile);
			return true;
		} catch (Exception ex) {
			logger.warn("Scaling of image failed : ", ex);
			return false;
		}
	}

	public static void scaleImage(ByteArrayInputStream sourceImageStream, int width, int height, File destFile) throws Exception {
		ByteArrayOutputStream thumbnailBaos = new ByteArrayOutputStream();
		Thumbnails.of(sourceImageStream).forceSize(width, height).outputFormat(PNG).toOutputStream(thumbnailBaos);
		FileUtils.writeByteArrayToFile(destFile, thumbnailBaos.toByteArray());
	}

	public static String downloadWebResourceToFile(String srcUrl, String outputFolderPath, String fileNamePrefix) {
		return downloadWebResourceToFile(srcUrl, outputFolderPath, fileNamePrefix, null);
	}

	public static String downloadWebResourceToFile(String srcUrl, String outputFolderPath, String fileNamePrefix, String fileExtension) {

		if (srcUrl == null || outputFolderPath == null || fileNamePrefix == null) {
			return null;
		}

		try {
			File outputFolder = new File(outputFolderPath);
			URL url = new URL(srcUrl);
			URLConnection urlCon = url.openConnection();
			InputStream inputStream = urlCon.getInputStream();

			if (!outputFolder.exists()) {
				outputFolder.mkdirs();
			}

			if (fileExtension == null) {
				fileExtension = getWebFileExtenstion(urlCon.getContentType());
			}
			String destFilePath = outputFolderPath + fileNamePrefix + "."+ fileExtension;

			File outputFile = new File(destFilePath);

			OutputStream out = new FileOutputStream(outputFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			inputStream.close();

			return destFilePath;
		} catch (Exception e) {
			logger.warn("DownloadImage failed:exception:", e);
			return null;
		}
	}

	public static ByteArrayInputStream getByteArrayInputStream(String srcFilePath) throws Exception {
		byte[] sourceFileData = FileUtils.readFileToByteArray(new File(srcFilePath));
		return new ByteArrayInputStream(sourceFileData);
	}

	public static String getWebFileExtenstion(String contentType) {

		if (contentType.equalsIgnoreCase(IMG_BMP))
			return BMP;
		else if (contentType.equalsIgnoreCase(IMG_PNG))
			return PNG;
		else if (contentType.equalsIgnoreCase(IMG_GIF))
			return GIF;
		else
			return JPG;

	}
	
	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public static String getFileExtenstion(String filePath) {

		return StringUtils.substringAfterLast(filePath, ".");

	}

	public static String getFileName(String filePath) {
		return StringUtils.substringAfterLast(filePath, "/");
	}

}
