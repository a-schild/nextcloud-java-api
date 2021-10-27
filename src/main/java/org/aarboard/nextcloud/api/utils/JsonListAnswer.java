package org.aarboard.nextcloud.api.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Collections;
import java.util.List;

@JsonRootName(value = "ocs")
public class JsonListAnswer extends JsonAnswer {
    @JsonProperty
    private List<String> data;

    @JsonIgnore
    public List<String> getResult() {
        if (data != null ) {
            return data;
        }
        return Collections.emptyList();
    }

}
