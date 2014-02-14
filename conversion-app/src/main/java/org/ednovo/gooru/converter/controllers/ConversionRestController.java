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
@RequestMapping(value = {"/conversion"})
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
		return toModelAndView(getConversionservice().convertJsonToCsv(conversion.getJsonString(), conversion.getTargetFolderPath(),conversion.getFileName()));
	}
	

	private Conversion buildMediaFromInputParameters(String json) {
		return JsonDeserializer.deserialize(json, Conversion.class);
	}

	public ConversionService getConversionservice() {
		return conversionservice;
	}

	
}