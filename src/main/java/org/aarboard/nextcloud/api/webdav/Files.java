package org.aarboard.nextcloud.api.webdav;

import java.io.IOException;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * 
 * @author tott
 *
 */
public class Files {

	private static final String WEB_DAV_BASE_PATH = "remote.php/webdav/";
	
	private final ServerConfig _serverConfig;
	
	public Files(ServerConfig _serverConfig) {
		this._serverConfig = _serverConfig;
	}
	
	
	/**
	 * method to remove files
	 * @param rootPath rootPath of the file which should be removed
	 */
	public void removeFile(String rootPath) {
		String path = (_serverConfig.isUseHTTPS() ? "https" : "http") + "://" + _serverConfig.getServerName() + "/" + WEB_DAV_BASE_PATH + rootPath;
		
		Sardine sardine = SardineFactory.begin();
		sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
		try {
			sardine.delete(path);
		} catch ( IOException e ) {
			throw new NextcloudApiException(e);
		}
	}
	
}
