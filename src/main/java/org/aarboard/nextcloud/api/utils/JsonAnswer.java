package org.aarboard.nextcloud.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "ocs")
public class JsonAnswer implements NextcloudResponse {
    @JsonProperty
    private Meta meta;

    @Override
    public String getStatus() {
        return meta.status;
    }

    @Override
    public int getStatusCode() {
        return meta.statusCode;
    }

    @Override
    public String getMessage() {
        return meta.message;
    }

    @Override
    public int getTotalItems() {
        return meta.totalItems;
    }

    @Override
    public int getItemsPerPage() {
        return meta.itemsPerPage;
    }

    public static class Meta {
        @JsonProperty
        public String status = null;
        @JsonProperty(value = "statuscode")
        public int statusCode = -1;
        @JsonProperty
        public String message = null;
        @JsonProperty(value = "totalitems")
        public int totalItems = -1;
        @JsonProperty(value = "itemsperpage")
        public int itemsPerPage = -1;
    }
}
