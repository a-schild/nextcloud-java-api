package org.aarboard.nextcloud.api.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;
import org.aarboard.nextcloud.api.utils.ConnectorCommon.ResponseParser;

public class XMLAnswerParser<A extends XMLAnswer> implements ResponseParser<A>
{
    private static final Map<String, XMLAnswerParser<? extends XMLAnswer>> PARSERS = new HashMap<>();

    /**
     * Shared, hardened StAX factory. DTD support and external entity resolution
     * are disabled to prevent XXE attacks from a malicious or MITM'd server
     * response. The factory is only configured once here and thereafter used
     * read-only, which is safe to share across threads.
     */
    private static final XMLInputFactory XML_INPUT_FACTORY = createHardenedInputFactory();

    private final JAXBContext jAXBContext;

    private static XMLInputFactory createHardenedInputFactory()
    {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return factory;
    }

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
              parser = new XMLAnswerParser<>(answerClass);
              PARSERS.put(answerClass.getName(), parser);
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
    private A tryParseAnswer(Reader xmlStream) throws JAXBException, XMLStreamException {
        Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
        // Unmarshal through the hardened StAX reader (not the raw Reader) so
        // DTDs / external entities are never processed.
        XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(xmlStream);
        try {
            return (A) unmarshaller.unmarshal(xmlStreamReader);
        } finally {
            xmlStreamReader.close();
        }
    }
}
