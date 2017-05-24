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
package org.aarboard.nextcloud.api.utils;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.utils.ConnectorCommon.ResponseParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author a.schild
 */
public class XMLAnswer implements ResponseParser<XMLAnswer> {
    private final Log LOG = LogFactory.getLog(XMLAnswer.class);
    
    private String status= null;
    private int statusCode= -1;
    private String message= null;
    private int totalItems= -1;
    private int itemsPerPage= -1;
    
    public XMLAnswer() {
    }

    @Override
    public XMLAnswer parseResponse(Reader xmlStream)
    {
        try {
            tryParseAnswer(xmlStream);
        } catch (Exception e) {
            throw new NextcloudApiException(e);
        } finally {
            try {
                xmlStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        return this;
    }

    private void tryParseAnswer(Reader xmlStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(xmlStream);
        Document doc = db.parse(is);
        Node rootNode= doc.getFirstChild(); // OCS root tag
        if (rootNode.getNodeName().equals("ocs"))
        {
            handleOCS(rootNode);
        }
        else
        {
            throw new IllegalArgumentException("Root tag in answer is not <ocs> but <"+rootNode.getNodeName());
        }
    }

    private void handleOCS(Node rootNode) {
        NodeList ocsChildren= rootNode.getChildNodes();
        for (int i= 0; i < ocsChildren.getLength(); i++)
        {
            Node n= ocsChildren.item(i);
            if (n.getNodeName().equals("meta"))
            {
                handleMetaPart(n);
            }
            else if (n.getNodeName().equals("data"))
            {
                handleDataPart(n);
            }
            else
            {
                handleOtherPart(n);
            }
        }
    }

    /**
     * Override this method, if you need to handle other tags that meta and data in the root level answer
     * 
     * @param otherNode 
     */
    protected void handleOtherPart(Node otherNode)
    {
        if (otherNode.getNodeName().equals("#text"))
        {
            // Ignore text
        }
        else
        {
            LOG.warn("Unhandled root node with name <"+otherNode.getNodeName()+">");
        }
    }

    /**
     * Override this method, if you need to handle other meta tags 
     * 
     * @param otherMetaNode 
     */
    protected void handleUnknownMetaNode(Node otherMetaNode)
    {
        LOG.warn("Unhandled meta node with name <"+otherMetaNode.getNodeName()+">");
    }

    /**
     * Override this method, if you need to handle other meta tags 
     * 
     * @param otherDataNode 
     */
    protected void handleUnknownDataNode(Node otherDataNode)
    {
        LOG.warn("Unhandled data node with name <"+otherDataNode.getNodeName()+">");
    }

    /**
     * Override this method, if you need to handle other meta tags 
     * 
     * @param otherElementNode 
     */
    protected void handleUnknownElementNode(Node otherElementNode)
    {
        LOG.warn("Unhandled data node with name <"+otherElementNode.getNodeName()+">");
    }

    
    protected void handleMetaPart(Node metaNode)
    {
        NodeList metaChildren= metaNode.getChildNodes();
        for (int i= 0; i < metaChildren.getLength(); i++)
        {
            Node n= metaChildren.item(i);
            switch (n.getNodeName())
            {
                case "status":
                            status= n.getTextContent();
                            break;
                case "statuscode":
                            statusCode= Integer.parseInt(n.getTextContent());
                            break;
                case "itemsperpage":
                            if (!n.getTextContent().isEmpty())
                            {
                                itemsPerPage= Integer.parseInt(n.getTextContent());
                            }
                            break;
                case "totalitems":
                            if (!n.getTextContent().isEmpty())
                            {
                                totalItems= Integer.parseInt(n.getTextContent());
                            }
                            break;
                case "message":
                            message= n.getTextContent();
                            break;
                case "#text": // Ignore text
                            break;
                default:
                    handleUnknownMetaNode(n);
            }
        }
    }

    protected void handleDataPart(Node dataNode)
    {
        NodeList metaChildren= dataNode.getChildNodes();
        for (int i= 0; i < metaChildren.getLength(); i++)
        {
            Node n= metaChildren.item(i);
            switch (n.getNodeName())
            {
                case "#text": // Ignore text
                            break;
                default:
                    handleUnknownDataNode(n);
            }
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

}
