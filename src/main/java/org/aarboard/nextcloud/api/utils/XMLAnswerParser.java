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

public abstract class XMLAnswerParser<A extends XMLAnswer> implements ResponseParser<A> {
    private final Log LOG = LogFactory.getLog(XMLAnswerParser.class);
    private static final XMLAnswerParser<XMLAnswer> INSTANCE = new DefaultXmlParser();

    protected abstract A createAnswer();

    public static XMLAnswerParser<XMLAnswer> getDefaultInstance()
    {
        return INSTANCE;
    }

    @Override
    public A parseResponse(Reader xmlStream)
    {
        A answer = createAnswer();
        try {
            tryParseAnswer(xmlStream, answer);
        } catch (Exception e) {
            throw new NextcloudApiException(e);
        } finally {
            try {
                xmlStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        return answer;
    }

    private void tryParseAnswer(Reader xmlStream, A answer) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(xmlStream);
        Document doc = db.parse(is);
        Node rootNode= doc.getFirstChild(); // OCS root tag
        if (rootNode.getNodeName().equals("ocs"))
        {
            handleOCS(rootNode, answer);
        }
        else
        {
            throw new IllegalArgumentException("Root tag in answer is not <ocs> but <"+rootNode.getNodeName());
        }
    }

    private void handleOCS(Node rootNode, A answer) {
        NodeList ocsChildren= rootNode.getChildNodes();
        for (int i= 0; i < ocsChildren.getLength(); i++)
        {
            Node n= ocsChildren.item(i);
            if (n.getNodeName().equals("meta"))
            {
                handleMetaPart(n, answer);
            }
            else if (n.getNodeName().equals("data"))
            {
                handleDataPart(n, answer);
            }
            else
            {
                handleOtherPart(n, answer);
            }
        }
    }

    /**
     * Override this method, if you need to handle other tags that meta and data in the root level answer
     *
     * @param otherNode
     */
    protected void handleOtherPart(Node otherNode, A answer)
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
    protected void handleUnknownMetaNode(Node otherMetaNode, A answer)
    {
        LOG.warn("Unhandled meta node with name <"+otherMetaNode.getNodeName()+">");
    }

    /**
     * Override this method, if you need to handle other meta tags
     *
     * @param otherDataNode
     */
    protected void handleUnknownDataNode(Node otherDataNode, A answer)
    {
        LOG.warn("Unhandled data node with name <"+otherDataNode.getNodeName()+">");
    }

    /**
     * Override this method, if you need to handle other meta tags
     *
     * @param otherElementNode
     */
    protected void handleUnknownElementNode(Node otherElementNode, A answer)
    {
        LOG.warn("Unhandled data node with name <"+otherElementNode.getNodeName()+">");
    }

    protected void handleMetaPart(Node metaNode, A answer)
    {
        NodeList metaChildren= metaNode.getChildNodes();
        for (int i= 0; i < metaChildren.getLength(); i++)
        {
            Node n= metaChildren.item(i);
            switch (n.getNodeName())
            {
                case "status":
                            answer.setStatus(n.getTextContent());
                            break;
                case "statuscode":
                            answer.setStatusCode(Integer.parseInt(n.getTextContent()));
                            break;
                case "itemsperpage":
                            if (!n.getTextContent().isEmpty())
                            {
                                answer.setItemsPerPage(Integer.parseInt(n.getTextContent()));
                            }
                            break;
                case "totalitems":
                            if (!n.getTextContent().isEmpty())
                            {
                                answer.setTotalItems(Integer.parseInt(n.getTextContent()));
                            }
                            break;
                case "message":
                            answer.setMessage(n.getTextContent());
                            break;
                case "#text": // Ignore text
                            break;
                default:
                    handleUnknownMetaNode(n, answer);
            }
        }
    }

    protected void handleDataPart(Node dataNode, A answer)
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
                    handleUnknownDataNode(n, answer);
            }
        }
    }

    private static final class DefaultXmlParser extends XMLAnswerParser<XMLAnswer> {
        @Override
        protected XMLAnswer createAnswer()
        {
            return new XMLAnswer();
        }
    }
}
