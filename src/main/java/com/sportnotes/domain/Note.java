/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sportnotes.utils.dao.OffsetDateTimePresister;
import com.sportnotes.utils.serialization.ExcludeSerialization;
import java.time.OffsetDateTime;

/**
 *
 * @author jaakko
 */
@DatabaseTable(tableName = "note")
public class Note {
    
    @DatabaseField(generatedId = true)
    private Long id;
    
    @DatabaseField(canBeNull = false)
    private Long orderno;
    
    @DatabaseField(canBeNull = false)
    private String content;
    
    @DatabaseField(canBeNull = false, persisterClass = OffsetDateTimePresister.class)
    private OffsetDateTime modified;
    
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 1)
    private Player modifier;
    
    @ExcludeSerialization
    @DatabaseField(canBeNull = false, foreign = true, maxForeignAutoRefreshLevel = 1)
    private Team team;

    public Note() {
    }

    public Note(Long orderno, String content, Player creater) {
        this.orderno = orderno;
        this.content = content;
        this.modifier = creater;
        this.modified = OffsetDateTime.now();
        this.team = creater.getTeam();
    }

    public Long getId() {
        return id;
    }

    public Long getOrderno() {
        return orderno;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getModified() {
        return modified;
    }

    public Player getModifier() {
        return modifier;
    }

    public Team getTeam() {
        return team;
    }
    
    
    
}
