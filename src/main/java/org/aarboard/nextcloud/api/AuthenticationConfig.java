package src.main.java.org.aarboard.nextcloud.api;

public package org.aarboard.nextcloud.api;

public class AuthenticationConfig {

    private String userName;
    private String password;

    private String bearerToken;

    public AuthenticationConfig(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public AuthenticationConfig(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public boolean usesBasicAuthentication() {
        return bearerToken == null;
    }

    public boolean usesBearerTokenAuthentication() {
        return !usesBasicAuthentication();
    }

}
