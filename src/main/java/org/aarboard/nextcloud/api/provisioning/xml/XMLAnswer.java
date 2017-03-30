/* 
 * Copyright (C) 2017 a.schild
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aarboard.nextcloud.api.provisioning.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author a.schild
 */
public class XMLAnswer {
    private final Log LOG = LogFactory.getLog(XMLAnswer.class);
    
    private String status= null;
    private int statusCode= 500;
    private String message= null;
    private int totalItems= -1;
    private int itemsPerPage= -1;
    
    private List<String> elements= new LinkedList<>();

    public XMLAnswer(String xmlAnswer) 
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlAnswer));
            try {
                Document doc = db.parse(is);
                NodeList nl= doc.getDocumentElement().getElementsByTagName("status");
                if (nl != null && nl.getLength() > 0)
                {
                    status= nl.item(0).getTextContent();
                }
                nl= doc.getDocumentElement().getElementsByTagName("statuscode");
                if (nl != null && nl.getLength() > 0)
                {
                    statusCode= Integer.parseInt(nl.item(0).getTextContent());
                }
                nl= doc.getDocumentElement().getElementsByTagName("itemsperpage");
                if (nl != null && nl.getLength() > 0 && !nl.item(0).getTextContent().isEmpty())
                {
                    itemsPerPage= Integer.parseInt(nl.item(0).getTextContent());
                }
                nl= doc.getDocumentElement().getElementsByTagName("totalItems");
                if (nl != null && nl.getLength() > 0 && !nl.item(0).getTextContent().isEmpty())
                {
                    totalItems= Integer.parseInt(nl.item(0).getTextContent());
                }
                nl= doc.getDocumentElement().getElementsByTagName("message");
                if (nl != null && nl.getLength() > 0)
                {
                    message= nl.item(0).getTextContent();
                }
                nl= doc.getDocumentElement().getElementsByTagName("element");
                if (nl != null && nl.getLength() > 0)
                {
                    for (int i= 0; i < nl.getLength(); i++)
                    {
                        elements.add(nl.item(i).getTextContent());
                    }
                }
                //System.out.println(message);
            } catch (SAXException e) {
                LOG.error("SAX exception", e);
            } catch (IOException e) {
                // handle IOException
                LOG.error("IO exception", e);
            }
        } catch (ParserConfigurationException e1) {
            // handle ParserConfigurationException
            LOG.error("Parser config exception", e1);
        }        
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the totalItems
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * @return the itemsPerPage
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * @return the elements
     */
    public List<String> getElements() {
        return elements;
    }
}
