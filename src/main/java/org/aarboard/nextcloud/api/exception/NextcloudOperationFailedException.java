package org.aarboard.nextcloud.api.exception;

public class NextcloudOperationFailedException extends NextcloudApiException {
    private static final long serialVersionUID = 6382478664807826933L;

    public NextcloudOperationFailedException(int statuscode, String message) {
        super(String.format("Nextcloud API call failed with statuscode %d and message \"%s\"", statuscode, message));
    }
}
