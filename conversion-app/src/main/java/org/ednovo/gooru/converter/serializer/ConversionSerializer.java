package org.ednovo.gooru.converter.serializer;

import org.ednovo.gooru.application.converter.ConversionAppConstants;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import flexjson.JSONSerializer;

public class ConversionSerializer implements ConversionAppConstants {
	
	public static ModelAndView toModelAndView(Object object) {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, object);
		return jsonmodel;
	}
	
	public static ModelAndView toJsonModelAndView(Object object) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, serializeToJsonObject(object));
		return jsonmodel;
	}

	public static JSONObject serializeToJsonObject(Object model) throws Exception {
		return new JSONObject(serialize(model, JSON, true));
	}
	public static JSONObject requestData(String data) throws Exception {

		return data != null ? new JSONObject(data) : null;
	}

	public static String serialize(Object model, String type, boolean deepSerialize) {
		if (model == null) {
			return "";
		}
		String serializedData = null;
		JSONSerializer serializer = new JSONSerializer();

		if (type == null || type.equals(JSON)) {
			try {
				serializedData = deepSerialize ? serializer.deepSerialize(model) : serializer.serialize(model);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return serializedData;
	}


}
