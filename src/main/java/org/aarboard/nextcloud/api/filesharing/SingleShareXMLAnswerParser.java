package org.aarboard.nextcloud.api.filesharing;

import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SingleShareXMLAnswerParser extends XMLAnswerParser<SingleShareXMLAnswer>
{
    private static final SingleShareXMLAnswerParser INSTANCE = new SingleShareXMLAnswerParser();

    public static SingleShareXMLAnswerParser getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void handleDataPart(Node otherDataNode, SingleShareXMLAnswer answer) {
        // Start share element
        NodeList shareNodes= otherDataNode.getChildNodes();
        answer.share= parseSingleShare(shareNodes);
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

    @Override
    protected SingleShareXMLAnswer createAnswer() {
        return new SingleShareXMLAnswer();
    }
}
