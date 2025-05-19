package app.controllers;

import app.entities.Order;
import app.entities.OrderDetail;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MaterialController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/showFinalOrder", ctx -> showFinalOrder(ctx, connectionPool));
    }

    private static void showFinalOrder(@NotNull Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int orderid = Integer.parseInt(ctx.formParam("orderid"));
        Order order = OrderMapper.getOrderById(orderid, connectionPool);
        List<OrderDetail> orderDetails = OrderMapper.getOrderDetailsFromViewById(order.getOrderId(), connectionPool);
        order.setOrderDetails(orderDetails);
        CarportSvg svg = new CarportSvg(order.getLength(), order.getWidth());

        ctx.attribute("svg", svg.toString());
        ctx.attribute("order", order);
        ctx.render("/viewFinalOrder.html");
    }


}
