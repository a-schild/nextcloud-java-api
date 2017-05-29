package org.aarboard.nextcloud.api.filesharing;

import org.aarboard.nextcloud.api.utils.XMLAnswerParser;
import org.w3c.dom.Node;

public class SharesXMLAnswerParser extends XMLAnswerParser<SharesXMLAnswer>
{
    private static final SharesXMLAnswerParser INSTANCE = new SharesXMLAnswerParser();

    public static SharesXMLAnswerParser getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void handleUnknownDataNode(Node otherDataNode, SharesXMLAnswer answer)
    {
        if (otherDataNode.getNodeName().equals("element"))
        {
            // Start share element
            Share s= SingleShareXMLAnswerParser.parseSingleShare(otherDataNode.getChildNodes());
            answer.shareList.add(s);
        }
        else
        {
            super.handleUnknownDataNode(otherDataNode, answer);
        }
    }

    @Override
    protected SharesXMLAnswer createAnswer()
    {
        return new SharesXMLAnswer();
    }
}
