package com.sepnotican.agi.core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "fieldName")
@ToString
public class CriteriaFilter {
    private String fieldName;
    private String fieldValue;
    private CompareType compateType;
}
