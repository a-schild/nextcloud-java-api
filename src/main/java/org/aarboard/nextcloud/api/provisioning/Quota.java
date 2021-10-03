package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quota {
    private long free;
    private long used;
    private long total;
    private float relative;

    public long getFree() {
        return free;
    }

    public long getUsed() {
        return used;
    }

    public long getTotal() {
        return total;
    }

    public float getRelative() {
        return relative;
    }
}
