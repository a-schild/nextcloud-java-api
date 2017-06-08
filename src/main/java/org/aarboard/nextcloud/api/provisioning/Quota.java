package org.aarboard.nextcloud.api.provisioning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
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
