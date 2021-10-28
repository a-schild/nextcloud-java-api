package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.aarboard.nextcloud.api.utils.JsonAnswer;

import java.util.Collections;
import java.util.List;

public class GroupListAnswer extends JsonAnswer {
    @JsonProperty
    private Data data;

    @JsonIgnore
    public List<String> getAllGroups() {
        if (data != null && data.groups != null) {
            return data.groups;
        }
        return Collections.emptyList();
    }

    public static class Data {
        @JsonProperty
        private List<String> groups;
    }
}
