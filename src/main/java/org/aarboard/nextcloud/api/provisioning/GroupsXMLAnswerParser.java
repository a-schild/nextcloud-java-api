package org.aarboard.nextcloud.api.provisioning;

import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GroupsXMLAnswerParser extends XMLAnswerParser<GroupsXMLAnswer> {
    private static final Log LOG = LogFactory.getLog(GroupsXMLAnswerParser.class);
    private static final GroupsXMLAnswerParser INSTANCE = new GroupsXMLAnswerParser();

    public static GroupsXMLAnswerParser getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void handleDataPart(Node dataNode, GroupsXMLAnswer answer)
    {
        NodeList groups= dataNode.getChildNodes();
        for (int i=0; i < groups.getLength(); i++)
        {
            Node n= groups.item(i);
            switch (n.getNodeName())
            {
                case "groups" : // Handle group
                    handleGroup(answer, n);
                    break;
                case "#text": // Ignore;
                    break;
                default:
                    LOG.warn("Unhandled group tag <"+n.getNodeName()+">");
            }
        }
    }

    private void handleGroup(GroupsXMLAnswer answer, Node n)
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
                Group g= new Group();
                g.setGroupId(no.getTextContent());
                answer.groupList.add(g);
            }
            else
            {
                LOG.warn("Unhandled group tag <"+no.getNodeName()+">");
            }
        }
    }

    @Override
    protected GroupsXMLAnswer createAnswer()
    {
        return new GroupsXMLAnswer();
    }
}
