package org.ednovo.gooru.application.converter;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

public class ResourceWebUrlValidator {

	private final Logger logger = LoggerFactory.getLogger(ResourceWebUrlValidator.class);

	private WebClient webClient;

	public synchronized Integer validateWebResourceUrl(String webURL, String pageTesterURL) {

		try {
			webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
			WebResponse webResponse = webClient.getPage(webURL).getWebResponse();
			int status = webResponse.getStatusCode();
			if (status != 200) {
				return status;
			}
			String urlToCheck = pageTesterURL + webURL;
			// Now check if the page has a frame breaker.
			webResponse = webClient.getPage(urlToCheck).getWebResponse();
			status = webResponse.getStatusCode();
			if (status != 200) {
				return status;
			}

			@SuppressWarnings("deprecation")
			String pageURL = webResponse.getRequestSettings().getUrl().toString();
			int validationStatus = (urlToCheck.equals(pageURL) && status == 200) ? 200 : -200;
			return validationStatus;

		} catch (MalformedURLException malformedURLException) {
			return -200;
		} catch (Exception exception) {
			logger.debug("ERROR : Unable to start web driver : " + exception.getMessage());
			return null;

		} finally {

			if (webClient != null) {
				webClient.closeAllWindows();
			}
		}
	}
}