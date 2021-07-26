/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sportnotes.utils.serialization.ExcludeSerialization;

/**
 *
 * @author jaakko
 */
@DatabaseTable(tableName = "player")
public class Player {
    
    @DatabaseField(id = true)
    private String username;
    
    @DatabaseField(canBeNull = false)
    private String name;
    
    @DatabaseField(canBeNull = false)
    private String email;
    
    @ExcludeSerialization
    @DatabaseField(canBeNull = false)
    private String password;
    
    @DatabaseField(canBeNull = false, unique = true)
    private String token;
    
    @DatabaseField(canBeNull = false, foreign = true)
    private Team team;

    public Player() {
    }

    public Player(String username, String name, String email, String password, String token, Team team) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.team = team;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public Team getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }
    
    
    
}
