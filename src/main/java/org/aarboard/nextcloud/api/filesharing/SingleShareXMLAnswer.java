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
package org.aarboard.nextcloud.api.filesharing;

import org.aarboard.nextcloud.api.utils.XMLAnswer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author a.schild
 */
public class SingleShareXMLAnswer extends XMLAnswer
{

    protected Share share= null;
    
    public SingleShareXMLAnswer() {
    }

    public Share getShare() {
        return share;
    }

    @Override
    protected void handleDataPart(Node otherDataNode) {
        // Start share element
        NodeList shareNodes= otherDataNode.getChildNodes();
        share= parseSingleShare(shareNodes);
    }
    
    protected static Share parseSingleShare(NodeList shareNodes)
    {
        Share s= new Share();
        for (int i= 0; i < shareNodes.getLength(); i++)
        {
            Node n= shareNodes.item(i);
            switch (n.getNodeName())
            {
                case "id":
                        s.setId(Integer.parseInt(n.getTextContent()));
                        break;
                case "share_type":
                        s.setShareType(ShareType.getShareTypeForIntValue(Integer.parseInt(n.getTextContent())));
                        break;
                case "uid_owner": 
                        s.setOwnerId(n.getTextContent());
                        break;
                case "displayname_owner":
                        s.setOwnerDisplayName(n.getTextContent());
                        break;
                case "permissions": 
                        s.setSharePermissions(new SharePermissions(Integer.parseInt(n.getTextContent())));
                        break;
                case  "uid_file_owner":
                        s.setFileOwnerId(n.getTextContent());
                        break;
                case  "displayname_file_owner":
                        s.setFileOwnerDisplayName(n.getTextContent());
                        break;
                case  "path":
                        s.setPath(n.getTextContent());
                        break;
                case  "item_type":
                        s.setItemType(ItemType.getItemByName(n.getTextContent()));
                        break;
                case  "file_target":
                        s.setFileTarget(n.getTextContent());
                        break;
                case  "share_with":
                        s.setShareWithId(n.getTextContent());
                        break;
                case  "share_with_displayname":
                        s.setShareWithDisplayName(n.getTextContent());
                        break;
                       // Ignore the following for now
                case  "stime": ;
                case  "parent": ;
                case  "expiration": ;
                case  "token": 
	                    s.setToken(n.getTextContent());
	                    break;
                case  "mimetype": ;
                case  "storage_id": ;
                case  "storage": ;
                case  "item_source": ;
                case  "file_source": ;
                case  "file_parent": ;
                case  "mail_send": ;
                            break;
                case "#text": // Ignore text
                            break;
                default:
                    break;
            }
        }
        return s;
    }
}
