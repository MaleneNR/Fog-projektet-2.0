package app.services;

import app.entities.Order;
import app.entities.OrderDetail;
import app.entities.Product;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final int POSTS = 1;
    private static final int RAFTERS = 2;               //ID for materiale i db, Hardcoded (må vi gerne:))
    private static final int BEAMS = 2;
    private static final int TILES = 3;

    private List<OrderDetail> orderDetails = new ArrayList<>(); //listen skal bestå af entiteten product, når denne er oprettet
    private int width;
    private int length;
    private ConnectionPool connectionPool;

    public Calculator(int width, int length, ConnectionPool connectionPool) {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
    }

    public Calculator(int width, int length) {
        this.width = width;
        this.length = length;
    }

    public void calcCarport(Order order) throws DatabaseException {
        calcPosts(order);
        calcBeams(order);
        calcRafters(order);

        if(order.wantRoof()){calcRoofPanels(order);}

    }

    /***** STOLPER *****/
    private void calcPosts(Order order) throws DatabaseException {
        int quantity = calcPostQuantity(); //Antallet af stolper beregnes

        List<Product> products = MaterialMapper.getProductsByMaterialId(POSTS,connectionPool); //TODO Der burde kun være 300 cm stolpe
        Product bestMatchingProduct = findBestMatchingProduct(products,order.getHeight());


        OrderDetail orderDetail = new OrderDetail(bestMatchingProduct,quantity,"Stolpe nedgraves 90cm i jord",bestMatchingProduct.getMaterial().getMaterialId(), order.getOrderId());

        orderDetails.add(orderDetail);

    }

    public int calcPostQuantity(){
        int quantity = 2 * (2 + (this.length-130) / 340);
        return quantity;
    }



    /***** REMME *****/
    private void calcBeams(Order order) throws DatabaseException {
        List<Product> products = MaterialMapper.getProductsByMaterialId(BEAMS,connectionPool);
        int quantity = 2; //Always 2, one for each side

        if(this.length <= 600) {  //Finder bedst matchende rem, hvis længden er under 600cm
            Product bestMatchingProduct = findBestMatchingProduct(products, order.getLength());


            OrderDetail orderDetail = new OrderDetail(bestMatchingProduct,quantity,"Remme i sider, sadles ned i stoplerne",bestMatchingProduct.getMaterial().getMaterialId(), order.getOrderId());
            orderDetails.add(orderDetail);


        }else {
            /* Da der ikke er noget spærtræ, der er længdere end 600 cm, så skal der regnes af to omgange for at få en solid rem:
            * 130 udgør den første meter, der er uden stolpe i fronten,
            * samt de 30 cm, som er efter sidste stolpe
            * De trækkes fra total-længden, så vi kun har længden på carporten indenfor de beregnede antal stolper
            * Deles i to, så vi går ud fra at midter-stolpen stilles i midten af front og bag-stolpen.
            */

            int frontBeamLength = ((order.getLength()-130)/2)+100;
            Product frontBeam = findBestMatchingProduct(products, frontBeamLength);

            OrderDetail front = new OrderDetail(frontBeam, quantity,"Forreste remme i sider, sadles ned i stoplerne",frontBeam.getMaterial().getMaterialId(),order.getOrderId());

            orderDetails.add(front);

            int backBeamLength = (((order.getLength()-130)/2)+30);
            Product backBeam  = findBestMatchingProduct(products, backBeamLength);

            OrderDetail back = new OrderDetail(backBeam, quantity,"Bagerste remme i sider, sadles ned i stoplerne",backBeam.getMaterial().getMaterialId(),order.getOrderId());

            orderDetails.add(back);
        }


        /***** Opbygning af funktion: *****/
        /*
        * Hent alle længder remme
        * Hvis længde på carport er større end største rem-længde, så skal vi lave beregning ift. hvad der er smartest
        * add Orderdetail
        */
    }

    /***** SPÆR *****/
    private void calcRafters(Order order) throws DatabaseException {
        int quantity = calcRaftersQuantity(); //Antallet af stolper beregnes

        List<Product> products = MaterialMapper.getProductsByMaterialId(RAFTERS,connectionPool);

        /***** Vi finder det bedst matchende produkt, hvor længden er lang nok, men kortest mulig til spæret *****/
        Product bestMatchingProduct = findBestMatchingProduct(products,this.width);


        OrderDetail orderDetail = new OrderDetail(bestMatchingProduct, quantity,"Spær, monteres på rem",bestMatchingProduct.getMaterial().getMaterialId(),order.getOrderId());

        orderDetails.add(orderDetail);
    }

    public int calcRaftersQuantity() {
        /*Et spær er 4,5cm tykke - her er der rundet op til 5 cm derfor 55+5,
        da der er 55 cm mellemrum mellem hvert spær. Og et spær for enden derfor +1
         */
        return this.length/(55+5) + 1;

    }

    /***** TAG/Trapez-plader *****/
    private void calcRoofPanels(Order order) throws DatabaseException{
        List<Product> products = MaterialMapper.getProductsByMaterialId(TILES,connectionPool);
        int quantity = calcTilesQuantity();
        String assemblyDescription = "Tagplader monteres på spær";
        Product bestMatchingProduct;
        int productMaxWidth = 600;  //TODO Kan dette gøres mindre hardcoded?

        if(order.getLength() <= productMaxWidth){
        bestMatchingProduct = findBestMatchingProduct(products, order.getLength());

        orderDetails.add(new OrderDetail(bestMatchingProduct,quantity, assemblyDescription,bestMatchingProduct.getMaterial().getMaterialId(),order.getOrderId()));}
        else{
            Product firstRow = findBestMatchingProduct(products, productMaxWidth);
            orderDetails.add(new OrderDetail(firstRow,quantity,assemblyDescription, firstRow.getMaterial().getMaterialId(), order.getOrderId()));

            int overlap = 30;
            Product secondRow = findBestMatchingProduct(products, this.length-productMaxWidth-overlap);
            orderDetails.add(new OrderDetail(secondRow,quantity,assemblyDescription, secondRow.getMaterial().getMaterialId(), order.getOrderId()));
        }


    }

    public int calcTilesQuantity() throws DatabaseException {
        //Det anbefales at en trapezplade overlægges med 2 bølger ved fortsættelse, dvs. overlap = 12 (cm)
        int materialWidth = MaterialMapper.getMaterialById(TILES,connectionPool).getWidth();
        int overlap = 12;
        return  (int)Math.ceil(this.length/(materialWidth-overlap));
    }


    public Product findBestMatchingProduct(List<Product> products, int minLength){
        int smallestDifference = Integer.MAX_VALUE;
        Product bestMatchingProduct = null;

        for (Product p : products) {
            if (p.getLength() >= minLength) {
                int difference = p.getLength() - minLength;
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    bestMatchingProduct = p;
                }
            }
        }

        if(bestMatchingProduct != null){
        return bestMatchingProduct;}
        else{
            throw new RuntimeException();
        }
    }

    public List<OrderDetail> getOrderDetails(){
        return orderDetails;
    }


}
