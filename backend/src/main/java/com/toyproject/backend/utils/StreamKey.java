package com.toyproject.backend.utils;

public enum StreamKey {
    PDF_EVENT("pdf:events"),
    CHAT_MESSAGE("chat:messages");

    private final String key;

    StreamKey(String key) { this.key = key; }
    public String getKey() { return key; }
}
