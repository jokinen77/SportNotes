package com.sportnotes.sockets;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.sportnotes.domain.Note;
import com.sportnotes.domain.Player;
import com.sportnotes.domain.Team;
import com.sportnotes.utils.dao.DaoUtils;
import com.sportnotes.utils.serialization.SerializationUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.eclipse.jetty.websocket.api.CloseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jaakko
 */
@WebSocket
public class NotesWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NotesWebSocketHandler.class);

    private static ConcurrentHashMap<String, Session> usernameToSession = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Set<Session>> teamNameToSession = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        user.setIdleTimeout(10 * 60 * 1000);
        LOG.info("User connected!");
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) throws IOException {
        LOG.warn("User session closed! statusCode: " + statusCode + ", reason: " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) throws SQLException, IOException {
        if (!SerializationUtils.isJSONValid(message)) {
            LOG.warn("Message wasn't in JSON format: " + message);
            return;
        }
        Gson gson = SerializationUtils.getBasicSerializer();
        Map<String, Object> messageMap = gson.fromJson(message, Map.class);
        
        String token = messageMap.get("token").toString();
        String action = messageMap.get("action").toString().toUpperCase();
        if (token == null || action == null) return;
        
        Player player = getPlayerByToken(token);
        if (player == null) return;
        
        Team team = player.getTeam();
        if (team == null) return;
        
        usernameToSession.put(player.getUsername(), user);
        if (teamNameToSession.get(team.getName()) == null) {
            teamNameToSession.put(team.getName(), Collections.synchronizedSet(new HashSet<>()));
        }
        teamNameToSession.get(team.getName()).add(user);

        switch (action) {
            case "GET_NOTES":
                List<Note> notes = team.getNotes().stream().collect(Collectors.toCollection(ArrayList::new));
                notes.sort(Comparator.comparing(Note::getOrderno).reversed());
                user.getRemote().sendString(gson.toJson(Map.of("action", "NOTES", "notes", notes)));
                break;
            case "CREATE_NOTE":
                Long maxOrderno = team.getNotes().stream().mapToLong(o -> o.getOrderno()).max().orElse(1);
                String content = messageMap.get("content").toString();
                Note note = new Note(maxOrderno + 1, content, player);
                createNote(note);
                broadcastMessageToTeam(team.getName(), Map.of("action", "NOTE", "note", note));
                break; 
            default:
                LOG.warn("Cannot handle message: " + message);
        }
    }

    @OnWebSocketError
    public void onSocketError(Session user, Throwable e) {
        if (e instanceof CloseException) {
            LOG.warn("User idle timeout!");
        } else {
            LOG.error("Socket error!", e);
        }
    }

    private static void broadcastMessageToTeam(String teamName, Object message) {
        Gson gson = SerializationUtils.getBasicSerializer();
        Set<Session> sessions = teamNameToSession.get(teamName);
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(gson.toJson(message));
                } catch (IOException e) {
                    LOG.error("Failed to send message!", e);
                }
            }
        }
    }

    private static Player getPlayerByToken(String token) {
        try (ConnectionSource cs = DaoUtils.getConnectionSource()) {
            Dao<Player, String> dao = DaoManager.createDao(cs, Player.class);
            QueryBuilder<Player, String> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("token", token);
            PreparedQuery<Player> preparedQuery = queryBuilder.prepare();
            Player player = dao.queryForFirst(preparedQuery);
            
            Dao<Team, String> teamDao = DaoManager.createDao(cs, Team.class);
            teamDao.refresh(player.getTeam());
            
            return player;
        } catch (SQLException|IOException ex) {
            LOG.error("Cannot get player by token!", ex);
        }
        return null;
    }
    
    private static void createNote(Note note) {
        try (ConnectionSource cs = DaoUtils.getConnectionSource()) {
            Dao<Note, Long> dao = DaoManager.createDao(cs, Note.class);
            dao.create(note);
        } catch (SQLException|IOException ex) {
            LOG.error("Cannot create note!", ex);
        }
    }
}