/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.domain;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.sportnotes.utils.serialization.ExcludeSerialization;

/**
 *
 * @author jaakko
 */
@DatabaseTable(tableName = "team")
public class Team {
    
    @DatabaseField(id = true)
    private String name;
    
    @ExcludeSerialization
    @ForeignCollectionField(eager = false)
    private ForeignCollection<Player> players;
    
    @ExcludeSerialization
    @ForeignCollectionField(eager = false, orderColumnName = "orderno")
    private ForeignCollection<Note> notes;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ForeignCollection<Player> getPlayers() {
        return players;
    }

    public ForeignCollection<Note> getNotes() {
        return notes;
    }
    
    
}
