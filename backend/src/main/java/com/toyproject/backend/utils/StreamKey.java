package com.toyproject.backend.utils;

public enum StreamKey {
    PDF_EVENT("pdf:events"),
    PDF_RESULT("pdf:results");

    private final String key;

    StreamKey(String key) { this.key = key; }
    public String getKey() { return key; }
}
