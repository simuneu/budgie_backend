package com.budgie.server.converter;

import com.budgie.server.enums.CategoryName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoryNameConverter implements AttributeConverter<CategoryName, String> {

    @Override
    public String convertToDatabaseColumn(CategoryName attribute) {
        return attribute != null ? attribute.getLabel() : null;
    }

    @Override
    public CategoryName convertToEntityAttribute(String dbData){
        if(dbData == null) return null;
        for(CategoryName name : CategoryName.values()){
            if(name.getLabel().equals(dbData)){
                return name;
            }
        }
        throw new IllegalArgumentException("카테고리 네임을 알 수 없음"+dbData);
    }
}
