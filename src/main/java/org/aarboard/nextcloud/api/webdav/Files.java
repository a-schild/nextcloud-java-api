package org.aarboard.nextcloud.api.webdav;

import java.io.IOException;
import java.io.InputStream;

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
	 * method to check if a file already exists
	 * 
	 * @param rootPath path of the file
	 * @return boolean value if the given file exists or not
	 */
	public boolean fileExists(String rootPath){
		String path = (_serverConfig.isUseHTTPS() ? "https" : "http") + "://" + _serverConfig.getServerName() + "/" + WEB_DAV_BASE_PATH + rootPath;
		
		Sardine sardine = SardineFactory.begin();
		
		sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
		sardine.enablePreemptiveAuthentication(_serverConfig.getServerName());
		
		try {
			return sardine.exists(path);
		} catch (IOException e) {
			throw new NextcloudApiException(e);
		}
	}
	
    /**
     * 
     * @param fileInputStream      inputstream of the file which should be uploaded
     * @param remotePath           path where the file should be uploaded to
     * @throws Exception
     */
    public void uploadFile(InputStream fileInputStream, String remotePath)
    {
    	String path = (_serverConfig.isUseHTTPS() ? "https" : "http") + "://" + _serverConfig.getServerName() + "/" + WEB_DAV_BASE_PATH + remotePath;
		
		Sardine sardine = SardineFactory.begin();
		
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        sardine.enablePreemptiveAuthentication(_serverConfig.getServerName());

        try {
            sardine.put(path, fileInputStream);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
	
	/**
	 * method to remove files
	 * @param rootPath rootPath of the file which should be removed
	 */
	public void removeFile(String rootPath) {
		String path = (_serverConfig.isUseHTTPS() ? "https" : "http") + "://" + _serverConfig.getServerName() + "/" + WEB_DAV_BASE_PATH + rootPath;
		
		Sardine sardine = SardineFactory.begin();
		
		sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        sardine.enablePreemptiveAuthentication(_serverConfig.getServerName());
		try {
			sardine.delete(path);
		} catch ( IOException e ) {
			throw new NextcloudApiException(e);
		}
	}
	
	
}
