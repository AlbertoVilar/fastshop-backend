package com.fastshop.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {

    public ValidationError(String timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path, new ArrayList<>());
    }

    public List<FieldMessage> getErrors() {
        return super.getErrors();
    }

    public void addError(String fieldName, String message) {
        List<FieldMessage> errs = getErrors();
        errs.removeIf(x -> x.getFieldName().equals(fieldName));
        errs.add(new FieldMessage(fieldName, message));
    }
}
