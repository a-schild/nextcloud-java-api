package org.aarboard.nextcloud.api.provisioning;

public enum ShareData
{
    PERMISSIONS("permissions"),
    PASSWORD("password"),
    PUBLICUPLOAD("publicUpload"),
    EXPIREDATE("expireDate"),
	HIDEDOWNLOAD("hideDownload");

    public final String parameterName;

    ShareData(String parameterName)
    {
        this.parameterName = parameterName;
    }
}
