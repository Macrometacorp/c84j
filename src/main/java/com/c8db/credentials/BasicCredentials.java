/**
 * Copyright (c) 2024 Macrometa Corp All rights reserved.
 */
package com.c8db.credentials;

public class BasicCredentials implements C8Credentials {

    private String user;
    private char[] password;

    public BasicCredentials(String user) {
        this.user = user;
    }

    public BasicCredentials(String user, String password) {
        this.password = password != null ? password.toCharArray() : new char[]{};
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password != null ? new String(password) : null;
    }
}
