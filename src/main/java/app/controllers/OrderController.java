package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
    }

    private static void makeRequest(Context ctx, ConnectionPool connectionPool){

        int length = Integer.parseInt(ctx.formParam("length"));
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));










        //som kunde kan jeg ud fra dropdown menuer vælge bestemte mål på carport
        //det som kunden indtaser bliver til session atributter der bliver henteet ind i controlleren og gemmes på brugeren når der er logget ind
        //gemmes i en ordre

        Order currentOrder = ctx.sessionAttribute("currentOrder");
        OrderMapper.addRequest(currentOrder, connectionPool);
        User currrentUser = ctx.sessionAttribute("currentUser");

        /*
          Basket currentBasket = ctx.sessionAttribute("currentBasket");
        OrderMapper.addOrder(currentBasket, connectionPool);
        User currentUser = ctx.sessionAttribute("currentUser");
        for (Cupcake cupcake : currentBasket.getBasket()) {
            currentUser.setBalance(currentUser.getBalance() - (cupcake.getPrice() * cupcake.getQuantity()));
        }
         */

    }
}
