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
        app.post("/Login", ctx -> login(ctx, connectionPool));
        //app.get("/loginPage", ctx -> ctx.render("Login.html"));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/loginOpretBruger", ctx -> ctx.render("OpretBrugerEllerLogin.html"));
        app.get("OpretBruger", ctx -> ctx.render("createUser.html"));
        app.get("Login", ctx -> ctx.render("Login.html"));
        app.post("/createUser", ctx -> createUser(ctx, connectionPool));
        app.get("/Customsite", ctx -> ctx.render("CustomMadeSite.html"));
        app.post("/seForesporgsel", ctx -> ctx.render("StatusSide.html"));


    }

    private static void createUser(@NotNull Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");



        //Validerer password
        if(password1.equals(password2)){
            try{
                UserMapper.createUser(username,password1,connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: "+ username+ ". Du skal nu logge på");
                ctx.render("Login.html");}
            catch (DatabaseException e) {
                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind");
                ctx.render("createUser.html");
            }
        } else {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createUser.html");
        }
    }




    private static void logout(@NotNull Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }





    private static void login(@NotNull Context ctx, ConnectionPool connectionPool) {
        String username= ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            User user = UserMapper.login(username, password, connectionPool);
            //Når user bliver oprettet, bliver der også oprettet en basket.
            int role = user.getRole();
            if(role == 3){
                loginAdmin(ctx,connectionPool);
            } else{

                ctx.render("OversigtCustomer.html");
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("OpretBrugerEllerLogin.html");
        }

    }




    private static void loginAdmin(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        //List<Order> orderList = OrderMapper.getAllRequest(connectionPool);
        List<Order> orderList = OrderMapper.getAllRequest(connectionPool);
        ctx.attribute("orderList", orderList);
        ctx.render("ForespørgeselOversigtAdmin.html");
    }
}

