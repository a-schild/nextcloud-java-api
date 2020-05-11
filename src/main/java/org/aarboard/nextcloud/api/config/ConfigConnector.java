/*
 * Copyright (C) 2020 Marco Descher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aarboard.nextcloud.api.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.utils.ConnectorCommon;
import org.aarboard.nextcloud.api.utils.NextcloudResponseHelper;
import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ConfigConnector {
	
	private final static String CONFIG_PART = "ocs/v2.php/apps/provisioning_api/api/v1/config/";
	
	private final ConnectorCommon connectorCommon;
	
	public ConfigConnector(ServerConfig serverConfig){
		this.connectorCommon = new ConnectorCommon(serverConfig);
	}
	
	public List<String> getAppConfigApps(){
		return NextcloudResponseHelper.getAndWrapException(getAppConfigAppsAsync())
			.getAppConfigApps();
	}
	
	private CompletableFuture<AppConfigAppsAnswer> getAppConfigAppsAsync(){
		return connectorCommon.executeGet(CONFIG_PART + "apps", Collections.emptyList(),
			XMLAnswerParser.getInstance(AppConfigAppsAnswer.class));
	}
	
	public List<String> getAppConfigAppKeys(String appConfigApp){
		return NextcloudResponseHelper.getAndWrapException(getAppConfigAppsAsync(appConfigApp))
			.getAppConfigApps();
	}
	
	private CompletableFuture<AppConfigAppsAnswer> getAppConfigAppsAsync(String appConfigApp){
		return connectorCommon.executeGet(CONFIG_PART + "apps/" + appConfigApp,
			Collections.emptyList(), XMLAnswerParser.getInstance(AppConfigAppsAnswer.class));
	}
	
	public String getAppConfigAppKeyValue(String appConfigApp, String appConfigAppKey){
		return NextcloudResponseHelper
			.getAndWrapException(getAppConfigAppsKeyAsync(appConfigApp + "/" + appConfigAppKey))
			.getAppConfigAppKeyValue();
	}
	
	public String getAppConfigAppKeyValue(String appConfigAppKeyPath){
		return NextcloudResponseHelper
			.getAndWrapException(getAppConfigAppsKeyAsync(appConfigAppKeyPath))
			.getAppConfigAppKeyValue();
	}
	
	private CompletableFuture<AppConfigAppKeyValueAnswer> getAppConfigAppsKeyAsync(
		String appConfigAppKeyPath){
		return connectorCommon.executeGet(CONFIG_PART + "apps/" + appConfigAppKeyPath,
			Collections.emptyList(), XMLAnswerParser.getInstance(AppConfigAppKeyValueAnswer.class));
	}
	
	public boolean setAppConfigAppKeyValue(String appConfigApp, String appConfigAppKey,
		Object value){
		return NextcloudResponseHelper.isStatusCodeOkay(
			setAppConfigAppKeyValueAsync(appConfigApp + "/" + appConfigAppKey, value));
	}
	
	public boolean setAppConfigAppKeyValue(String appConfigAppKeyPath, Object value){
		return NextcloudResponseHelper
			.isStatusCodeOkay(setAppConfigAppKeyValueAsync(appConfigAppKeyPath, value));
	}
	
	public CompletableFuture<AppConfigAppKeyValueAnswer> setAppConfigAppKeyValueAsync(
		String appConfigAppKeyPath, Object value){
		List<NameValuePair> postParams = new LinkedList<>();
		postParams.add(new BasicNameValuePair("value", value.toString()));
		return connectorCommon.executePost(CONFIG_PART + "apps/" + appConfigAppKeyPath, postParams,
			XMLAnswerParser.getInstance(AppConfigAppKeyValueAnswer.class));
	}
	
	public boolean deleteAppConfigAppKeyEntry(String appConfigApp, String appConfigAppkey){
		throw new UnsupportedOperationException();
	}
	
}
