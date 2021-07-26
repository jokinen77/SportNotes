/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.utils.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sportnotes.domain.Note;
import com.sportnotes.domain.Player;
import com.sportnotes.domain.Team;
import com.sportnotes.utils.EncryptionUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jaakko
 */
public class DaoUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(DaoUtils.class);
    
    public static Player getPlayerByToken(String token) {
        try (ConnectionSource cs = getConnectionSource()) {
            Dao<Player, String> dao = DaoManager.createDao(cs, Player.class);
            QueryBuilder<Player, String> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("token", token);
            PreparedQuery<Player> preparedQuery = queryBuilder.prepare();
            Player player = dao.queryForFirst(preparedQuery);

            Dao<Team, String> teamDao = DaoManager.createDao(cs, Team.class);
            teamDao.refresh(player.getTeam());

            return player;
        } catch (SQLException | IOException ex) {
            LOG.error("Cannot get player by token!", ex);
        }
        return null;
    }

    public static void createNote(Note note) {
        try (ConnectionSource cs = getConnectionSource()) {
            Dao<Note, Long> dao = DaoManager.createDao(cs, Note.class);
            dao.create(note);
        } catch (SQLException | IOException ex) {
            LOG.error("Cannot create note!", ex);
        }
    }

    public static void deleteNote(Long id) {
        try (ConnectionSource cs = getConnectionSource()) {
            Dao<Note, Long> dao = DaoManager.createDao(cs, Note.class);
            dao.deleteById(id);
        } catch (SQLException | IOException ex) {
            LOG.error("Cannot create note!", ex);
        }
    }
    
    public static ConnectionSource getConnectionSource() throws SQLException {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && !dbUrl.isEmpty()) {
            return new JdbcConnectionSource(dbUrl);
        }
        dbUrl = "jdbc:sqlite:sport_notes.db";
        ConnectionSource cs = new JdbcConnectionSource(dbUrl);
        return cs;
    }
    
    private static void createTable(Class clazz) {
        try (ConnectionSource cs = getConnectionSource()) {
            TableUtils.createTable(cs, clazz);
        } catch (SQLException|IOException e) {
            LOG.warn("Cannot create table!", e);
        }
    }
    
    public static void main(String[] args) throws SQLException {
        createTable(Team.class);
        createTable(Player.class);
        createTable(Note.class);
        
        Team team = new Team("Test team");
        Player player = new Player("tester", "jester tester", "tester@tester.com", EncryptionUtils.hashPassword("tester"), EncryptionUtils.generateToken(), team);
        Player player1 = new Player("tester1", "jester tester", "tester1@tester.com", EncryptionUtils.hashPassword("tester1"), EncryptionUtils.generateToken(), team);
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            notes.add(new Note((long) i, "I spare my readers the account of my delight on coming home, my happiness while there, and my sorrow on being obliged to bid them, once more, a long adieu. " + i, player));
        }
        
        try (ConnectionSource cs = getConnectionSource()) {
            Dao<Team, String> teamDao = DaoManager.createDao(cs, Team.class);
            teamDao.create(team);
            
            Dao<Player, String> playerDao = DaoManager.createDao(cs, Player.class);
            playerDao.create(player);
            playerDao.create(player1);
            
            Dao<Note, Long> noteDao = DaoManager.createDao(cs, Note.class);
            noteDao.create(notes);
        } catch (SQLException|IOException e) {
            LOG.warn("Cannot create table!", e);
        }
        
        try (ConnectionSource cs = getConnectionSource()) {
            Dao<Team, String> teamDao = DaoManager.createDao(cs, Team.class);
            Team team1 = teamDao.queryForId(team.getName());
            System.out.println("Players in team:");
            team1.getPlayers().stream().forEach((p) -> {
                System.out.println("Username: " + p.getUsername());
            });
            
            Dao<Player, String> playerDao = DaoManager.createDao(cs, Player.class);
            Player player2 = playerDao.queryForId(player.getUsername());
            System.out.println("Player: " + player2.getTeam().getName());
        } catch (SQLException|IOException e) {
            LOG.warn("Cannot create table!", e);
        }
    }
    
}
