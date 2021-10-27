package org.aarboard.nextcloud.api.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

public class NextcloudSearch {
    private final String search;
    private final int limit;
    private final int offset;

    public NextcloudSearch(String search, int limit, int offset) {
        this.search = search;
        this.limit = limit;
        this.offset = offset;
    }

    public List<NameValuePair> asQueryParameters() {
        List<NameValuePair> result = new LinkedList<>();

        if (limit != -1) {
            result.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        }

        if (offset != -1) {
            result.add(new BasicNameValuePair("offset", Integer.toString(offset)));
        }

        if (search != null) {
            result.add(new BasicNameValuePair("search", search));
        }

        return result;
    }
}
