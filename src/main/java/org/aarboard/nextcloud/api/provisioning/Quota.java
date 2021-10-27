package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quota {
    @JsonDeserialize(using = QuotaDeserializer.class)
    private Optional<Long> quota = Optional.empty();
    private long free;
    private long used;
    private long total;
    private float relative;

    public Optional<Long> getQuota() {
        return quota;
    }

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
