package org.aarboard.nextcloud.api.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.aarboard.nextcloud.api.exception.NextcloudApiException;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class JsonAnswerParser<A extends JsonAnswer> implements ConnectorCommon.ResponseParser<A> {

    private static final Map<String, JsonAnswerParser<? extends JsonAnswer>> PARSERS = new HashMap<>();

    private final ObjectReader objectReader;

    private JsonAnswerParser(Class<A> answerClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);

        objectReader = objectMapper.readerFor(answerClass);
    }

    public static <A extends JsonAnswer> JsonAnswerParser<A> getInstance(Class<A> answerClass) {
        @SuppressWarnings("unchecked")
        JsonAnswerParser<A> parser = (JsonAnswerParser<A>) PARSERS.get(answerClass.getName());
        if (parser == null) {
            synchronized (PARSERS) {
              parser = new JsonAnswerParser<>(answerClass);
              PARSERS.put(answerClass.getName(), parser);
            }
        }
        return parser;
    }

    @Override
    public A parseResponse(Reader reader) {
        try (Reader response = reader) {
            return objectReader.readValue(response);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
}
