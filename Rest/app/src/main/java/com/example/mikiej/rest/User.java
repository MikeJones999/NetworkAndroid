package com.example.mikiej.rest;

import java.io.Serializable;

/**
 * Created by mikieJ on 23/07/2015.
 */

/**
 * Object that mirrors the web server
 */
public class User implements Serializable {

    private String userName;
    //private String email;
    private String password;
    private String userRole;


    //only used for user_roles table - when deleting or changing role status
    private int id;

    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUserRole()
    {
        return userRole;
    }
    public void setUserRole(String userRole)
    {
        this.userRole = userRole;
    }



}
