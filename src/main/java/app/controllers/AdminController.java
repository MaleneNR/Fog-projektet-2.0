package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.services.Parse;
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

        //app.post("/sendTilbud", ctx -> OrderMapper.updateOrder(ctx, connectionPool));
        app.post("/sendTilbud", ctx -> { sendOffer(ctx, connectionPool);});
    }


    public static void sendOffer(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
            String message = null;
            message = validateNewPrice(ctx);
            if(message != null) {
            ctx.attribute("errorMsg", message);
            ctx.render("adminStatusSite.html");
            } else{
                boolean success = OrderMapper.updateOrder(ctx, connectionPool);//her sendes tilbudet til kunden
                if (success) {
                    List<Order> orderList = OrderMapper.getAllRequests(connectionPool); //henter de opdaterede ordre fra databasen.
                    ctx.sessionAttribute("orderList", orderList); //opdaterer ordrelisten så den nye status kan ses.
                    ctx.render("adminIndex.html");
                } else {
                    ctx.attribute("message", "Ordre kunne ikke opdateres");
                    ctx.render("error.html");
                }
            }
    }

    public static void editProduct(Context ctx, ConnectionPool connectionPool) {

        try {
            //Hent ordre ID og ny pris fra form
            int orderId = Integer.parseInt(ctx.formParam("orderid"));

            //Hent ordren for at sikre den findes
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            User user = order.getUser();

            if (order == null) {
                ctx.status(404).result("Ordre ikke fundet.");
                return;
            }

            //Brug den nuværende status fra ordren (så den ikke ændres her!)
            String currentStatus = order.getOrderStatus();

            //double suggestedPrice = order.getOrderPrice()*0.9;
            //double discount = order.getOrderPrice()*0.1;
            //ctx.sessionAttribute("suggestedPrice", suggestedPrice);
            //ctx.sessionAttribute("discount", discount);
            boolean updated = true;

            if (updated) {
                //Hent opdateret ordre igen for visning
                Order updatedOrder = OrderMapper.getOrderById(orderId, connectionPool);
                double suggestedPrice = updatedOrder.getOrderPrice() * 0.9;
                double discount = updatedOrder.getOrderPrice() * 0.1;
                ctx.sessionAttribute("suggestedPrice", suggestedPrice);
                ctx.sessionAttribute("discount", discount);
                ctx.sessionAttribute("order", updatedOrder);
                ctx.sessionAttribute("user", user);
                ctx.render("adminStatusSite.html"); //Vis opdateret ordre
            } else {
                ctx.status(500).result("Opdatering fejlede.");
            }

        } catch (DatabaseException e) {
            ctx.status(500).result("Fejl: " + e.getMessage());
        }
    }


    private static void viewAllOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        //denne funktions skal admin kunne se ud fra alle forespørgelser der er blevet lagt i db af kunderne

        //funktionen skal tage en ctx og connection pool, så der er adgang til db og så der kan komunikeres med html ind og ud

        //funktionen ska ltil sidst retunere alle ordre der er tilgængelige i db

        //Funktionen henter alt inde fra db via. OrderMapper.getAllRequest()

        //TODO kræver at der ligger ordre fra kunde (hardcode en odre)

        //funktionen ska ltil sidst retunere alle ordre der er tilgængelige i db måske vi en ordre liste

        //derfra skal der routes til statusside.html


        // 1. Hent alle ordre fra databasen (via OrderMapper)
        List<Order> orderList = OrderMapper.getAllRequests(connectionPool);

        // 2. Læg listen af ordrer som attribut (så Thymeleaf kan bruge dem)
        ctx.attribute("orders", orderList);

        // 3. Vis admin status siden (Thymeleaf HTML skabelon)
        ctx.render("adminIndex.html");


    }

    public static String validateNewPrice(Context ctx) {
        String newPrice = ctx.formParam("newPrice");
        String orderPriceStr = ctx.formParam("orderPrice");
        int orderPrice = Integer.parseInt(orderPriceStr);
        String message = null;
        if (newPrice == null || newPrice.isBlank()) {
            message = "Du skal indtaste en pris. Prøv igen";
        }
        message = validateInput(newPrice); //tjekker at pris er med punktum, ikke indeholder bogstaver eller mellemrum

        Integer updatedPrice = Parse.tryParseInt(newPrice);
        if (updatedPrice == null) {
            return "Pris skal være et gyldigt tal. Prøv igen.";
        }
            if (updatedPrice < 0) {
                message = "Pris må ikke være negativ. Prøv igen";
            }

        if(validateProcentCalc(updatedPrice, orderPrice) == false){
            message = "Du må max give et tilbud med 25% i afslag på estimeret pris og max 10% over den estimerede pris. Prøv igen";
        }
        return message;
    }



    public static String validateInput(String newPrice) {
        String message = null;
        if (newPrice.contains(" ")) {
            message = "Pris kan ikke indeholde mellemrum. Prøv igen.";

        }
        if (newPrice.contains(",")) {
            message = "Pris kan ikke indeholde komma (,). Prøv igen.";
        }

        try {
            Integer.parseInt(newPrice);
        } catch (NumberFormatException e) {
            return "Pris kan ikke indeholde bogstaver eller være ugyldig. Prøv igen.";
        }
        
        return message;
    }

    public static boolean validateProcentCalc (int updatedPrice, int orderPrice) {

        double min = orderPrice * 0.75;
        double max = orderPrice * 1.10;

        boolean validation = updatedPrice >= min && updatedPrice <= max;
        return validation;
    }
}
