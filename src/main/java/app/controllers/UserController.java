package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import app.services.Dimensions;
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
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createUserOrLogin", ctx -> ctx.render("createUserOrLogin.html")); //TODO:
        app.get("/createUser", ctx -> ctx.render("createUser.html"));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/createUser", ctx -> createUser(ctx, connectionPool));
        app.get("/index", ctx -> ctx.render("index.html"));

    }

    private static void createUser(@NotNull Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String name = ctx.formParam("name");
        String phoneNumber = ctx.formParam("phoneNumber");
        String text = ctx.formParam("address");

        //Validerer password
        if(password1.equals(password2)){
            try{
                UserMapper.createUser(email,password1, name, phoneNumber, text, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med email: "+ email + ". Du skal nu logge på");
                ctx.render("login.html");}
            catch (DatabaseException e) {
                ctx.attribute("error", "Dit brugernavn findes allerede. Prøv igen, eller log ind");
                ctx.render("createUser.html");
            }
        } else {
            ctx.attribute("error", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createUser.html");
        }
    }


    private static void logout(@NotNull Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }


    private static void login(@NotNull Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("user", user);

            if (user.getRole() == 3 || user.getRole() == 2) {
                loginAdmin(ctx, connectionPool);

            } else if (ctx.sessionAttribute("length") != null ||  //Hvis bare én parameter indeholder noget så går den i makeRequest(), som beder om alle parametre
                    ctx.sessionAttribute("width") != null ||
                    ctx.sessionAttribute("height") != null ||
                    ctx.sessionAttribute("shed") != null ||
                    ctx.sessionAttribute("roof") != null) {
                makeRequest(ctx, user, connectionPool);
            } else{
            ctx.attribute("orders", OrderMapper.getAllRequestsByUserId(user.getUserId(), connectionPool));
            ctx.render("customerRequest.html");}

        } catch (DatabaseException e) {
            ctx.attribute("message", "Log ind var ikke vellykket. Prøv igen eller opret ny bruger.");
            ctx.render("createUserOrLogin.html");
        }
    }


    private static void makeRequest(Context ctx, User user, ConnectionPool connectionPool) throws DatabaseException {
            if (ctx.sessionAttribute("length") == null ||
                    ctx.sessionAttribute("width") == null ||
                    ctx.sessionAttribute("height") == null ||
                    ctx.sessionAttribute("shed") == null ||
                    ctx.sessionAttribute("roof") == null) {
                throw new NullPointerException("Length, Width or Height is not set, prøv igen");
            }else {
                Order order = new Order(user,
                        ctx.sessionAttribute("length"),
                        ctx.sessionAttribute("height"),
                        ctx.sessionAttribute("width"),
                        ctx.sessionAttribute("shed"),
                        ctx.sessionAttribute("roof"));

                OrderMapper.addRequest(order, connectionPool);
                ctx.attribute("orders", OrderMapper.getAllRequestsByUserId(user.getUserId(), connectionPool));
                ctx.render("customerRequest.html");
            }
    }




    private static void loginAdmin(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orderList = OrderMapper.getAllRequests(connectionPool);
        ctx.sessionAttribute("orderList", orderList);
        ctx.render("adminIndex.html");
    }
}

