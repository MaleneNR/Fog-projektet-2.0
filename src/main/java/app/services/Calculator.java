package app.services;

import app.entities.Order;
import app.entities.OrderDetail;
import app.entities.Product;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.OrderMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final int POSTS = 1;
    private static final int RAFTERS = 2;               //ID for materiale i db, Hardcoded (må vi gerne:))
    private static final int BEAMS = 2;

    private List<OrderDetail> orderDetails = new ArrayList<>(); //listen skal bestå af entiteten product, når denne er oprettet
    private int width;
    private int length;
    private ConnectionPool connectionPool;

    public Calculator(int width, int length, ConnectionPool connectionPool) {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
    }


    public void calcCarport(Order order) throws DatabaseException {
        calcPosts(order);
        calcBeams(order);
        calcRafters(order);

    }

    // Stolper
    private void calcPosts(Order order) throws DatabaseException {
        int quantity = calcPostQuantity(); //Antallet af stolper beregnes

        List<Product> products = MaterialMapper.getProductsByMaterialId(0,POSTS,connectionPool); //Vi henter produkter, der er over minLength (her 0)
        Product product = products.get(0);                                                                 //Tager den første i listen
        OrderDetail orderDetail = new OrderDetail(order.getOrderId(),product, quantity,"Stolpe nedgraves 90cm i jord");
        orderDetails.add(orderDetail);
    }

    public int calcPostQuantity(){
        int quantity = 2 * ( 2 + (this.length-130) / 340);
        return quantity;
    }

    // Remme
    private void calcBeams(Order order){

    }

    // Spær
    private void calcRafters(Order order){

    }

    public List<OrderDetail> getOrderDetails(){
        return orderDetails;
    }


}
