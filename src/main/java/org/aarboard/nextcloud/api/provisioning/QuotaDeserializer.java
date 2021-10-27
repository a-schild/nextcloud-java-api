package org.aarboard.nextcloud.api.provisioning;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.Optional;

public class QuotaDeserializer extends JsonDeserializer<Optional<Long>> {

    @Override
    public Optional<Long> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Long result = null;
        String quota = jsonParser.getValueAsString();
        if (NumberUtils.isCreatable(quota)) {
            result = NumberUtils.createLong(quota);
        }

        return Optional.ofNullable(result);
    }

}
