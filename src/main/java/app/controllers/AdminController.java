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
        //funktionen skal give admin adgang til at kunne redigere i beskriverlser og priser

        //funktionen skal tage en ctx og connection pool, så der er adgang til db og så der kan komunikeres med html ind og ud

        //funktionen skal retunere det færdige redigerede produkt

        //jeg skal have en ordre med adgang igennem db

        // funktionen skal gå ind i db og hente de forespørgsler via viewAllOrders() der nu engang ligger og derfra give adgang til at man kan ændre i tingene

        //man skal som admin kunne vælge en af mange ordre at kigge på

        //TODO for at kunne redigere i tingene skal vi først have en forespørgsel fra en kunde

        //jeg vil måske starte med at have en fobinedelse til db i noget try-catch så skal man via OrderMapper kalde getAllRequest() så de bliver hentet

        //så skal man kunne gå ind via Orders tabellen i Price og ændre prisen og gemme det som ny opdateret pris

        //så skal den retunere den nye opdaterede ordre med opdateret pris

        //evt kunne sende tilbus men tror det hører til mailsystem


            try {
                // Hent ordre ID og ny pris fra form
                int orderId = Integer.parseInt(ctx.formParam("order_id"));
                int newPrice = Integer.parseInt(ctx.formParam("price"));

                // Hent ordren for at sikre den findes
                Order order = OrderMapper.getOrderById(orderId, connectionPool);

                if (order == null) {
                    ctx.status(404).result("Ordre ikke fundet.");
                    return;
                }

                // Brug den nuværende status fra ordren (så den ikke ændres her!)
                String currentStatus = order.getOrderStatus();

                // Opdater KUN prisen via OrderMapper → og behold nuværende status
                boolean updated = OrderMapper.updateOrder(orderId, newPrice, currentStatus, connectionPool);

                if (updated) {
                    // Hent opdateret ordre igen for visning
                    Order updatedOrder = OrderMapper.getOrderById(orderId, connectionPool);
                    ctx.attribute("order", updatedOrder);
                    ctx.render("ForespørgeselOversigtAdmin.html"); // Vis opdateret ordre
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
        List<Order> orderList = OrderMapper.getAllRequest(connectionPool);

        // 2. Læg listen af ordrer som attribut (så Thymeleaf kan bruge dem)
        ctx.attribute("orders", orderList);

        // 3. Vis admin status siden (Thymeleaf HTML skabelon)
        ctx.render("ForespørgeselOversigtAdmin.html");





    }



}
