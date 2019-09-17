package org.aarboard.nextcloud.api.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.utils.ConnectorCommon.ResponseParser;

public class XMLAnswerParser<A extends XMLAnswer> implements ResponseParser<A>
{
    private static final Map<String, XMLAnswerParser<? extends XMLAnswer>> PARSERS = new HashMap<>();

    private final JAXBContext jAXBContext;

    public XMLAnswerParser(Class<A> answerClass)
    {
        try {
            jAXBContext = JAXBContext.newInstance(XMLAnswer.class, answerClass);
        } catch (JAXBException e) {
            throw new NextcloudApiException(e);
        }
    }

    public static <A extends XMLAnswer> XMLAnswerParser<A> getInstance(Class<A> answerClass)
    {
        @SuppressWarnings("unchecked")
        XMLAnswerParser<A> parser = (XMLAnswerParser<A>) PARSERS.get(answerClass.getName());
        if (parser == null)
        {
            synchronized (PARSERS)
            {
                if (parser == null)
                {
                    parser = new XMLAnswerParser<>(answerClass);
                    PARSERS.put(answerClass.getName(), parser);
                }
            }
        }
        return parser;
    }

    @Override
    public A parseResponse(Reader xmlStream)
    {
        try {
            return tryParseAnswer(xmlStream);
        } catch (Exception e) {
            throw new NextcloudApiException(e);
        } finally {
            try {
                xmlStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    @SuppressWarnings("unchecked")
    private A tryParseAnswer(Reader xmlStream) throws JAXBException, IOException
    {
        Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
        Object result = unmarshaller.unmarshal(xmlStream);
        return (A) result;
    }
}
