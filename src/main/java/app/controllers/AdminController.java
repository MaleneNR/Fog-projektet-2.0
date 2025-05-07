package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
    }

    private static void editProduct(Context ctx, ConnectionPool connectionPool){
        //Man skal som admin kunne ændre i priser og beskrivelse og lign.



    }

    private static void viewAllOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        //som admin skal man kunne se alle forespørgelser der ligger fra kunderne, ligesom i cupcake


    }




}
