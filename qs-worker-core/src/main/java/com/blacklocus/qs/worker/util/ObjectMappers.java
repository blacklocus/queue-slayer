package com.blacklocus.qs.worker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Bundled {@link ObjectMapper}s
 */
public class ObjectMappers {
    
    private static final ObjectMapper NORMAL = new ObjectMapper();

    public static JsonNode valueToTree(Object fromValue) throws IllegalArgumentException {
        return NORMAL.valueToTree(fromValue);
    }

    public static <T> T treeToValue(TreeNode n, Class<T> valueType) {
        try {
            return NORMAL.treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
