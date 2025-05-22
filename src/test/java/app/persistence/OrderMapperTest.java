package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @BeforeAll
    static void setupClass()
    {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test.users");
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test.users AS (SELECT * from public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                // Create sequences for auto generating id's for users and orders
                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");

                stmt.execute("INSERT INTO test.users (user_id, email, password, role_id, name, phonenumber, address) " +
                        "VALUES  (1, 'malene@hej.dk', '1234', '1','Malene','12345678', 'Lyngbyvej 123'), (2, 'mie@hej.dk', '1234', '3', 'Mie', '12345678','Lyngbyvej 456')"); //role_id 1 er kunde, role_id 3 er admin.

                stmt.execute("INSERT INTO test.orders (order_id, carport_width, carport_length, payed, order_status, order_price, user_id, date, shed, tiles) " +
                        "VALUES (1, 600, 780,true, 'Shipped', 20000, 1,current_date, true, false), (2, 540, 700,false, 'Waiting', 15000, 2,current_date,true,true), (3, 480, 600,false, 'Request', 14000, 1,current_date, false, false);") ;
                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id) + 1 FROM test.users), 1), false)");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @Test
    void getAllRequests() {
        try
        {
            // 1) Arrange
            List<Order> orders = OrderMapper.getAllRequests(connectionPool);
            int expected = 3;
            // 2) Act
            int actual = orders.size();
            // 3) Assert
            System.out.println(orders);
            assertEquals(expected, actual);
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void getOrderById()
    {
        try
        {
            User user = new User(1, "malene@hej.dk", "1234", 1,"Malene","12345678","Lyngbyvej 123");
            Order expected = new Order(1,"Shipped",20000,true, LocalDate.now(),user,780,0,600);
            Order dbOrder = OrderMapper.getOrderById(1, connectionPool);
            assertEquals(expected, dbOrder);
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }

//    @Test
//    void insertOrder()
//    {
//        try
//        {
//            User user = new User(1, "jon", "1234", "customer");
//            Order newOrder = new Order(2, 550, 750, 20000, user);
//            newOrder = OrderMapper.insertOrder(newOrder, connectionPool);
//            Order dbOrder = OrderMapper.getOrderById(newOrder.getOrderId(), connectionPool);
//            assertEquals(newOrder, dbOrder);
//        }
//        catch (DatabaseException e)
//        {
//            fail("Database fejl: " + e.getMessage());
//        }
//    }
}