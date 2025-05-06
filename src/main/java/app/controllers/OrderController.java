package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
    }

    private static void makeRequest(Context ctx, ConnectionPool connectionPool){
        //som kunde kan jeg ud fra dropdown menuer vælge bestemte mål på carport
        //det som kunden indtaser bliver til session atributter der bliver henteet ind icontrolleren og gemmes på brugeren når der er logget ind
        //gemmes i en ordre
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
