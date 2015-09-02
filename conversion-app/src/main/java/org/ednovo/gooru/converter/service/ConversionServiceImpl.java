/*******************************************************************************
 * ConversionServiceImpl.java
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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.ednovo.gooru.application.converter.ConversionAppConstants;
import org.ednovo.gooru.application.converter.GooruImageUtil;
import org.ednovo.gooru.application.converter.PdfToImageRenderer;
import org.ednovo.gooru.converter.controllers.Conversion;
import org.ednovo.gooru.kafka.KafkaProducer;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.code.javascribd.connection.ScribdClient;
import com.google.code.javascribd.connection.StreamableData;
import com.google.code.javascribd.docs.Upload;
import com.google.code.javascribd.type.Access;
import com.google.code.javascribd.type.ApiKey;
import com.google.code.javascribd.type.FileData;

import flexjson.JSONSerializer;

@Service
public class ConversionServiceImpl implements ConversionService, ConversionAppConstants {

	private static final Logger logger = LoggerFactory.getLogger(GooruImageUtil.class);

	@Autowired
	@javax.annotation.Resource(name = "contentS3Service")
	private RestS3Service s3Service;

	@Autowired
	private KafkaProducer kafkaProducer;

	@Override
	public List<String> resizeImageByDimensions(String srcFilePath, String targetFolderPath, String dimensions, String resourceGooruOid, String sessionToken, String thumbnail, String apiEndPoint) {
		return resizeImageByDimensions(srcFilePath, targetFolderPath, null, dimensions, resourceGooruOid, sessionToken, thumbnail, apiEndPoint);
	}

	@Override
	public void scribdUpload(String apiKey, String dockey, String filepath, String gooruOid, String authXml) {
		ScribdClient client = new ScribdClient();

		ApiKey apikey = new ApiKey(apiKey);

		File file = new File(filepath);

		StreamableData uploadData = new FileData(file);

		// initialize upload method
		Upload upload = new Upload(apikey, uploadData);
		upload.setDocType(PDF);
		upload.setAccess(Access.PRIVATE);
		upload.setRevId(new Integer(dockey));

		try {
			client.execute(upload);
		} catch (Exception e) {
			logger.info("$ Textbook upload failed ", e);
		}

		try {
			File srcFile = new File(filepath);

			new PdfToImageRenderer().process(filepath, srcFile.getParent(), false, JPG, gooruOid, authXml);

		} catch (Exception ex) {
			logger.info("$ Generation of pdf slides failed ", ex);
		}
	}

	@Override
	public void resizeImage(String command, String logFile) {
		logger.info("Runtime Executor .... Initializing...");
		try {
			command = StringUtils.replace(command, "/usr/bin/convert", "/usr/bin/gm@convert");
			String cmdArgs[] = applyTemporaryFixForPath(command.split("@"));
			Process thumsProcess = Runtime.getRuntime().exec(cmdArgs);
			thumsProcess.waitFor();

			String line;
			StringBuffer sb = new StringBuffer();

			BufferedReader in = new BufferedReader(new InputStreamReader(thumsProcess.getInputStream()));
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\n");
			}
			in.close();

			logger.info("output : {} -  Status : {} - Command : " + StringUtils.join(cmdArgs, " "), sb.toString(), thumsProcess.exitValue() + "");

			if (logFile != null) {
				FileUtils.writeStringToFile(new File(logFile), "Completed");
			}
		} catch (Exception e) {
			logger.error("something went wrong while converting image", e);
		}

	}

	private static String[] applyTemporaryFixForPath(String[] cmdArgs) {
		if (cmdArgs.length > 0) {
			String[] fileTypes = { ".jpeg", ".JPEG", ".jpg", ".JPG", ".png", ".PNG", ".gif", ".GIF" };
			for (int argIndex = 0; argIndex < cmdArgs.length; argIndex++) {
				String part = cmdArgs[argIndex];
				if (containsIgnoreCaseInArray(part, fileTypes)) {
					String filePath = null;
					String repoPath = StringUtils.substringBeforeLast(part, "/");
					String remainingPart = StringUtils.substringAfter(part, "/");
					for (String fileType : fileTypes) {
						filePath = StringUtils.substringAfter(remainingPart, fileType);
						if (!filePath.isEmpty()) {
							break;
						}
					}

					if (!filePath.isEmpty()) {
						if (!repoPath.isEmpty()) {
							filePath = repoPath + "/" + filePath;
						}
						cmdArgs[argIndex] = filePath;
					}
				}
			}
		}
		return cmdArgs;
	}

	private static boolean containsIgnoreCaseInArray(String haystack, String[] containsArray) {
		for (String key : containsArray) {
			if (haystack.contains(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void convertPdfToImage(String resourceFilePath, String gooruOid, String authXml) {
		logger.warn("$ Processing  pdf slides conversion request for : " + resourceFilePath);

		try {
			File srcFile = new File(resourceFilePath);

			new PdfToImageRenderer().process(resourceFilePath, srcFile.getParent(), false, JPG, gooruOid, authXml);

		} catch (Exception ex) {
			logger.info("$ Generation of pdf slides failed ", ex);
		}
	}

	public List<String> resizeImageByDimensions(String srcFilePath, String targetFolderPath, String filenamePrefix, String dimensions, String resourceGooruOid, String sessionToken, String thumbnail, String apiEndPoint) {
		String imagePath = null;
		List<String> list = new ArrayList<String>();
		try {
			logger.debug(" src : {} /= target : {}", srcFilePath, targetFolderPath);
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
				imagePath = resizeImageByDimensions(srcFilePath, width, height, targetFolderPath + filenamePrefix + "-" + dimension + "." + getFileExtenstion(srcFilePath));

				list.add(imagePath);
			}

		} catch (Exception ex) {
			logger.error("Multiple scaling of image failed for src : " + srcFilePath + " : ", ex);
		}

		return list;
	}

	public String resizeImageByDimensions(String srcFilePath, int width, int height, String destFilePath) throws Exception {
		String imagePath = null;
		try {
			logger.debug(" src : {} /= target : {}", srcFilePath, destFilePath);
			File destFile = new File(destFilePath);
			
			scaleImageUsingImageMagick(srcFilePath, width, height, destFilePath);
			imagePath = destFile.getPath();
		} catch (Exception ex) {
			logger.error("Error while scaling image", ex);
			throw ex;
		}

		return imagePath;
	}

	public void scaleImageUsingImageMagick(String srcFilePath, int width, int height, String destFilePath) throws Exception {
		try {
			String resizeCommand = new String("/usr/bin/gm@convert@" + srcFilePath + "@-resize@" + width + X + height + "@" + destFilePath);
			String cmdArgs[] = resizeCommand.split("@");
			Process thumsProcess = Runtime.getRuntime().exec(cmdArgs);
			thumsProcess.waitFor();

			String line;
			StringBuffer sb = new StringBuffer();

			BufferedReader in = new BufferedReader(new InputStreamReader(thumsProcess.getInputStream()));
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\n");
			}
			in.close();

			logger.info("output : {} -  Status : {} - Command : " + StringUtils.join(cmdArgs, " "), sb.toString(), thumsProcess.exitValue() + "");

		} catch (Exception e) {
			logger.error("something went wrong while converting image", e);
		}

	}

	@Override
	public String convertHtmlToPdf(String htmlContent, String targetPath, String sourceHtmlUrl, String filename) {
		File targetDir = new File(targetPath);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		if (filename == null) {
			filename = String.valueOf(System.currentTimeMillis());
		} else {
			File file = new File(targetPath + filename + DOT_PDF);
			if (file.exists()) {
				filename = filename + "-" + System.currentTimeMillis();
			}
		}
		if (htmlContent != null) {
			if (saveAsHtml(targetPath + filename + DOT_HTML, htmlContent) != null) {
				convertHtmlToPdf(targetPath + filename + DOT_HTML, targetPath + filename + DOT_PDF, 0);
				File file = new File(targetPath + filename + DOT_HTML);
				file.delete();
				return filename + DOT_PDF;
			}
		} else if (sourceHtmlUrl != null) {
			convertHtmlToPdf(sourceHtmlUrl, targetPath + filename + DOT_PDF, 30000);
			return filename + DOT_PDF;
		}

		return null;
	}

	private static String saveAsHtml(String fileName, String content) {
		File file = new File(fileName);
		try {
			FileOutputStream fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void convertHtmlToPdf(String srcFileName, String destFileName, long delayInMillsec) {
		try {
			String resizeCommand = new String("/usr/bin/wkhtmltopdf@" + srcFileName + "@--redirect-delay@" + delayInMillsec + "@" + destFileName);
			String cmdArgs[] = resizeCommand.split("@");
			Process thumsProcess = Runtime.getRuntime().exec(cmdArgs);
			thumsProcess.waitFor();

		} catch (Exception e) {
			logger.error("something went wrong while converting pdf", e);
		}
	}

	public static String getFileExtenstion(String filePath) {

		return StringUtils.substringAfterLast(filePath, ".");

	}

	public String convertJsonToCsv(String jsonString, String targetFolderPath, String filename) {
		try {
			JSONArray docs = new JSONArray(jsonString);
			File file = new File(targetFolderPath + filename + DOT_CSV);
			String csv = CDL.toString(docs);
			if (file.exists()) {
				filename = filename + "-" + System.currentTimeMillis() + DOT_CSV;
			} else {
				filename = filename + DOT_CSV;
				file = new File(targetFolderPath + filename);
			}
			FileUtils.writeStringToFile(file, csv);
			return filename;

		} catch (Exception ex) {
			logger.info("$ Conversion of Csv file failed ", ex);
		}
		return null;
	}

	@Override
	public void resourceImageUpload(String folderInBucket, String gooruBucket, String fileName, String callBackUrl, String sourceFilePath) throws Exception {
		Integer s3UploadFlag = 0;
		if (folderInBucket != null) {
			fileName = folderInBucket + fileName;
		}
		File file = new File(sourceFilePath + fileName);
		if (!file.isDirectory()) {
			byte[] data = FileUtils.readFileToByteArray(file);
			S3Object fileObject = new S3Object(fileName, data);
			fileObject = getS3Service().putObject(gooruBucket, fileObject);
			setPublicACL(fileName, gooruBucket);
			s3UploadFlag = 1;
		} else {
			listFilesForFolder(file, gooruBucket, fileName);
			s3UploadFlag = 1;
		}
		try {
			if (callBackUrl != null) {
				final JSONObject data = new JSONObject();
				final JSONObject resource = new JSONObject();
				resource.put("s3UploadFlag", s3UploadFlag);
				data.put("resource", resource);
				new ClientResource(callBackUrl).put(data.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPublicACL(String objectKey, String gooruBucket) throws Exception {
		S3Object fileObject = getS3Service().getObject(gooruBucket, objectKey);
		AccessControlList objectAcl = getS3Service().getObjectAcl(gooruBucket, fileObject.getKey());
		objectAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
		fileObject.setAcl(objectAcl);
		getS3Service().putObject(gooruBucket, fileObject);
	}

	public void listFilesForFolder(final File folder, String gooruBucket, String fileName) throws Exception {
		for (final File file : folder.listFiles()) {
			if (file.isDirectory()) {
				listFilesForFolder(file, gooruBucket, fileName);
			} else {
				byte[] data = FileUtils.readFileToByteArray(file);
				S3Object fileObject = new S3Object(fileName + file.getName(), data);
				fileObject = getS3Service().putObject(gooruBucket, fileObject);
				setPublicACL(fileName + file.getName(), gooruBucket);
			}
		}
	}

	public RestS3Service getS3Service() {
		return s3Service;
	}

	@Override
	public void convertDocumentToPdf(Conversion conversion) {
		JSONObject data;
		try {
			data = new JSONObject(new JSONSerializer().serialize(conversion));
			data.put("eventName", "convert.docToPdf");

			try {
				if (conversion != null) {
					OfficeManager officeManager = new DefaultOfficeManagerConfiguration().buildOfficeManager();
					officeManager.start();

					OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
					converter.convert(new File(conversion.getSourceFilePath()), new File(conversion.getTargetFolderPath() + "/" + conversion.getFileName()));
					data.put("status", "completed");
					convertPdfToImage(conversion.getTargetFolderPath() + "/" + conversion.getFileName(), conversion.getResourceGooruOid(), conversion.getAuthXml());
					File file = new File(conversion.getSourceFilePath());
					file.renameTo(new File(conversion.getTargetFolderPath() + "/" + StringUtils.substringBeforeLast(conversion.getFileName(), ".") + ".ppt"));
					officeManager.stop();
					resourceImageUpload(conversion.getFolderInBucket(), conversion.getGooruBucket(), conversion.getFileName(), null, conversion.getTargetFolderPath());
				}
			} catch (Exception e) {
				data.put("status", "failed");
			}
			new ClientResource(conversion.getApiEndPoint() + "v2/job/" + conversion.getJobUid() + "?sessionToken=" + conversion.getSessionToken()).put(data.toString());
		} catch (JSONException jsonException) {
			logger.error("Failed to parse json : " + jsonException);
		}
	}
	public KafkaProducer getKafkaProducer() {
		return kafkaProducer;
	}

	@Override
	public String convertHtmlToExcel(String htmlContent, String targetPath, String filename) {
		if (htmlContent != null) {
			File targetDir = new File(targetPath);
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
			if (filename == null || (filename!=null&&filename.isEmpty())) {
				filename = String.valueOf(System.currentTimeMillis());
			} else {
				File file = new File(targetPath + filename + DOT_XLSX);
				if (file.exists()) {
					filename = filename + "-" + System.currentTimeMillis();
				}
			}
			
			String filePath = targetPath + filename + DOT_XLSX;
			return convertHtmlToExcel(htmlContent, filePath);
		}
		return null;
	}
	
	private String convertHtmlToExcel(String htmlContent, String filePath) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			// create excel sheet
			XSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(workbook.getSheetIndex(sheet), STUDENT_DATA);
			
			//Set Header Font
			XSSFFont headerFont = workbook.createFont();
			headerFont.setBoldweight(headerFont.BOLDWEIGHT_BOLD);
			headerFont.setFontHeightInPoints((short) 12);

			//Set Header Style
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
			headerStyle.setAlignment(headerStyle.ALIGN_CENTER);
			headerStyle.setFont(headerFont);

			int rowCount = 0;

			Document doc = Jsoup.parse(htmlContent);
			
			/**
			 * 1. The <table> tag should be the outer most element. Multiple <table> or other HTML tags should not appear at the same level
			 *  Not allowed
			 *  <table>....</table>
			 *  <div>...<div>
			 *  <table>....</table>
			 *  <div>...<div>
			 *  <table>....</table>
			 *  
			 *  Allowed
			 *  <table> 
			 *  	<tr>
			 *  		<th></th>
			 *  		<th><div></div></th>
			 *  	</tr>
			 *  	</tr>
			 *  		<td></td>
			 *  		<td><div></div></td>
			 *  	</tr>
			 *  </table>
			 *  
			 *  2. Table Cell <TD> should be given a background color 
			 */
			
			for (Element table : doc.select(TABLE)) {
				// loop through all tr of table
				for (Element row : table.select(TR)) {
					// create row for each tag
					Row header = sheet.createRow(rowCount);
					// loop through all tag of tag

					Elements ths = row.select(TH);
					int count = 0;
					for (Element element : ths) {
						Cell cell = header.createCell(count);
						sheet.setColumnWidth(count, 15*256);
						// set header style
						cell.setCellValue(element.text());
						cell.setCellStyle(headerStyle);
						count++;
					}
					// now loop through all td tag
					Elements tds = row.select(TD);
					count = 0;
					for (Element element : tds) {
						// create cell for each tag
						Cell cell = header.createCell(count);
						String bgStyle = element.attr(STYLE);
						if(bgStyle != null&&!bgStyle.isEmpty()&&bgStyle.contains("#")) {
							cell.setCellStyle(getCellStyle(workbook.createCellStyle(), bgStyle.replaceAll(BACKGROUND_COLOR, "")));
						}
						cell.setCellValue(element.text());
						count++;
					}
					rowCount++;
				}
			}
			File file = new File(filePath);
			String path = file.getAbsolutePath();
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(new File(path));
			} catch (FileNotFoundException e) {
				filePath = null;
				logger.error("file not found on server ", e);
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				filePath = null;
				logger.error("error in the writing to the file ", e);
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					filePath = null;
					logger.error("error in the file output ", e);
				}
			}
		} catch (Exception e) {
			logger.error("something went wrong while converting xlsx", e);
		}
		return filePath;
	}
	
	private XSSFCellStyle getCellStyle(XSSFCellStyle cellStyle, String hexa) {
		Color c = new Color(
				Integer.valueOf(hexa.substring(1, 3), 16),
				Integer.valueOf(hexa.substring(3, 5), 16),
				Integer.valueOf(hexa.substring(5, 7), 16));
		byte[] rgb = new byte[3];
		rgb[0] = (byte) c.getRed();
		rgb[1] = (byte) c.getGreen();
		rgb[2] = (byte) c.getBlue();
		cellStyle.setFillForegroundColor(new XSSFColor(rgb));
		cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);             
		cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);            
		cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		cellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
		return cellStyle;
	}
	
}
