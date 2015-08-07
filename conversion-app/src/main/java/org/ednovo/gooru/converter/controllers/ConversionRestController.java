/*******************************************************************************
 * ConversionRestController.java
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.converter.serializer.ConversionSerializer;
import org.ednovo.gooru.converter.serializer.JsonDeserializer;
import org.ednovo.gooru.converter.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/conversion" })
public class ConversionRestController extends ConversionSerializer {

	@Autowired
	public ConversionService conversionservice;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/image", method = { RequestMethod.POST })
	public ModelAndView imageConversion(@RequestBody String data, @RequestParam(value = SESSION_TOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		getConversionservice().resizeImageByDimensions(conversion.getSourceFilePath(), conversion.getTargetFolderPath(), conversion.getDimensions(), conversion.getResourceGooruOid(), sessionToken, conversion.getThumbnail(), conversion.getApiEndPoint());
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/textbook/upload", method = { RequestMethod.POST })
	public ModelAndView Textbook(@RequestBody String data, @RequestParam(value = SESSION_TOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		getConversionservice().scribdUpload(conversion.getScribdAPIKey(), conversion.getDocKey(), conversion.getResourceFilePath(), conversion.getResourceGooruOid(), conversion.getAuthXml());
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/pdf-to-image", method = { RequestMethod.POST })
	public ModelAndView PdfToConversion(@RequestBody String data, @RequestParam(value = SESSION_TOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		getConversionservice().convertPdfToImage(conversion.getResourceFilePath(), conversion.getResourceGooruOid(), conversion.getAuthXml());
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/image/resize", method = { RequestMethod.POST })
	public ModelAndView imageResize(@RequestBody String data, @RequestParam(value = SESSION_TOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		getConversionservice().resizeImage(conversion.getCommand(), conversion.getLogFile());
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/htmltopdf", method = { RequestMethod.POST })
	public ModelAndView htmltopdf(@RequestBody String data, @RequestParam(value = SESSION_TOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		return toModelAndView(getConversionservice().convertHtmlToPdf(conversion.getHtml(), conversion.getTargetFolderPath(), conversion.getUrl(), conversion.getFileName()));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/jsontostring", method = { RequestMethod.POST })
	public ModelAndView jsontocsv(@RequestBody String data, @RequestParam(value = SESSION_TOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		return toModelAndView(getConversionservice().convertJsonToCsv(conversion.getJsonString(), conversion.getTargetFolderPath(), conversion.getFileName()));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/image/upload", method = { RequestMethod.POST })
	public ModelAndView resourceImageUpload(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Conversion conversion = buildMediaFromInputParameters(data);
		this.getConversionservice().resourceImageUpload(conversion.getFolderInBucket(), conversion.getGooruBucket(), conversion.getFileName(), conversion.getCallBackUrl(), conversion.getSourceFilePath());
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/document-to-pdf", method = { RequestMethod.POST })
	public ModelAndView documentToPdf(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getConversionservice().convertDocumentToPdf(buildMediaFromInputParameters(data));
		return null;
	}

	private Conversion buildMediaFromInputParameters(String json) {
		return JsonDeserializer.deserialize(json, Conversion.class);
	}

	public ConversionService getConversionservice() {
		return conversionservice;
	}

}
