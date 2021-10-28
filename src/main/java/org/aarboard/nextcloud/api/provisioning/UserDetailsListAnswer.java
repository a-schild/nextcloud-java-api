package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.aarboard.nextcloud.api.utils.JsonAnswer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@JsonRootName(value = "ocs")
public class UserDetailsListAnswer extends JsonAnswer {
    @JsonProperty
    private Data data;

    @JsonIgnore
    public List<User> getAllUserDetails() {
        if (data != null && data.users != null) {
            return data.users.values().stream().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static class Data {
        @JsonProperty
        private Users users;
    }

    public static class Users extends HashMap<String, User> {}
}
