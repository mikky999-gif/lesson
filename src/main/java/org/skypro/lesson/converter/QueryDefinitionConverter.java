package org.skypro.lesson.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.skypro.lesson.model.DynamicRule;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Converter(autoApply = true)
public class QueryDefinitionConverter implements AttributeConverter<List<DynamicRule.QueryDefinition>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<DynamicRule.QueryDefinition> attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DynamicRule.QueryDefinition> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? Collections.emptyList() :
                    objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, DynamicRule.QueryDefinition.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}