package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.aarboard.nextcloud.api.utils.JsonAnswer;

import java.util.Collections;
import java.util.List;

public class UserListAnswer extends JsonAnswer {
    @JsonProperty
    private Data data;

    @JsonIgnore
    public List<String> getAllUsers() {
        if (data != null && data.users != null) {
            return data.users;
        }
        return Collections.emptyList();
    }

    public static class Data {
        @JsonProperty
        private List<String> users;
    }
}
