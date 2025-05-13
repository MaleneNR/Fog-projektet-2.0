package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/carport-request", ctx -> showRequest(ctx));
    }

    private static void showRequest(Context ctx){

        Boolean shed = ctx.formParam("shed").equals("Med skur");
        int length = Integer.parseInt(ctx.formParam("length"));
        int height = Integer.parseInt(ctx.formParam("height"));
        int width = Integer.parseInt(ctx.formParam("width"));
        Boolean craftsmen = ctx.formParam("craftsmen").equals("Ja");

        ctx.sessionAttribute("shed", shed);
        ctx.sessionAttribute("length", length);
        ctx.sessionAttribute("width", width);
        ctx.sessionAttribute("height", height);
        ctx.sessionAttribute("craftsmen", craftsmen);

        ctx.render("viewRequest.html");
    }
    private static void makeRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        //funktionen skal kunne, så en bruger ud fra egne valg af mål der bliver givet i dropdownmenuerne og derfra kunne gå videre og ligge en forespørgsel
        //det som kunden vælger i menuerne bliver til en ordre og session som bliver tilkoblet på deres user_id når de logger ind

        //funktionen skal tage en ctx og connection pool, så der er adgang til db og så der kan komunikeres med html ind og ud

        //funktionen skal gemme de valgte oplysninger via session

        //den skal i sidste ende retunere en ordre hvorpå alle oplysninger er gemt  evt gemmes i OrderMapper.addRequest()

        //routes til loginEllerOpretBruger.html


          /*
          Basket currentBasket = ctx.sessionAttribute("currentBasket");
        OrderMapper.addOrder(currentBasket, connectionPool);
        User currentUser = ctx.sessionAttribute("currentUser");
        for (Cupcake cupcake : currentBasket.getBasket()) {
            currentUser.setBalance(currentUser.getBalance() - (cupcake.getPrice() * cupcake.getQuantity()));
        }
         */



       /* int length = Integer.parseInt(ctx.formParam("length"));
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));


        Order currentOrder = ctx.sessionAttribute("currentOrder");
        OrderMapper.addRequest(currentOrder, connectionPool);
        User currrentUser = ctx.sessionAttribute("currentUser");

*/
        int length = Integer.parseInt(ctx.formParam("length"));//skal der ændres til carport_length
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));

        Order currentOrder = ctx.sessionAttribute("currentOrder");
        User currentUser = ctx.sessionAttribute("currentUser");

//Sæt mål på ordren (så de kommer med ned i databasen)
        currentOrder.setLength(length);
        currentOrder.setWidth(width);
        currentOrder.setHeight(height);

//Sæt brugeren på ordren
        currentOrder.setUser(currentUser);//skal der være noget med order_id

//Gem ordren i databasen
        OrderMapper.addRequest(currentOrder, connectionPool);

//evt. redirect eller vis bekræftelse
        ctx.render("viewRequest.html");



    }
}
