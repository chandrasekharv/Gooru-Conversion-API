/*******************************************************************************
 * ConversionSerializer.java
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
