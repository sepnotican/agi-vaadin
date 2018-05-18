package com.sepnotican.agi.core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"targetClass", "fieldName"})
@ToString
public class CriteriaFilter {
    private Class targetClass;
    private String fieldName;
    private Object fieldValue;
    private CompareType compareType;
}
