package org.aarboard.nextcloud.api.utils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;


public class InstantXmlAdapter extends XmlAdapter<Long, Instant>
{
    @Override
    public Long marshal(Instant instant)
    {
        return instant.getEpochSecond();
    }

    @Override
    public Instant unmarshal(Long time)
    {
        return Instant.ofEpochSecond(time);
    }
}
