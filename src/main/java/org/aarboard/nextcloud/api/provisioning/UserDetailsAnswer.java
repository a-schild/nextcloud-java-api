package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.aarboard.nextcloud.api.utils.JsonAnswer;

@JsonRootName(value = "ocs")
public class UserDetailsAnswer extends JsonAnswer {
    @JsonProperty
    private User data;

    @JsonIgnore
    public User getUserDetails() {
        if (data != null ) {
            return data;
        }
        return null;
    }

}
