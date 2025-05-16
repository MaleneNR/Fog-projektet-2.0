package app.persistence;

import app.entities.Order;
import app.entities.OrderDetail;
import app.entities.Product;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.services.Calculator;
import io.javalin.http.Context;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

                orders.add(new Order(orderId,status,price,payed,date,user,l,h,w,shed));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer, getAllRequests()", e.getMessage());
        }
        return orders;

        //Admin skla kunne se alle forespørgelser så alle orders bliver hentet ud fra db via orderMapper
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

                orders.add(new Order(orderId,status,price,payed,date,user,l,h,w,shed));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer, getAllRequests()", e.getMessage());
        }
        return orders;

        //Admin skla kunne se alle forespørgelser så alle orders bliver hentet ud fra db via orderMapper
    }

    public static List<OrderDetail> getAllOrderDetails (int orderId, ConnectionPool connectionPool) throws DatabaseException {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        )
        {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                int totalPrice = rs.getInt("total_price");
                String assemblyDescription = rs.getString("assembly_description");

                Product product = MaterialMapper.getProductById(productId,connectionPool);
                OrderDetail orderDetail = new OrderDetail(orderId, product,quantity,assemblyDescription,totalPrice);
                orderDetails.add(orderDetail);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer, getAllRequests()", e.getMessage());
        }



        //Skal hente detaljerne til givne ordre (Stk liste)
    return orderDetails;  //TODO Skal returnerer en order_detail
    }

    public static boolean addRequest(Order order, ConnectionPool connectionPool) throws DatabaseException {
        int rowsAffected = 0;
        Boolean orderAdded = false;

            String status = "Received";  //TODO Skal dette hardcodes
            LocalDate dateOfToday = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());

        String sql = "INSERT INTO orders (order_status, order_price, payed, date, user_id, carport_length, height, carport_width,shed) values (?,?,?,?,?,?,?,?,?) RETURNING order_id";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setString(1, status);
            ps.setInt(2,20000); //TODO Estimeret pris, IKKE denne hardcodede pris!
            ps.setBoolean(3,false);
            ps.setDate(4, Date.valueOf(dateOfToday)); //Dags dato i (YYYY-MM-DD)-format
            ps.setInt(5, order.getUser().getUserId());
            ps.setInt(6,order.getLength());
            ps.setInt(7,order.getHeight());
            ps.setInt(8,order.getWidth());
            ps.setBoolean(9, order.wantShed());

            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt("order_id");
                        order.setOrderId(orderId);
                        orderAdded = true;

                        //Calculator beregner materialler
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

        String sql = "INSERT INTO order_details (product_id, quantity, total_price, assembly_description, material_id, order_id) values (?,?,?,?,?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            for (OrderDetail orderDetail : orderDetails) {

                //Calculation of totalprice (quantity * pricePerUnit)
                int pricePerUnit = MaterialMapper.getMaterialById(orderDetail.getMaterialId(),connectionPool).getPricePerUnit();
                int lengthInMeter = orderDetail.getProduct().getLength()/100; //from cm i db
                int totalPrice = pricePerUnit * lengthInMeter;

                ps.setInt(1, orderDetail.getProduct().getProductId());
                ps.setInt(2, orderDetail.getQuantity());
                ps.setInt(3, totalPrice); //TODO TOTALPRICE KAN OPTIMERES
                ps.setString(4, orderDetail.getAssemblyDescription());
                ps.setInt(5, orderDetail.getMaterialId());
                ps.setInt(6, orderDetail.getOrderId());
                affectedRows = ps.executeUpdate();
            }
            if (affectedRows == orderDetails.size()){
                orderDetailsAdded = true;
            }

        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException(e);
        }
        return orderDetailsAdded;
    }


    public static boolean deleteOrderDetailsAndOrder (int orderId, ConnectionPool connectionPool){
        //Admin kan slette ordre fra db
return false;
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

                    order = new Order(orderId,status,price,payed,date,user,l,h,w,shed);
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




        //TODO evt lave en update funktion så man kan opdaterer ordre som admin

    public static boolean updateOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int newPrice = Integer.parseInt(ctx.formParam("newPrice")); //ala det her.
        Order order = ctx.sessionAttribute("order");
        int orderId = order.getOrderId();
        String sql = "UPDATE orders SET order_price = ?, payed = ? WHERE order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, newPrice);
            ps.setBoolean(2, false);
            ps.setInt(3, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl i opdatering af ordre i updateOrder()", e.getMessage());
        }
    }
        public static void insertOrder(Order order, ConnectionPool connectionPool){
        }

}











