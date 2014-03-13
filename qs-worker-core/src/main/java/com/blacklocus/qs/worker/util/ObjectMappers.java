package com.blacklocus.qs.worker.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMappers {
    
    private static final ObjectMapper NORMAL = new ObjectMapper();

    public static <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
        return NORMAL.valueToTree(fromValue);
    }
}
