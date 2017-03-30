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
package org.aarboard.nextcloud.api.provisioning;

import java.util.LinkedList;
import java.util.List;
import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author a.schild
 */
public class GroupsXMLAnswer extends XMLAnswer {
    private final static Log LOG = LogFactory.getLog(GroupsXMLAnswer.class);
    List<Group>  groupList= new LinkedList<>();
    
    public GroupsXMLAnswer() {
    }

    @Override
    protected void handleDataPart(Node dataNode) {
        NodeList groups= dataNode.getChildNodes();
        for (int i=0; i < groups.getLength(); i++)
        {
            Node n= groups.item(i);
            switch (n.getNodeName())
            {
                case "groups" : // Handle group
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
                            groupList.add(g);
                        }
                        else
                        {
                            LOG.warn("Unhandled group tag <"+no.getNodeName()+">");
                        }
                    }
                    break;
                case "#text": // Ignore;
                    break;
                default:
                    LOG.warn("Unhandled group tag <"+n.getNodeName()+">");
            }
        }
    }
    
    public List<Group> getGroups()
    {
        return groupList;
    }
    
    
}
