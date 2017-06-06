package org.aarboard.nextcloud.api.utils;

import java.time.Instant;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
