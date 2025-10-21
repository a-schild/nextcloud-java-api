package org.aarboard.nextcloud.api.webdav;


import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private static final String ERROR_SHUTDOWN = "Error in sardine shutdown";
    public static final String URI_NS_OWNCLOUD = "http://owncloud.org/ns";
    public static final String URI_NS_DAV = "DAV:";

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
                LOG.warn(ERROR_SHUTDOWN, ex2);
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
                LOG.warn(ERROR_SHUTDOWN, ex2);
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
     * method to rename/move files
     *
     * @param oldPath path of the file which should be renamed/moved
     * @param newPath path of the file which should be renamed/moved
     * @param overwriteExisting overwrite if target already exists
     */
    public void renameFile(String oldPath, String newPath, boolean overwriteExisting) {
        renamePath(oldPath, newPath, overwriteExisting);
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
        boolean status;
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
            String[] segments = remotePath.split("/");
            String filename = segments[segments.length - 1];
            downloadDirPath = downloadDirPath + "/" + filename;
        }
      try (InputStream in = sardine.get(path)) {
        byte[] buffer = new byte[AWebdavHandler.FILE_BUFFER_SIZE];
        int bytesRead;
        File targetFile = new File(downloadDirPath);
        try (OutputStream outStream = java.nio.file.Files.newOutputStream(targetFile.toPath())) {
          while ((bytesRead = in.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
          }
          outStream.flush();
        }
        status = true;
      } catch (IOException e) {
        throw new NextcloudApiException(e);
      } finally {
        sardine.shutdown();
      }
        return status;
    }

    /**
     * Downloads the file at the specified remotepath as an InputStream,
     *
     * @param remotePath Remotepath where the file is saved in the nextcloud
     * server
     * @return InputStream
     */
    public InputStream downloadFile(String remotePath) {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        WebdavInputStream in;
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
                LOG.warn(ERROR_SHUTDOWN, ex2);
            }
        }
        return in;
    }
    
    /**
     * Get the properties of a resource, File or Folder
     *
     * @param remotePath Remotepath of the resource to query for
     * server
     * @param allProperties Return all properties, not only base properties
     * <a href="https://docs.nextcloud.com/server/latest/developer_manual/client_apis/WebDAV/basic.html#requesting-properties">...</a>
     * @return InputStream
     */
    public ResourceProperties getProperties(String remotePath, boolean allProperties) {
        String path = buildWebdavPath(remotePath);
        Sardine sardine = buildAuthSardine();

        try
        {
            Set<QName> props= new HashSet<>();
            if (allProperties)
            {
                props.add(new QName(URI_NS_DAV, "getlastmodified", "d"));
                props.add(new QName(URI_NS_DAV, "getetag", "d"));
                props.add(new QName(URI_NS_DAV, "getcontenttype", "d"));
                props.add(new QName(URI_NS_DAV, "resourcetype", "d"));
                props.add(new QName(URI_NS_DAV, "getcontentlength", "d"));
                props.add(new QName(URI_NS_DAV, "displayname", "d"));
                props.add(new QName(URI_NS_OWNCLOUD, "id", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "fileid", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "favorite", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "comments-href", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "comments-count", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "comments-unread", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "owner-id", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "owner-display-name", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "share-types", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "checksums", "oc"));
                props.add(new QName("http://nextcloud.org/ns", "has-preview", "nc"));
                props.add(new QName(URI_NS_OWNCLOUD, "permissions", "oc"));
                props.add(new QName(URI_NS_OWNCLOUD, "size", "oc"));
            }
            List<DavResource> resources= sardine.propfind(path, 0, props);
            if (resources != null && resources.size() == 1)
            {
                DavResource res= resources.get(0);
                ResourceProperties retVal= new ResourceProperties();
                retVal.setModified(res.getModified());
                retVal.setEtag(res.getEtag());
                retVal.setContentType(res.getContentType());
                retVal.setContentLength(res.getContentLength());
                retVal.setCreation(res.getCreation());
                retVal.setDisplayName(res.getDisplayName());
                if (allProperties)
                {
                    Map<String, String> custProps= res.getCustomProps();
                    retVal.setResourceType(custProps.get("resourcetype"));
                    retVal.setId(custProps.get("id"));
                    retVal.setFileId(custProps.get("fileid"));
                    retVal.setFavorite("1".equals(custProps.get("favorite")));
                    retVal.setCommentsHref(custProps.get("comments-href"));
                    retVal.setCommentsCount( convertStringToLong(custProps.get("comments-count")));
                    retVal.setCommentsUnread(convertStringToLong(custProps.get("comments-unread")));
                    retVal.setOwnerId(custProps.get("owner-id"));
                    retVal.setOwnerDisplayName(custProps.get("owner-display-name"));
                    retVal.setShareTypes(custProps.get("share-types"));
                    retVal.setChecksums(custProps.get("checksums"));
                    retVal.setHasPreview("1".equals(custProps.get("has-preview")));
                    retVal.setPermissions(custProps.get("permissions"));
                    retVal.setSize(convertStringToLong(custProps.get("size")));
                }
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
                LOG.warn(ERROR_SHUTDOWN, ex2);
            }
        }
    }
    
    private long convertStringToLong(String number)
    {
        if (number == null || number.isEmpty())
        {
            return 0;
        }
        else
        {
            return Long.parseLong(number);
        }
    }
}
