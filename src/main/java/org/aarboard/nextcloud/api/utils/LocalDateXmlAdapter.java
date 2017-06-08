package org.aarboard.nextcloud.api.utils;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate>
{
    @Override
    public String marshal(LocalDate date)
    {
        return date.toString();
    }

    @Override
    public LocalDate unmarshal(String date)
    {
        return LocalDate.parse(date.substring(0, 10));
    }
}
