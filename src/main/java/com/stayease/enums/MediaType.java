package com.stayease.enums;

/**
 * Enum for property media types
 */
public enum MediaType {
    IMAGE("Image"),
    VIDEO("Video"),
    VIDEO_360("360 Degree Video");

    private final String displayName;

    MediaType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

