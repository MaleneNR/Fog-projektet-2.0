package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
    }

    private static void editProduct(Context ctx, ConnectionPool connectionPool){
        //Man skal som admin kunne ændre i priser og beskrivelse og lign.

    }

    private static void viewAllOrders(Context ctx, ConnectionPool connectionPool){
        //som admin skal man kunne se alle forespørgelser der ligger fra kunderne, ligesom i cupcake

    }

}
