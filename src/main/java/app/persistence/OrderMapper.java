package app.persistence;

import app.entities.Order;
import app.entities.OrderDetail;
import app.entities.User;
import app.exceptions.DatabaseException;

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
            if (rs.next())
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

                orders.add(new Order(orderId,status,price,payed,date,user,l,h,w));
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
        List<OrderDetail> orderDetails = new ArrayList<>();//TODO skal hente details ud, IKKE FÆRDIG
        String sql = "SELECT \n" +
                "  description,\n" +
                "  length,\n" +
                "  quantity,\n" +
                "  unit,\n" +
                "  assembly_description  \n" +
                "FROM orderdetails_view;";

        try (
                Connection connection = connectionPool.getConnection();
                Statement s = connection.createStatement();
        )
        {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next())
            {
                String description = rs.getString("description");
                int length = rs.getInt("length");
                int pricePerUnit = rs.getInt("price_per_unit");
                int quantity = rs.getInt("quantity");
                String unit = rs.getString("unit"); 
                String assemblyDescription = rs.getString("assembly_description");

                //orderDetails.add(new OrderDetail(orderId,));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer, getAllRequests()", e.getMessage());
        }



        //Skal hente detaljerne til givne ordre (Stk liste)
    return null;  //TODO Skal returnerer en order_detail
    }

    public static boolean addRequest(Order order, ConnectionPool connectionPool) throws DatabaseException {
        int rowsAffected = 0;
        Boolean orderAdded = false;

            String status = "Received";  //TODO Skal dette hardcodes
            LocalDate dateOfToday = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());

        String sql = "INSERT INTO orders (order_status, order_price, payed, date, user_id, carport_length, height, carport_width) values (?,?,?,?,?,?,?,?) RETURNING order_id";

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

            rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt("order_id");
                        order = new Order(orderId,status,20000, order.isPayed(), dateOfToday, order.getUser(),order.getLength(),order.getHeight(),order.getWidth());
                        orderAdded = true;
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

    //vi har en funktion der hedder addOderDetails men tænker det er beregneren der styrer det


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
                if (rs.next())
                {
                    String status = rs.getString("order_status");
                    int price = rs.getInt("order_price");
                    boolean payed = rs.getBoolean("payed");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    User user = UserMapper.getUserById(rs.getInt("user_id"), connectionPool);
                    int l = rs.getInt("length");
                    int h = rs.getInt("height");
                    int w = rs.getInt("width");

                    order = new Order(orderId,status,price,payed,date,user,l,h,w);
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Fejl i søgning på en ordre ved id" + orderId+ " i getOrderById()", e.getMessage());
            }
            return order;
        }
    }





