/*******************************************************************************
 * ResourceWebUrlValidator.java
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
