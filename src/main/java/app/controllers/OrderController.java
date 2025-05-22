package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.IllegalInputException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

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
        OrderMapper.updateStatus("Afvist", Parse.tryParseInt(ctx.formParam("orderid")),connectionPool);

        //Henter brugeren orders på ny og renderer siden igen
        User user = ctx.sessionAttribute("user");
        List<Order> orders = OrderMapper.getAllRequestsByUserId(user.getUserId(), connectionPool);
        ctx.attribute("orders", orders);
        ctx.render("/customerRequest.html");
    }

    private static void acceptOrder(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException { //TODO
        int orderId = Parse.tryParseInt(ctx.formParam("orderid"));
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
            Boolean withShed = Parse.tryParseBoolean(ctx.formParam("shed"));
            Boolean withTiles = Parse.tryParseBoolean(ctx.formParam("plastic-roof"));
            Integer length = Parse.tryParseInt(ctx.formParam("length")); //Integer, da Integer objektet godt kan være null, det kan en primitiv int ikke.
            Integer height = Parse.tryParseInt(ctx.formParam("height"));
            Integer width = Parse.tryParseInt(ctx.formParam("width"));
            Boolean craftsmen = Parse.tryParseBoolean(ctx.formParam("craftsmen"));

            if(withShed == null || withTiles == null || length == null || width == null || height == null || craftsmen == null){
                ctx.attribute("error", "Du manglede en eller flere valg i forbindelse med dit design af ny carport, prøv igen");
                ctx.render("/customMadeSite");
                return; //return, så resten af funktionen ikke bliver eksekveret
            }

            //Sætter dem til sessionAttributter, så vi kan putte dem i db, når bruger har logget ind
            /*De sendes stadig tilbage som sessionAttribute, da der ikke kan være en ordre endnu
            - en ordre indeholder nemlig en user, som vi ikke har før de er logget ind*/
            ctx.sessionAttribute("shed", withShed);
            ctx.sessionAttribute("roof", withTiles);
            ctx.sessionAttribute("length", length);
            ctx.sessionAttribute("width", width);
            ctx.sessionAttribute("height", height);
            ctx.sessionAttribute("craftsmen", craftsmen);

            showOrder(ctx);
            ctx.render("viewRequest.html");
        }catch (IllegalInputException e){
            designYourCarport(ctx);
            ctx.status(400).result(e.getMessage());
            }

    }

    public static void showOrder(Context ctx){
        Locale.setDefault(new Locale("US"));

        DimensionSvg svg = new DimensionSvg(ctx.sessionAttribute("width"),ctx.sessionAttribute("length"));


        ctx.attribute("svg", svg.toString());
    }

}
