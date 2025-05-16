package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.IllegalInputException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.Dimensions;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/customMadeSite", ctx -> designYourCarport(ctx));
        app.post("/carportRequest", ctx -> showRequest(ctx));
        app.post("/rejectOrder", ctx -> rejectOrder(ctx, connectionPool));
        app.post("/viewFinalOrder", ctx -> acceptOrder(ctx,connectionPool));

    }


    private static void designYourCarport(@NotNull Context ctx) {
        ctx.sessionAttribute("lengthOptions", Dimensions.options(240,780,30));
        ctx.sessionAttribute("widthOptions", Dimensions.options(240,600,30));
        ctx.sessionAttribute("heightOptions", Dimensions.options(210,300,30));

        ctx.render("customMadeSite.html");
    }

    private static void rejectOrder(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        //Opdaterer status til "afvist" i db
        OrderMapper.updateStatus("Afvist", tryParseInt(ctx.formParam("orderid")),connectionPool);

        //Henter brugeren orders på ny og renderer siden igen
        User user = ctx.sessionAttribute("user");
        List<Order> orders = OrderMapper.getAllRequestsByUserId(user.getUserId(), connectionPool);
        ctx.attribute("orders", orders);
        ctx.render("/customerRequest.html");
    }

    private static void acceptOrder(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException { //TODO
        int orderId = tryParseInt(ctx.formParam("orderid"));
        Boolean updated = OrderMapper.updatePayed(true,orderId, connectionPool);

        if(updated){
        OrderMapper.updateStatus("Betalt", orderId,connectionPool);
        User user = ctx.sessionAttribute("user");
        List<Order> orders = OrderMapper.getAllRequestsByUserId(user.getUserId(), connectionPool);
        ctx.attribute("orders", orders);
        ctx.render("/customerRequest.html");}
        else {
            ctx.render("/error.html");
        }

    }


    private static void showRequest(Context ctx){
        try {
            //Henter alle parametere ind og parse dem ind i rette datatype
            Boolean shed = tryParseBoolean(ctx.formParam("shed"));
            Boolean roof = tryParseBoolean(ctx.formParam("plastic-roof"));
            Integer length = tryParseInt(ctx.formParam("length")); //Integer, da Integer objektet godt kan være null, det kan en primitiv int ikke.
            Integer height = tryParseInt(ctx.formParam("height"));
            Integer width = tryParseInt(ctx.formParam("width"));
            Boolean craftsmen = tryParseBoolean(ctx.formParam("craftsmen"));

            if(shed == null || roof == null || length == null || width == null || height == null || craftsmen == null){
                ctx.attribute("error", "Du manglede en eller flere valg i forbindelse med dit design af ny carport, prøv igen");
                ctx.render("/customMadeSite");
                return; //return, så resten af funktionen ikke bliver eksekveret
            }


            //Sætter dem til sessionAttributter, så vi kan putte dem i db, når bruger har logget ind
            ctx.sessionAttribute("shed", shed);
            ctx.sessionAttribute("roof", roof);
            ctx.sessionAttribute("length", length);
            ctx.sessionAttribute("width", width);
            ctx.sessionAttribute("height", height);
            ctx.sessionAttribute("craftsmen", craftsmen);

            //Viser deres forespørgsel, når de er logget ind
            ctx.render("viewRequest.html");
        }catch (IllegalInputException e){
            designYourCarport(ctx);
            ctx.status(400).result(e.getMessage());
            }

    }

    public static Integer tryParseInt(String value) {  //Overvej at put denne i en anden klasse, hvis vi bruger den i mere end den her
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static Boolean tryParseBoolean(String value) {
        if (value == null || value.startsWith("Med/uden")){
            return null;
        }
        return value.equalsIgnoreCase("ja") || value.startsWith("Med"); //Hvis ja eller med, så returneres true, ellers false
    }

}
