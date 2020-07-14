package org.aarboard.nextcloud.api.webdav;


import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.aarboard.nextcloud.api.utils.WebdavInputStream;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tott
 *
 */
public class Files extends AWebdavHandler{
    private static final Logger LOG = LoggerFactory.getLogger(Files.class);

    public Files(ServerConfig serverConfig) {
        super(serverConfig);
    }

    /**
     * method to check if a file already exists
     *
     * @param remotePath path of the file
     * @return boolean value if the given file exists or not
     */
    public boolean fileExists(String remotePath) {
        return pathExists(remotePath);
    }

    /**
     * Uploads a file at the specified path with the data from the File
     *
     * @param localSource file which should be uploaded
     * @param remotePath path where the file should be uploaded to
     */
    public void uploadFile(File localSource, String remotePath) {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        try
        {
            sardine.put(path, localSource, null, true);
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }   
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch(Exception ex2)
            {
                LOG.warn("Error in sardine shutdown", ex2);
            }
        }
    }
    
    /**
     * Uploads a file at the specified path with the data from the InputStream
     *
     * @param inputStream InputStream of the file which should be uploaded
     * @param remotePath path where the file should be uploaded to
     */
    public void uploadFile(InputStream inputStream, String remotePath) {
        uploadFile(inputStream, remotePath, true);
    }

    /**
     * Uploads a file at the specified path with the data from the InputStream
     * with an additional continue header
     *
     * @param inputStream InputStream of the file which should be uploaded
     * @param remotePath path where the file should be uploaded to
     * @param continueHeader Continue header is added to receive a possible error by the server before any data is sent.
     */
    public void uploadFile(InputStream inputStream, String remotePath, boolean continueHeader) {
        String path = buildWebdavPath(remotePath);
        
        Sardine sardine = buildAuthSardine();

        try
        {
            sardine.put(path, inputStream, null, continueHeader);
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }        
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch(Exception ex2)
            {
                LOG.warn("Error in sardine shutdown", ex2);
            }
        }
    }

    /**
     * method to remove files
     *
     * @param remotePath path of the file which should be removed
     */
    public void removeFile(String remotePath) {
        deletePath(remotePath);
    }

    /**
     * Downloads the file at the specified remotepath to the download directory,
     * and returns true if the download is successful
     *
     * @param remotePath Remotepath where the file is saved in the nextcloud
     * server
     * @param downloadDirPath Path where the file is downloaded, it would be
     * created if it doesn't exist.
     * @return boolean
     * @throws IOException  In case of IO errors
     */
    public boolean downloadFile(String remotePath, String downloadDirPath) throws IOException {
        boolean status = false;
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        File downloadFilepath = new File(downloadDirPath);
        if (!downloadFilepath.exists())
        {
            downloadFilepath.mkdir();
        }

        if (fileExists(remotePath))
        {
            //Extract the Filename from the path
            String[] segments = path.split("/");
            String filename = segments[segments.length - 1];
            downloadDirPath = downloadDirPath + "/" + filename;
        }
        InputStream in = null;
        try
        {
            in = sardine.get(path);
            byte[] buffer = new byte[AWebdavHandler.FILE_BUFFER_SIZE];
            int bytesRead;
            File targetFile = new File(downloadDirPath);
            try (OutputStream outStream = new FileOutputStream(targetFile))
            {
                while ((bytesRead = in.read(buffer)) != -1)
                {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.flush();
                outStream.close();
            }
            status = true;
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        } finally
        {
            sardine.shutdown();
            if (in != null)
            {
                in.close();
            }
        }
        return status;
    }

    /**
     * Downloads the file at the specified remotepath as an InputStream,
     *
     * @param remotePath Remotepath where the file is saved in the nextcloud
     * server
     * @return InputStream
     * @throws IOException  In case of IO errors
     */
    public InputStream downloadFile(String remotePath) throws IOException {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        WebdavInputStream in = null;
        try
        {
            in = new WebdavInputStream(sardine, sardine.get(path));
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch(Exception ex2)
            {
                LOG.warn("Error in sardine shutdown", ex2);
            }
        }
        return in;
    }
    
    /**
     * Downloads the file at the specified remotepath as an InputStream,
     *
     * @param remotePath Remotepath where the file is saved in the nextcloud
     * server
     * @return InputStream
     * @throws IOException  In case of IO errors
     */
    public ResourceProperties getFileProperties(String remotePath) throws IOException {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        try
        {
            Set<QName> props= new HashSet<>();
            List<DavResource> resources= sardine.propfind(path, 0, props);
            if (resources != null && resources.size() == 1)
            {
                DavResource res= resources.get(0);
                ResourceProperties retVal= new ResourceProperties();
                retVal.setContentLength(res.getContentLength());
                retVal.setContentType(res.getContentType());
                retVal.setCreation(res.getCreation());
                retVal.setDisplayName(res.getDisplayName());
                retVal.setEtag(res.getEtag());
                retVal.setModified(res.getModified());
                return retVal;
            }
            else
            {
                throw new NextcloudApiException("Unexpected number of resources received");
            }
        } catch (IOException e)
        {
            throw new NextcloudApiException(e);
        }
        finally
        {
            try
            {
                sardine.shutdown();
            }
            catch(Exception ex2)
            {
                LOG.warn("Error in sardine shutdown", ex2);
            }
        }
    }
}
