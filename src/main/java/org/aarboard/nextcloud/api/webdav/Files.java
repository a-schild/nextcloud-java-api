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

    /** Uploads a file at the specified path with the data from the InputStream
     *
     * @param inputStream          InputStream of the file which should be uploaded
     * @param remotePath           path where the file should be uploaded to
     */
    public void uploadFile(InputStream inputStream, String remotePath)
    {
    	String path = (_serverConfig.isUseHTTPS() ? "https" : "http") + "://" + _serverConfig.getServerName() + "/" + WEB_DAV_BASE_PATH + remotePath;
		
		Sardine sardine = SardineFactory.begin();
		
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        sardine.enablePreemptiveAuthentication(_serverConfig.getServerName());

        try {
            sardine.put(path, inputStream);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
	
	/**
	 * method to remove files
	 * @param rootPath path of the file which should be removed
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

	/**
	 *Downloads the file at the specified remotepath to the download directory, and returns true if the download is successful
	 *
	 * @param remotepath Remotepath where the file is saved in the nextcloud server
	 * @param downloadirpath Path where the file is downloaded, it would be created if it doesn't exist.
	 * @return boolean
	 * @throws IOException
	 */

	public boolean downloadFile(String remotepath, String downloadirpath) throws IOException {
		boolean status=false;
		String path = (_serverConfig.isUseHTTPS() ? "https" : "http") + "://" + _serverConfig.getServerName() + "/" + WEB_DAV_BASE_PATH + remotepath;
		Sardine sardine = SardineFactory.begin();
		sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
		sardine.enablePreemptiveAuthentication(_serverConfig.getServerName());

		File downloadFilepath = new File(downloadirpath);
		if(!downloadFilepath.exists()) {
			downloadFilepath.mkdir();
		}

		if(fileExists(remotepath)) {
			//Extract the Filename from the path
			String[] segments = path.split("/");
			String filename = segments[segments.length - 1];
			downloadirpath = downloadirpath + "/" + filename;
		}
		InputStream in = null;
		try {
			in = sardine.get(path);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			File targetFile = new File(downloadirpath);
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			status = true;
		} catch (IOException e) {
			throw new NextcloudApiException(e);
		} finally {
			sardine.shutdown();
			in.close();
			return status;
		}
	}

}
