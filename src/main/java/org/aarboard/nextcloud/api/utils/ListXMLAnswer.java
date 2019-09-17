package org.aarboard.nextcloud.api.utils;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "ocs")
public class ListXMLAnswer extends XMLAnswer
{
    @XmlElementWrapper(name = "data")
    @XmlElement(name = "element")
    private List<String> result;

    public List<String> getResult() {
        return result;
    }
}
