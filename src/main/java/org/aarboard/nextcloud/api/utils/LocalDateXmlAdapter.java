package org.aarboard.nextcloud.api.utils;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;


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
