package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.services.DimensionSvg;
import app.services.Parse;
import app.services.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/sendTilbud", ctx -> {sendOffer(ctx, connectionPool);});
        app.post("/seForesporgsel",ctx ->{editProduct(ctx,connectionPool);});
        app.post("/deleteOrder", ctx -> {deleteOrder(ctx, connectionPool);});
    }

    private static void deleteOrder(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int orderId = Parse.tryParseInt(ctx.formParam("orderId"));
        boolean isDelected = OrderMapper.deleteOrderDetailsAndOrder(orderId,connectionPool);
        if(isDelected != true){
            ctx.render("error.html");
        } else {
            ctx.sessionAttribute("orderList", OrderMapper.getAllRequests(connectionPool));
            ctx.render("adminIndex.html");
        }

    }

    private static void sendOffer(Context ctx, ConnectionPool connectionPool) throws DatabaseException {

        String message = validateNewPrice(ctx); //Returnerer en uddybdende fejlbesked, hvis der er noget galt med prisen
        if(message != null) {
            ctx.attribute("errorMsg", message);
            ctx.render("adminStatusSite.html");
        }
        else {//Ellers er prisen valid, og ordren opdateres nu db med status "Tilbud sendt"
            int newPrice = Integer.parseInt(ctx.formParam("newPrice"));
            Order order = ctx.sessionAttribute("order");
            boolean success = OrderMapper.updateOrder(order, newPrice, connectionPool);  //her sendes tilbudet til kunden
            if (success) {
                List<Order> orderList = OrderMapper.getAllRequests(connectionPool); //henter de opdaterede ordre fra databasen.
                ctx.sessionAttribute("orderList", orderList); //opdaterer ordrelisten så den nye status kan ses.
                ctx.render("adminIndex.html");
            } else {
                ctx.attribute("error", "Ordre kunne ikke opdateres");
                ctx.render("error.html");
            }
        }
    }


    private static void editProduct(Context ctx, ConnectionPool connectionPool) {

        try {
            //Hent ordre ID og ny pris fra form
            int orderId = Integer.parseInt(ctx.formParam("orderid"));

            //Hent ordren for at sikre den findes
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            User user = order.getUser();

            if (order == null) {
                ctx.status(404).result("Ordre ikke fundet.");
                ctx.render("error.html");
                return;
            }

            //Brug den nuværende status fra ordren (så den ikke ændres her!)
            String currentStatus = order.getOrderStatus();


            boolean updated = true;


            if (updated) {
                //Hent opdateret ordre igen for visning
                Order updatedOrder = OrderMapper.getOrderById(orderId, connectionPool);
                int suggestedPrice = (int)(updatedOrder.getOrderPrice()*0.9);
                int discount = (int)(updatedOrder.getOrderPrice()*0.1);
                int min = (int)(updatedOrder.getOrderPrice()*0.75);
                int max = (int)(updatedOrder.getOrderPrice()*1.10);
                ctx.sessionAttribute("min",min);
                ctx.sessionAttribute("max",max);
                    ctx.sessionAttribute("suggestedPrice", suggestedPrice);
                    ctx.sessionAttribute("discount", discount);
                    ctx.sessionAttribute("order", updatedOrder);
                    ctx.sessionAttribute("user", user);
              
                    DimensionSvg svg = new DimensionSvg(order.getWidth(), order.getLength());
                    ctx.sessionAttribute("svg", svg.toString());

                ctx.render("adminStatusSite.html"); //Vis opdateret ordre
            } else {
                ctx.status(500).result("Opdatering fejlede.");
            }

        } catch (DatabaseException e) {
            ctx.status(500).result("Fejl: " + e.getMessage());
            ctx.render("error.html");
        }
    }










    public static String validateNewPrice(Context ctx) {
        String newPrice = ctx.formParam("newPrice");
        String orderPriceStr = ctx.formParam("orderPrice");
        int orderPrice = Integer.parseInt(orderPriceStr);
        String message = null;
        if (newPrice == null || newPrice.isBlank()) {
            message = "Du skal indtaste en pris. Prøv igen";
        }
        String inputValidation = validateInput(newPrice);
        if (inputValidation != null) {
            return inputValidation;
        } //tjekker at pris ikke indeholder bogstaver, komma/punktum eller mellemrum

        Integer updatedPrice = Parse.tryParseInt(newPrice);
        if (updatedPrice == null) {
            return "Pris skal være et gyldigt tal. Prøv igen.";
        }
        if (updatedPrice < 0) {
            return "Pris må ikke være negativ. Prøv igen";
        }

        if(validateProcentCalc(updatedPrice, orderPrice) == false){
            return "Du må max give et tilbud med 25% i afslag på estimeret pris og max 10% over den estimerede pris. Prøv igen";
        }
        return message;
    }



    public static String validateInput(String newPrice) {
        if (newPrice.contains(" ")) {
            return "Pris kan ikke indeholde mellemrum. Prøv igen.";

        }
        if (newPrice.contains(",")) {
            return "Pris kan ikke indeholde komma (,). Prøv igen.";
        }

        if (newPrice.contains(".")) {
            return "Pris kan ikke indeholde punktum (.). Prøv igen.";
        }

        try {
            Integer.parseInt(newPrice);
        } catch (NumberFormatException e) {
            return "Pris kan ikke indeholde bogstaver (eks. 'kr'). Prøv igen.";
        }

        return null;
    }

    public static boolean validateProcentCalc (int updatedPrice, int orderPrice) {

        double min = orderPrice * 0.75;
        double max = orderPrice * 1.10;

        boolean validation = updatedPrice >= min && updatedPrice <= max;
        return validation;
    }
}