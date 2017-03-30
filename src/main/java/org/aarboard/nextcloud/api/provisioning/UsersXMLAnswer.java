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
public class UsersXMLAnswer extends XMLAnswer {
    private final static Log LOG = LogFactory.getLog(UsersXMLAnswer.class);

    List<User>  userList= new LinkedList<>();
    
    public UsersXMLAnswer() {
    }

    @Override
    protected void handleDataPart(Node dataNode) {
        NodeList users= dataNode.getChildNodes();
        for (int i=0; i < users.getLength(); i++)
        {
            Node n= users.item(i);
            switch (n.getNodeName())
            {
                case "users" : // Handle user
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
                            userList.add(u);
                        }
                        else
                        {
                            LOG.warn("Unhandled user tag <"+no.getNodeName()+">");
                        }
                    }
                    break;
                case "#text": // Ignore;
                    break;
                default:
                    LOG.warn("Unhandled user tag <"+n.getNodeName()+">");
            }
        }
    }
    
    public List<User> getUsers()
    {
        return userList;
    }
    
    
}
