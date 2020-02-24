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
package org.aarboard.nextcloud.api;

/**
 *
 * @author a.schild
 */
public class ServerConfig {
    
    private String userName;
    private String password;
    private String serverName;
    private String subpathPrefix;
    private boolean useHTTPS;
    private int port;
    private boolean trustAllCertificates;

    public ServerConfig(String serverName, boolean useHTTPS, int port, 
            String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.subpathPrefix = null;
        this.useHTTPS = useHTTPS;
        this.port = port;
        this.trustAllCertificates = false;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

	/**
	 * @param serverName
	 *            the serverName to set, defaults to <code>null</code>
	 */
	public void setServerName(String serverName){
		this.serverName = serverName;
	}
	
	/**
	 * @return the configured subpath prefix
	 */
	public String getSubpathPrefix(){
		return subpathPrefix;
	}
    
	/**
	 * @param subpathPrefix to apply
	 */
	public void setSubpathPrefix(String subpathPrefix){
		this.subpathPrefix = subpathPrefix;
	}

    /**
     * @return the useHTTPS
     */
    public boolean isUseHTTPS() {
        return useHTTPS;
    }

    /**
     * @param useHTTPS the useHTTPS to set
     */
    public void setUseHTTPS(boolean useHTTPS) {
        this.useHTTPS = useHTTPS;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    
	/**
	 * @param trustAllCertificates if the client should accept any 
         *          HTTPScertificate (e.g. to work against a self-signed
         * 	    certificate)
	 */
	public void setTrustAllCertificates(boolean trustAllCertificates){
		this.trustAllCertificates = trustAllCertificates;
	}
	
	/**
	 * @return if the client should accept any HTTPS certificate (e.g. to work against a self-signed
	 *         certificate)
	 */
	public boolean isTrustAllCertificates(){
		return trustAllCertificates;
	}
    
}
