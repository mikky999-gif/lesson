package org.skypro.lesson.dto.response;

import org.skypro.lesson.model.DynamicRule;

import java.util.List;

public class RulesResponseDTO {
    private List<DynamicRule> data;

    public RulesResponseDTO(List<DynamicRule> data) {
        this.data = data;
    }

    public List<DynamicRule> getData() {
        return data;
    }

    public void setData(List<DynamicRule> data) {
        this.data = data;
    }
}