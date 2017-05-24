package org.aarboard.nextcloud.api.provisioning;

import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UsersXMLAnswerParser extends XMLAnswerParser<UsersXMLAnswer> {
    private static final Log LOG = LogFactory.getLog(UsersXMLAnswerParser.class);
    private static final UsersXMLAnswerParser INSTANCE = new UsersXMLAnswerParser();

    public static UsersXMLAnswerParser getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void handleDataPart(Node dataNode, UsersXMLAnswer answer)
    {
        NodeList users= dataNode.getChildNodes();
        for (int i=0; i < users.getLength(); i++)
        {
            Node n= users.item(i);
            switch (n.getNodeName())
            {
                case "users" : // Handle user
                handleUser(answer, n);
                    break;
                case "#text": // Ignore;
                    break;
                default:
                    LOG.warn("Unhandled user tag <"+n.getNodeName()+">");
            }
        }
    }

    private void handleUser(UsersXMLAnswer answer, Node n)
    {
        NodeList nU= n.getChildNodes();
        for (int j=0; j < nU.getLength(); j++)
        {
            Node no= nU.item(j);
            if (no.getNodeName().equals("#text"))
            {
                // Ignore
            }
            else if (no.getNodeName().equals("element"))
            {
                User u= new User();
                u.setUserId(no.getTextContent());
                answer.userList.add(u);
            }
            else
            {
                LOG.warn("Unhandled user tag <"+no.getNodeName()+">");
            }
        }
    }

    @Override
    protected UsersXMLAnswer createAnswer()
    {
        return new UsersXMLAnswer();
    }
}
