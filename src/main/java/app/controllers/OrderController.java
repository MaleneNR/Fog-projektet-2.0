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

public class OrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/customMadeSite", ctx -> designYourCarport(ctx));
        app.post("/carportRequest", ctx -> showRequest(ctx));
        app.post("/viewFinalOrder", ctx -> acceptOrder(ctx));

    }

    private static void designYourCarport(@NotNull Context ctx) {
        ctx.sessionAttribute("lengthOptions", Dimensions.options(240,780,30));
        ctx.sessionAttribute("widthOptions", Dimensions.options(240,600,30));
        ctx.sessionAttribute("heightOptions", Dimensions.options(210,300,30));

        ctx.render("customMadeSite.html");
    }

    private static void acceptOrder(@NotNull Context ctx) { //TODO
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
    private static void makeRequest(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        //funktionen skal kunne, så en bruger ud fra egne valg af mål der bliver givet i dropdownmenuerne og derfra kunne gå videre og ligge en forespørgsel
        //det som kunden vælger i menuerne bliver til en ordre og session som bliver tilkoblet på deres user_id når de logger ind

        //funktionen skal tage en ctx og connection pool, så der er adgang til db og så der kan komunikeres med html ind og ud

        //funktionen skal gemme de valgte oplysninger via session

        //den skal i sidste ende retunere en ordre hvorpå alle oplysninger er gemt  evt gemmes i OrderMapper.addRequest()

        //routes til loginEllerOpretBruger.html


          /*
          Basket currentBasket = ctx.sessionAttribute("currentBasket");
        OrderMapper.addOrder(currentBasket, connectionPool);
        User currentUser = ctx.sessionAttribute("currentUser");
        for (Cupcake cupcake : currentBasket.getBasket()) {
            currentUser.setBalance(currentUser.getBalance() - (cupcake.getPrice() * cupcake.getQuantity()));
        }
         */



       /* int length = Integer.parseInt(ctx.formParam("length"));
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));


        Order currentOrder = ctx.sessionAttribute("currentOrder");
        OrderMapper.addRequest(currentOrder, connectionPool);
        User currrentUser = ctx.sessionAttribute("currentUser");

*/
        int length = Integer.parseInt(ctx.formParam("length"));//skal der ændres til carport_length
        int width = Integer.parseInt(ctx.formParam("width"));
        int height = Integer.parseInt(ctx.formParam("height"));

        Order currentOrder = ctx.sessionAttribute("currentOrder");
        User currentUser = ctx.sessionAttribute("currentUser");

//Sæt mål på ordren (så de kommer med ned i databasen)
        currentOrder.setLength(length);
        currentOrder.setWidth(width);
        currentOrder.setHeight(height);

//Sæt brugeren på ordren
        currentOrder.setUser(currentUser);//skal der være noget med order_id

//Gem ordren i databasen
        OrderMapper.addRequest(currentOrder, connectionPool);

//evt. redirect eller vis bekræftelse
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
