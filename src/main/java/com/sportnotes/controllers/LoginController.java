/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.controllers;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.sportnotes.domain.Player;
import com.sportnotes.utils.dao.DaoUtils;
import com.sportnotes.utils.EncryptionUtils;
import com.sportnotes.utils.serialization.SerializationUtils;
import java.util.Map;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

/**
 *
 * @author jaakko
 */
public class LoginController {
    
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    public static void init() {
        Spark.post("/api/login", (req, res) -> {
            Gson gson = SerializationUtils.getBasicSerializer();
            Map<String, String> data = gson.fromJson(req.body(), Map.class);
            try (ConnectionSource cs = DaoUtils.getConnectionSource()) {
                Dao<Player, String> playerDao = DaoManager.createDao(cs, Player.class);
                Player player = playerDao.queryForId(data.get("username"));
                if (player != null && EncryptionUtils.passwordMatchHash(data.get("password"), player.getPassword())) {
                    LOG.info("Login successful!");
                    res.status(HttpStatus.OK_200);
                    return gson.toJson(player);
                }
            }
            LOG.warn("Login failed!");
            res.status(HttpStatus.UNAUTHORIZED_401);
            return gson.toJson(Map.of("message", "Wrong username or password!"));
        });
    }
    
}
