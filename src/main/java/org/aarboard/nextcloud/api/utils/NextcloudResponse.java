package org.aarboard.nextcloud.api.utils;

public interface NextcloudResponse {

    /**
     * @return the status
     */
    String getStatus();

    /**
     * @return the statusCode
     */
    int getStatusCode();

    /**
     * @return the message
     */
    String getMessage();

    /**
     * @return the totalItems
     */
    int getTotalItems();

    /**
     * @return the itemsPerPage
     */
    int getItemsPerPage();

}
