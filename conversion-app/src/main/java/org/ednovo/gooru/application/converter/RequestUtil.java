package org.ednovo.gooru.application.converter;

import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtil {

	private transient static Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	private static Representation executeMethod(ClientResource clientResource, String data, String type) {
		Representation representation = null;
		if (type.equalsIgnoreCase(Method.POST.getName())) {
			representation = clientResource.post(data);
		} else if (type.equalsIgnoreCase(Method.PUT.getName())) {
			representation = clientResource.put(data);
		}
		return representation;

	}

	public static String executeRestAPI(String data, String requestUrl, String requestType, String sessionToken) {
		try {
			if (sessionToken != null) {
				Representation representation = executeMethod(new ClientResource(requestUrl + "?sessionToken=" + sessionToken), data, requestType);
				return representation != null ? representation.getText() : null;
			} else {
				logger.error("session token cannot be null!");
			}
		} catch (Exception e) {

		}
		return null;

	}
}
