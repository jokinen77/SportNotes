/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes;

import com.sportnotes.controllers.LoginController;
import com.sportnotes.sockets.NotesWebSocketHandler;
import com.sportnotes.utils.FileUtils;
import java.util.Arrays;
import spark.Spark;

/**
 *
 * @author jaakko
 */
public class Main {
    
    public static void main(String[] args) {
        
        Spark.webSocket("/socket/test", NotesWebSocketHandler.class);
        Spark.externalStaticFileLocation("public");
        Spark.init();
        
        Spark.get("/hello", (req, res) -> "Hello World");
        LoginController.init();
        
        Spark.after("/api/*", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
        });
        for (String path : Arrays.asList("/", "/login", "/note", "/settings")) {
            Spark.get(path, (req, res) -> FileUtils.getFile("public/index.html"));
        }
        //Spark.get("*", (req, res) -> FileUtils.getFile("public/index.html"));
        
    }
}
