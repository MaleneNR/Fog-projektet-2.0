package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.exceptions.IllegalInputException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import app.services.Dimensions;
import app.services.Parse;
import app.services.Svg;
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
            Boolean shed = Parse.tryParseBoolean(ctx.formParam("shed"));
            Boolean roof = Parse.tryParseBoolean(ctx.formParam("plastic-roof"));
            Integer length = Parse.tryParseInt(ctx.formParam("length")); //Integer, da Integer objektet godt kan være null, det kan en primitiv int ikke.
            Integer height = Parse.tryParseInt(ctx.formParam("height"));
            Integer width = Parse.tryParseInt(ctx.formParam("width"));
            Boolean craftsmen = Parse.tryParseBoolean(ctx.formParam("craftsmen"));

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

            showOrder(ctx);
            //Viser deres forespørgsel, når de er logget ind
            //ctx.render("viewRequest.html");
        }catch (IllegalInputException e){
            designYourCarport(ctx);
            ctx.status(400).result(e.getMessage());
            }

    }

    public static void showOrder(Context ctx){
        Locale.setDefault(new Locale("US"));
        String rectStyle ="stroke:black;fill: white";

        CarportSvg svg = new CarportSvg(780, 600);

//        //Ramme
//        carportSvg.addRectangle(0,0 ,600, 780,rectStyle );
//
//        //Spær
//        carportSvg.addRectangle(0,0,600,5,rectStyle);
//        carportSvg.addRectangle(775,0,600,5,rectStyle);
//
//        //Remme
//        carportSvg.addRectangle(0,35,5,780, rectStyle);
//        carportSvg.addRectangle(0,560,5,780, rectStyle);
//
//        //Stiplede linjer
//        carportSvg.addLine(55,40,550,565, "stroke:black;stroke-dasharray:10,5");
//        carportSvg.addLine(55,565,550,40, "stroke:black;stroke-dasharray:10,5");
//
//        //Stolper oppe
//        carportSvg.addRectangle(100,35,10,10, rectStyle);
//        carportSvg.addRectangle(425,35,10,10, rectStyle);
//        carportSvg.addRectangle(750,35,10,10, rectStyle);
//
//        //Stolper nede
//        carportSvg.addRectangle(100,555,10,10, rectStyle);
//        carportSvg.addRectangle(425,555,10,10, rectStyle);
//        carportSvg.addRectangle(750,555,10,10, rectStyle);

        ctx.attribute("svg", svg.toString());
        ctx.render("viewRequest.html");
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
