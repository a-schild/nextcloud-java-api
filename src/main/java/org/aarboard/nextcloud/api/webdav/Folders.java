/*
 * Copyright (C) 2017 a.schild
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
package org.aarboard.nextcloud.api.webdav;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 *
 * @author a.schild
 */
public class Folders {

    private final String WEB_DAV_BASE_PATH= "remote.php/webdav/";

    private final ServerConfig  _serverConfig;

    public Folders(ServerConfig _serverConfig) {
        this._serverConfig = _serverConfig;
    }

    /**
     * Get all subfolders of the specified path
     *
     * @param rootPath path of the folder
     * @return found subfolders
     *
     * @deprecated The methods naming is somehow misleading, as it lists all resources (subfolders and files) within the given {@code rootPath}. Please use {@link #listFolderContent(String)} instead.
     */
    @Deprecated
    public List<String> getFolders(String rootPath)
    {
        return listFolderContent(rootPath);
    }

    /**
     * List all file names and subfolders of the specified path
     *
     * @param path path of the folder
     * @return found file names and subfolders
     */
    public List<String> listFolderContent(String path)
    {
        return listFolderContent(path, 1);
    }

    /**
     * List all file names and subfolders of the specified path traversing into subfolders to the given depth.
     *
     * @param path path of the folder
     * @param depth depth of recursion while listing folder contents
     * @return found file names and subfolders
     */
    public List<String> listFolderContent(String path, int depth)
    {
        String url = (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+path ;

        List<String> retVal= new LinkedList<>();
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        List<DavResource> resources;
        try {
            resources = sardine.list(url, depth);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
        for (DavResource res : resources)
        {
            retVal.add(res.getName());
        }
        return retVal;
    }

    /**
     * Checks if the folder at the specified path exists
     *
     * @param rootPath path of the folder
     * @return true if the folder exists
     */
    public boolean exists(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;

        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            return sardine.exists(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Creates a folder at the specified path
     *
     * @param rootPath path of the folder
     */
    public void createFolder(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;

        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            sardine.createDirectory(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Deletes the folder at the specified path
     *
     * @param rootPath path of the folder
     */
    public void deleteFolder(String rootPath)
    {
        String path=  (_serverConfig.isUseHTTPS() ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH+rootPath ;

        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            sardine.delete(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

    /**
     * Downloads the folder at the specified remotepath to the rootdownloadirpath
     *
     * @param remotepath the path in the nextcloud server with respect to the specific folder
     * @param rootdownloadirpath the local path in the system where the folder needs be saved
     * @return
     * @throws IOException
     */
    public void downloadFolder(String remotepath,String rootdownloadirpath) throws IOException {
        int depth=1;
        String rootpath = (_serverConfig.isUseHTTPS()  ? "https" : "http") +"://"+_serverConfig.getServerName()+"/"+WEB_DAV_BASE_PATH;
        String[] segments = remotepath.split("/");
        String foldername = segments[segments.length - 1];
        String newdownloadir = rootdownloadirpath + "/" + foldername;
        System.out.println(newdownloadir);
        File nefile1 = new File(newdownloadir);
        if(!nefile1.exists()) {
            nefile1.mkdir();
        }
        String rootpathnew= rootpath+remotepath ;
        int count = 0;
        String filepath;
        List<String> retVal= new LinkedList<>();
        List<DavResource> resources;
        Sardine sardine = SardineFactory.begin();
        sardine.setCredentials(_serverConfig.getUserName(), _serverConfig.getPassword());
        try {
            resources = sardine.list(rootpathnew, depth);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }

        for (DavResource res : resources)
        {
            System.out.println(res.getName());
            //Skip the Documents folder which is listed as default as first by the sardine output
            if(count != 0) {
                if(res.isDirectory()) {
                    String filename = res.getName();
                    String pathtosend = remotepath + "/" + filename;
                    downloadFolder(pathtosend,newdownloadir);
                }
                else {
                    String filename = res.getName();
                        filepath = rootpathnew + "/" + filename;
                        retVal.add(res.getName());
                        InputStream in = null;
                        if (sardine.exists(filepath)) {
                            in = sardine.get(filepath);
                            byte[] buffer = new byte[in.available()];
                            in.read(buffer);
                            File targetFile = new File(newdownloadir + "/" + filename);
                            OutputStream outStream = new FileOutputStream(targetFile);
                            outStream.write(buffer);
                            in.close();
                            outStream.close();
                        }
                }
            }
            count ++;
        }
    }
}
