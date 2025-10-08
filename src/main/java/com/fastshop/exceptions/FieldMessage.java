package com.fastshop.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FieldMessage {

    private String FieldName;
    private String message;


}
