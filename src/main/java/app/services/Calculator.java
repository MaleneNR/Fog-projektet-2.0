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

    /***** STOLPER *****/
    private void calcPosts(Order order) throws DatabaseException {
        int quantity = calcPostQuantity(); //Antallet af stolper beregnes

        List<Product> products = MaterialMapper.getProductsByMaterialId(POSTS,connectionPool); //Vi henter produkter, der er over minLength (her 0)
        Product bestMatchingProduct = findBestMatchingProduct(products,order.getHeight());                                                                //Tager den første i listen
        OrderDetail orderDetail = new OrderDetail(order.getOrderId(),bestMatchingProduct, quantity,"Stolpe nedgraves 90cm i jord");
        orderDetails.add(orderDetail);
    }

    public int calcPostQuantity(){
        int quantity = 2 * ( 2 + (this.length-130) / 340);
        return quantity;
    }



    /***** REMME *****/
    private void calcBeams(Order order) throws DatabaseException {
        List<Product> products = MaterialMapper.getProductsByMaterialId(RAFTERS,connectionPool);

        if(this.length <= 600) {  //Finder bedst matchende rem, hvis længden er under 600cm
            Product bestMatchingProduct = findBestMatchingProduct(products,this.length);
            int quantity = 2; //Vi skal bruge en til hver side af carporten, derfor 2

            OrderDetail orderDetail = new OrderDetail(order.getOrderId(),bestMatchingProduct, quantity,"Remme i sider, sadles ned i stoplerne");
            orderDetails.add(orderDetail);


        }else {
            //Beregning
        }



        /*
        * Hent alle længder remme
        * Hvis længde på carport er større end største rem-længde, så skal vi lave beregning ift. hvad der er smartest
        * add Orderdetail
        * */


    }

    /***** SPÆR *****/
    private void calcRafters(Order order) throws DatabaseException {
        int quantity = calcRaftersQuantity(); //Antallet af stolper beregnes

        List<Product> products = MaterialMapper.getProductsByMaterialId(RAFTERS,connectionPool);

        /***** Vi finder det bedst matchende produkt, hvor længden er lang nok, men kortest mulig til spæret *****/
        Product bestMatchingProduct = findBestMatchingProduct(products,this.width);

        OrderDetail orderDetail = new OrderDetail(order.getOrderId(),bestMatchingProduct, quantity,"Spær, monteres på rem");
        orderDetails.add(orderDetail);
    }

    public int calcRaftersQuantity() {
        /*Et spær er 4,5cm tykke - her er der rundet op til 5 cm derfor 55+5,
        da der er 55 cm mellemrum mellem hvert spær. Og et spær for enden derfor +1
         */
        return this.length/(55+5) + 1;

    }

    private Product findBestMatchingProduct(List<Product> products, int minLength){
        int smallestDifference = Integer.MAX_VALUE;
        Product bestMatchingProduct = null;

        for (Product p : products) {
            if (p.getLength() >= minLength) {
                int difference = length - this.width;
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    bestMatchingProduct = p;
                }
            }
        }
        return bestMatchingProduct;
    }

    public List<OrderDetail> getOrderDetails(){
        return orderDetails;
    }


}
