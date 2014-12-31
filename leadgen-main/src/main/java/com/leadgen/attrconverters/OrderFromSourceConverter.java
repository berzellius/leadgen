package com.leadgen.attrconverters;

import com.leadgen.json.OrderFromSource;
import org.codehaus.jackson.map.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

/**
 * Created by berz on 16.11.14.
 */
@Converter(autoApply = true)
public class OrderFromSourceConverter implements AttributeConverter<OrderFromSource, String> {
    @Override
    public String convertToDatabaseColumn(OrderFromSource orderFromSource) {
        ObjectMapper objectMapper = new ObjectMapper();
        String s;

        // На null и суда null
        if(orderFromSource == null) return null;

        try {
            s = objectMapper.writeValueAsString(orderFromSource);
        } catch (IOException e) {
            s = null;
        }

        return s;
    }

    @Override
    public OrderFromSource convertToEntityAttribute(String s) {

        ObjectMapper objectMapper = new ObjectMapper();
        OrderFromSource orderFromSource;

        // просто null
        if(s == null) return null;

        try {
            orderFromSource = objectMapper.readValue(s, OrderFromSource.class);
        } catch (IOException e) {
            orderFromSource = null;
        }

        return orderFromSource;
    }
}
