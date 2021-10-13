package org.aarboard.nextcloud.api;

public class AuthenticationConfig {

    private String loginName;
    private String password;

    private String bearerToken;

    public AuthenticationConfig(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }

    public AuthenticationConfig(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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
