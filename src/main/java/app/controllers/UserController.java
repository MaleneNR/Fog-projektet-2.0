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
        app.post("/Login", ctx -> login(ctx, connectionPool));
        //app.get("/loginPage", ctx -> ctx.render("login.html"));
        app.get("/logout", ctx -> logout(ctx));
        app.post("/loginOpretBruger", ctx -> ctx.render("createUserOrLogin.html")); //TODO:
        app.get("OpretBruger", ctx -> ctx.render("createUser.html"));
        app.get("Login", ctx -> ctx.render("login.html"));
        app.post("/createUser", ctx -> createUser(ctx, connectionPool));
        app.get("/Customsite", ctx -> {
            ctx.attribute("lengthOptions", Dimensions.options(240,780,30));
            ctx.attribute("widthOptions", Dimensions.options(240,600,30));
            ctx.attribute("heightOptions", Dimensions.options(210,300,30));

            ctx.render("customMadeSite.html");
        });
        //app.post("/seForesporgsel", ctx -> ctx.render("adminStatusSite.html"));
        app.post("/seForesporgsel", ctx -> AdminController.editProduct(ctx, connectionPool));


    }
//TODO brug lidt alla det samme her til custommadesite i from af at genne valgte mål i formparametre  fx cupcake  <select id="topping" name="topping">
//                    <option>Vælg topping</option>
//                    <option th:each="topping: ${toppingsList}"
//                            th:value="${topping.toppingId}"
//                            th:text="${topping.topping}">
//                    </option>
    private static void createUser(@NotNull Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("email"); //til thomsd, hvorfor hedder denne username?
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String navn = ctx.formParam("navn");
        String phoneNumber = ctx.formParam("telefon");//hvorfor ikke int
        String text = ctx.formParam("text");


        //TODO: 1. gem inputs fra de andre keys i createUser.html som fx "telefon".



        //Validerer password
        if(password1.equals(password2)){
            try{
                UserMapper.createUser(username,password1, phoneNumber, navn, text, connectionPool); //TODO 2. funktionen skal opdateres så den modtager de nye parametre fx telefon.
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: "+ username+ ". Du skal nu logge på");
                ctx.render("login.html");}
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
            //TODO.5 User klassen skal opdateres, så den kan indeholde de nye parametre. Husk konstruktoren!
            //TODO.6 UserMapper.login() skal opdateres til at tage de nye parametre.
            User user = UserMapper.login(username, password, connectionPool);//skal denne også opdaterers med nye parametre?
            //Når user bliver oprettet, bliver der også oprettet en basket.
            if(user.getRole() == 3){
                loginAdmin(ctx,connectionPool);
            } else{

                ctx.render("adminIndex.html");
            }

        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("createUserOrLogin.html");
        }

    }




    private static void loginAdmin(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        //List<Order> orderList = OrderMapper.getAllRequest(connectionPool);
        List<Order> orderList = OrderMapper.getAllRequests(connectionPool);//skal man kalde viewAllOrders fra dmin controller?
        ctx.sessionAttribute("orderList", orderList);
        ctx.render("adminIndex.html");


    }
}

