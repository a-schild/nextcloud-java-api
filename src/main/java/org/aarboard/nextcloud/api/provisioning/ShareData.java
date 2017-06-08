package org.aarboard.nextcloud.api.provisioning;

public enum ShareData
{
    PERMISSIONS("permissions"),
    PASSWORD("password"),
    PUBLICUPLOAD("publicUpload"),
    EXPIREDATE("expireDate");

    public final String parameterName;

    private ShareData(String parameterName)
    {
        this.parameterName = parameterName;
    }
}
