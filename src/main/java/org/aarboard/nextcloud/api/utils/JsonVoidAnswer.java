package org.aarboard.nextcloud.api.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "ocs")
@JsonIgnoreProperties({"data"})
public class JsonVoidAnswer extends JsonAnswer {
}
