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


    public static List<Order> getAllRequest(ConnectionPool connectionPool) throws DatabaseException {
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
                int l = rs.getInt("length");
                int h = rs.getInt("height");
                int w = rs.getInt("width");

                orders.add(new Order(orderId,status,price,payed,date,user,l,h,w));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Fejl i søgning på alle ordrer getOrderById()", e.getMessage());
        }
        return orders;

        //Admin skla kunne se alle forespørgelser så alle orders bliver hentet ud fra db via orderMapper
    }

    public static List<OrderDetail> getAllOrderdetails (int orderId, ConnectionPool connectionPool){
        //Skal hente detaljerne til givne ordre (Stk liste)
    return null;
    }

    public static boolean addRequest(Order order, ConnectionPool connectionPool){
        //Her gemmes det som kunden har indtastet på en ordre i ordretabellen i db
    return false;
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





