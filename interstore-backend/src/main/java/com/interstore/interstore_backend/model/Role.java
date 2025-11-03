package com.interstore.interstore_backend.model;

public enum Role {
    USER,
    ADMIN;

    @Override
    public String toString() {
        return name();
    }
}
