/**
 * Copyright (c) 2024 Macrometa Corp All rights reserved.
 */
package com.c8db.credentials;

public class DefaultCredentials implements C8Credentials {

    private String email;
    private char[] password;
    private String user;

    public DefaultCredentials(String email, String password) {
        this(email, password, null);
    }

    public DefaultCredentials(String email, String password, String user) {
        this.email = email;
        this.password = password != null ? password.toCharArray() : new char[]{};
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password != null ? new String(password) : null;
    }

    public String getUser() {
        return user;
    }
}
