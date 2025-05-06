package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.persistence.OrderMapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;




public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/loginPage", ctx -> ctx.render("Login.html"));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("createUser.html"));
        app.post("/createuser", ctx -> createUser(ctx, connectionPool));


    }

    private static void createUser(@NotNull Context ctx, ConnectionPool connectionPool) {
    }

    private static void logout(@NotNull Context ctx) {
    }

    private static void login(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        System.out.println(UserMapper.login("mr@hej.dk", "1234", connectionPool));
    }

    private static void loginAdmin(@NotNull Context ctx, ConnectionPool connectionPool) {
    }
}

