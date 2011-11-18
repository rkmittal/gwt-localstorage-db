/**
 * Copyright 2011, Raghu Kumar Mittal, Handheld Solutions Research Labs Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.handsrel.localstorage.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point class, that also loads the Query browser UI to access/change the local storage api
 * @author Raghu Mittal
 *
 */
public class Localstorage implements EntryPoint {

	private Logger logger = Logger.getLogger("");
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		try {
			Storage storage = Storage.getLocalStorageIfSupported();
			if (storage==null)
				throw new LocalStorageDatabaseException("Local storage not supported in the browser");
			//Window.alert("Local storage not supported in the browser. Exiting...");
			else {
				LdbApi.initialize(storage);
			}
		} catch (LocalStorageDatabaseException e) {
			logger.log(Level.SEVERE, "Database Exception", e);
		}

		RootPanel rootPanel = RootPanel.get("localstoragequerybrowser");
		if(rootPanel == null){
			return;
		}
		LdbQueryBrowser lqb = new LdbQueryBrowser();

		rootPanel.add(lqb);
		/*customLogArea.show();
		if (LogConfiguration.loggingIsEnabled()) {
			logger.addHandler(new HasWidgetsLogHandler(customLogArea.getPanel()));
		}*/
		//rootPanel.add(customLogArea, 400, 0);
	}
}
