package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.services.Calculator;
import io.javalin.http.Context;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class OrderMapper {


   public static List<Order> getAllRequests(ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orders = new ArrayList<>();
        String sql = "select * from orders";

        try (
                Connection connection = connectionPool.getConnection();
                Statement s = connection.createStatement();
        )
        {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next())
            {
                int orderId = rs.getInt("order_id");
                String status = rs.getString("order_status");
                int price = rs.getInt("order_price");
                boolean payed = rs.getBoolean("payed");
                LocalDate date = rs.getDate("date").toLocalDate();
                User user = UserMapper.getUserById(rs.getInt("user_id"), connectionPool);
                int l = rs.getInt("carport_length");
                int h = rs.getInt("height");
                int w = rs.getInt("carport_width");
                boolean shed = rs.getBoolean("shed");
                boolean roof = rs.getBoolean("tiles");

                orders.add(new Order(orderId,status,price,payed,date,user,l,h,w,shed,roof));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer, getAllRequests()", e.getMessage());
        }
       Map<String, Integer> statusPriority = new HashMap<>();
       statusPriority.put("Modtaget", 0);
       statusPriority.put("Tilbud sendt", 1);
       statusPriority.put("Betalt", 2);
       statusPriority.put("Afvist", 3);

       //Sorterer dem efter rækkefølgen i hashmappet
       orders.sort(Comparator.comparing(order -> statusPriority.get(order.getOrderStatus())));

       return orders;


    }

    public static List<Order> getAllRequestsByUserId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        List<Order> orders = new ArrayList<>();
        String sql = "select * from orders where user_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int orderId = rs.getInt("order_id");
                String status = rs.getString("order_status");
                int price = rs.getInt("order_price");
                boolean payed = rs.getBoolean("payed");
                LocalDate date = rs.getDate("date").toLocalDate();
                User user = UserMapper.getUserById(rs.getInt("user_id"), connectionPool);
                int l = rs.getInt("carport_length");
                int h = rs.getInt("height");
                int w = rs.getInt("carport_width");
                boolean shed = rs.getBoolean("shed");
                boolean roof = rs.getBoolean("tiles");

                orders.add(new Order(orderId,status,price,payed,date,user,l,h,w,shed,roof));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer, getAllRequests()", e.getMessage());
        }
        orders.sort(Comparator.comparing(Order::getOrderId).reversed()); //Sorterer efter ordreId;
        return orders;

    }



    public static List<OrderDetail> getOrderDetailsFromViewById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orderdetails_view WHERE order_id = ?";
        List<OrderDetail> orderDetails = new ArrayList<>();
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        )
        {
            ps.setInt(1,orderId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String title = rs.getString("material");
                int materialId = rs.getInt("material_id");
                String unit = rs.getString("unit");
                String description = rs.getString("description");
                int pricePerUnit = rs.getInt("price_per_unit");
                Material material = new Material(materialId,title,unit,description,pricePerUnit);

                int productId = rs.getInt("product_id");
                int length = rs.getInt("length");
                Product product = new Product(productId,length,material);


                int quantity = rs.getInt("quantity");
                int totalPrice = rs.getInt("total_price");
                String assemblyDescription = rs.getString("assembly_description");
                OrderDetail orderDetail = new OrderDetail(product,quantity,totalPrice,assemblyDescription,materialId,orderId);

                orderDetails.add(orderDetail);
            }

        }catch (SQLException e){
            throw new DatabaseException("Kunne ikke finde enten materiale, product eller orderdetails for ordre: " +orderId, e.getMessage());
        }
        return orderDetails;
    }

    public static boolean addRequest(Order order, ConnectionPool connectionPool) throws DatabaseException {
        int rowsAffected = 0;
        Boolean orderAdded = false;

            String status = "Modtaget";
            LocalDate dateOfToday = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());

        String sql = "INSERT INTO orders (order_status, payed, date, user_id, carport_length, height, carport_width,shed,tiles) values (?,?,?,?,?,?,?,?,?) RETURNING order_id";
        //(order_price sættes senere (nede i addOrderDetails))
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setString(1, status);
            ps.setBoolean(2,false);
            ps.setDate(3, Date.valueOf(dateOfToday)); //Dags dato i (YYYY-MM-DD)-format
            ps.setInt(4, order.getUser().getUserId());
            ps.setInt(5,order.getLength());
            ps.setInt(6,order.getHeight());
            ps.setInt(7,order.getWidth());
            ps.setBoolean(8, order.wantShed());
            ps.setBoolean(9, order.wantRoof());


            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt("order_id");
                        order.setOrderId(orderId);
                        orderAdded = true;

                        //Calculator beregner materialer
                        Calculator calculator = new Calculator(order.getWidth(),order.getLength(),connectionPool);
                        calculator.calcCarport(order); //Kalder alle beregningsmetoder i calculatorklassen, hvor de tilføjer til en liste af orderdetails
                        List<OrderDetail> orderDetails = calculator.getOrderDetails(); //Her får vi så listen
                        if(addOrderDetail(orderDetails,connectionPool) == true){
                            orderAdded = true;
                        }
                    }
                }
            } else {
                throw new DatabaseException("Fejl ved indsætning af en ordre");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Fejl ved indsætning af en ordre", e.getMessage());
        }
        return orderAdded;
    }


    private static boolean addOrderDetail(List<OrderDetail> orderDetails, ConnectionPool connectionPool) {
        int affectedRows = 0;
        Boolean orderDetailsAdded = false;
        int orderPrice = 0;
        int currentOrderId = orderDetails.get(0).getOrderId();

        String sql = "INSERT INTO order_details (product_id, quantity, total_price, assembly_description, material_id, order_id) values (?,?,?,?,?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            for (OrderDetail orderDetail : orderDetails) {

                //Calculation of totalprice (productLengthInMeter * pricePerUnit)
                int pricePerUnit = MaterialMapper.getMaterialById(orderDetail.getMaterialId(),connectionPool).getPricePerUnit();
                int lengthInMeter = orderDetail.getProduct().getLength()/100; //from cm i db
                orderDetail.setTotalPrice(pricePerUnit * lengthInMeter);

                //Update order_details with every detail from the list
                ps.setInt(1, orderDetail.getProduct().getProductId());
                ps.setInt(2, orderDetail.getQuantity());
                ps.setInt(3, orderDetail.getTotalPrice());
                ps.setString(4, orderDetail.getAssemblyDescription());
                ps.setInt(5, orderDetail.getMaterialId());
                ps.setInt(6, orderDetail.getOrderId());
                affectedRows += ps.executeUpdate();

                //Calculation of orderPrice (total_price * quantity)
                orderPrice += orderDetail.getQuantity() * orderDetail.getTotalPrice();
            }
            if (affectedRows == orderDetails.size()){
                orderDetailsAdded = true;
                updatePrice(orderPrice,currentOrderId,connectionPool);
            }

        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException(e);
        }
        return orderDetailsAdded;
    }


    public static boolean deleteOrderDetailsAndOrder (int orderId, ConnectionPool connectionPool) throws DatabaseException {
        boolean deleted = false;
        String orderdetails = "delete from order_details where order_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps1 = connection.prepareStatement(orderdetails)
        )
        {
            ps1.setInt(1, orderId);
            int rowsAffected = -1;
            rowsAffected = ps1.executeUpdate();
            if (rowsAffected > -1){
                String order = "delete from orders where order_id = ?";

                try (
                        PreparedStatement ps2 = connection.prepareStatement(order);
                ){
                    ps2.setInt(1, orderId);
                    int orderRowsAffected = ps2.executeUpdate();
                    if (orderRowsAffected == 1){
                        deleted = true;
                    }
                }
            }else
            {
                throw new DatabaseException("Fejl i sletning af en ordredetajle");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl ved sletning af en ordre", e.getMessage());
        }
        return deleted;
    }

    public  static Order getOrderById (int orderId, ConnectionPool connectionPool) throws DatabaseException{
            Order order = null;
            String sql = "select * from orders where order_id = ?";

            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                {
                    String status = rs.getString("order_status");
                    int price = rs.getInt("order_price");
                    boolean payed = rs.getBoolean("payed");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    User user = UserMapper.getUserById(rs.getInt("user_id"), connectionPool);
                    int l = rs.getInt("carport_length");
                    int h = rs.getInt("height");
                    int w = rs.getInt("carport_width");
                    boolean shed = rs.getBoolean("shed");
                    boolean roof = rs.getBoolean("tiles");

                    order = new Order(orderId,status,price,payed,date,user,l,h,w,shed,roof);
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Fejl i søgning på en ordre ved id" + orderId+ " i getOrderById()", e.getMessage());
            }
            return order;
        }

    public static boolean updateStatus(String status, int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, status);
            ps.setInt(2, orderId);

            int rows = ps.executeUpdate();
            if(rows == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i opdatering af ordre i updateStatus()", e.getMessage());
        }
    }

    public static boolean updatePayed(Boolean newStatusOfPayed, int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET payed = ? WHERE order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setBoolean(1, newStatusOfPayed);
            ps.setInt(2, orderId);

            int rows = ps.executeUpdate();
            if(rows == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i opdatering af ordre i updatePayed()", e.getMessage());
        }
    }

    public static boolean updatePrice(int newPrice, int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET order_price = ? WHERE order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, newPrice);
            ps.setInt(2, orderId);

            int rows = ps.executeUpdate();
            if(rows == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i opdatering af ordre i updatePayed()", e.getMessage());
        }
    }



    public static boolean updateOrder(Order order, int newPrice, ConnectionPool connectionPool) throws DatabaseException {
        int orderId = order.getOrderId();
        String sql = "UPDATE orders SET order_price = ?, order_status = ? WHERE order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, newPrice);
            ps.setString(2, "Tilbud sendt");
            ps.setInt(3, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i opdatering af ordre i updateOrder()", e.getMessage());
        }
    }


}











