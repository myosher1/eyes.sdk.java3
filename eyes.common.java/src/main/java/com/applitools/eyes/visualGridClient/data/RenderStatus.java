package com.applitools.eyes.visualGridClient.data;

import java.util.HashMap;
import java.util.Map;

public enum RenderStatus {

    NEED_MORE_RESOURCES(1, "need-more-resources"), RENDERING(2, "rendering"), RENDERED(3, "rendered"), ERROR(4, "error");

    private final Integer value;
    private final String text;

    /**
     * A mapping between the integer code and its corresponding text to facilitate lookup by code.
     */
    private static Map<Integer, RenderStatus> valueToTextMapping;

    RenderStatus(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public static RenderStatus getText(Integer i) {
        if (valueToTextMapping == null) {
            initMapping();
        }
        return valueToTextMapping.get(i);
    }

    private static void initMapping() {
        valueToTextMapping = new HashMap<>();
        for (RenderStatus s : values()) {
            valueToTextMapping.put(s.value, s);
        }
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RenderStatus");
        sb.append("{value=").append(value);
        sb.append(", text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}


