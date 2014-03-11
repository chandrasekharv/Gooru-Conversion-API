/*******************************************************************************
 * Conversion.java
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
package org.ednovo.gooru.converter.controllers;

public class Conversion {

	private String sourceFilePath;
	private String targetFolderPath;
	private String thumbnail;
	private String dimensions;
	private String resourceGooruOid;
	private String resourceFilePath;
	private String scribdAPIKey;
	private String docKey;
	private String authXml;
	private String command;
	private String logFile;
	private String apiEndPoint;
	private String html;
	private String url;
	private String fileName;
	private String jsonString;
	

	public String getScribdAPIKey() {
		return scribdAPIKey;
	}

	public void setScribdAPIKey(String scribdAPIKey) {
		this.scribdAPIKey = scribdAPIKey;
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getTargetFolderPath() {
		return targetFolderPath;
	}

	public void setTargetFolderPath(String targetFolderPath) {
		this.targetFolderPath = targetFolderPath;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getDimensions() {
		return dimensions;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public String getResourceGooruOid() {
		return resourceGooruOid;
	}

	public void setResourceGooruOid(String resourceGooruOid) {
		this.resourceGooruOid = resourceGooruOid;
	}

	public void setResourceFilePath(String resourceFilePath) {
		this.resourceFilePath = resourceFilePath;
	}

	public String getResourceFilePath() {
		return resourceFilePath;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	public String getAuthXml() {
		return authXml;
	}

	public void setAuthXml(String authXml) {
		this.authXml = authXml;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setApiEndPoint(String apiEndPoint) {
		this.apiEndPoint = apiEndPoint;
	}

	public String getApiEndPoint() {
		return apiEndPoint;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}


}
