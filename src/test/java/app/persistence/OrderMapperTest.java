package app.persistence;

import app.entities.Order;
import app.entities.OrderDetail;
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
    static void setupClass() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                //View
                stmt.execute("DROP VIEW IF EXISTS test.orderdetails_view");
                // Drop tables og sekvenser
                stmt.execute("DROP TABLE IF EXISTS test.order_details");
                stmt.execute("DROP TABLE IF EXISTS test.products");
                stmt.execute("DROP TABLE IF EXISTS test.materials");
                stmt.execute("DROP TABLE IF EXISTS test.roles");
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP TABLE IF EXISTS test.users");

                stmt.execute("DROP SEQUENCE IF EXISTS test.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.products_product_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.materials_material_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.roles_role_id_seq CASCADE;");

                // Opret tomme kopier af tabeller
                stmt.execute("CREATE TABLE test.roles AS (SELECT * FROM public.roles) WITH NO DATA");
                stmt.execute("CREATE TABLE test.users AS (SELECT * FROM public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * FROM public.orders) WITH NO DATA");
                stmt.execute("CREATE TABLE test.materials AS (SELECT * FROM public.materials) WITH NO DATA");
                stmt.execute("CREATE TABLE test.products AS (SELECT * FROM public.products) WITH NO DATA");
                stmt.execute("CREATE TABLE test.order_details AS (SELECT * FROM public.order_details) WITH NO DATA");

                stmt.execute("""
    CREATE OR REPLACE VIEW test.orderdetails_view AS
    SELECT m.material_id,
           od.order_id,
           m.material,
           m.description,
           p.product_id,
           p.length,
           m.price_per_unit,
           od.quantity,
           m.unit,
           od.total_price,
           od.assembly_description
    FROM order_details od
    JOIN products p ON od.product_id = p.product_id
    JOIN materials m ON p.material_id = m.material_id;
""");

                // Sekvenser og default values
                stmt.execute("CREATE SEQUENCE test.roles_role_id_seq");
                stmt.execute("ALTER TABLE test.roles ALTER COLUMN role_id SET DEFAULT nextval('test.roles_role_id_seq')");

                stmt.execute("CREATE SEQUENCE test.users_user_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_user_id_seq')");

                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

                stmt.execute("CREATE SEQUENCE test.materials_material_id_seq");
                stmt.execute("ALTER TABLE test.materials ALTER COLUMN material_id SET DEFAULT nextval('test.materials_material_id_seq')");

                stmt.execute("CREATE SEQUENCE test.products_product_id_seq");
                stmt.execute("ALTER TABLE test.products ALTER COLUMN product_id SET DEFAULT nextval('test.products_product_id_seq')");

            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database setup failed");
        }
    }


    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Slet indhold i tabeller
                stmt.execute("DELETE FROM test.order_details");
                stmt.execute("DELETE FROM test.products");
                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.users");
                stmt.execute("DELETE FROM test.materials");
                stmt.execute("DELETE FROM test.roles");

                // Indsæt roller
                stmt.execute("INSERT INTO test.roles (role_id, role) VALUES (1, 'Kunde'), (3, 'Admin')");

                // Indsæt brugere
                stmt.execute("INSERT INTO test.users (user_id, email, password, role_id, name, phonenumber, address) VALUES" +
                        "(1, 'malene@hej.dk', '1234', 1, 'Malene', '12345678', 'Lyngbyvej 123')," +
                        "(2, 'john@hej.dk', '1234', 1, 'John', '12345678','Lyngbyvej 789')," +
                        "(3, 'mie@hej.dk', '1234', 3, 'Mie', '12345678','Lyngbyvej 456')");

                stmt.execute("""
            INSERT INTO test.materials (material_id, material, width, height, unit, description, price_per_unit) VALUES
            (1, 'stolpe', 97, 97, 'stk', '97x97mm trykimp. stolpe', 39),
            (2, 'spærtræ', 45, 195, 'stk', '45x195mm spærtræ ubehandlet', 40),
            (3, 'trapezplade', 109, 2, 'stk', '1090x16mm trapezplade, tykkelse 0,7mm, glasklar', 110);
        """);

                // Indsæt produkter
                stmt.execute("""
    INSERT INTO test.products (product_id, length, material_id) VALUES
    (1, 300, 1),
    (2, 270, 1),
    (3, 210, 1),
    (4, 600, 2),
    (5, 540, 2),
    (6, 480, 2),
    (7, 420, 2),
    (8, 360, 2),
    (9, 240, 1),
    (10, 240, 3),
    (11, 300, 3),
    (12, 360, 3),
    (13, 420, 3),
    (14, 480, 3),
    (15, 520, 3),
    (16, 600, 3);
""");


                // Indsæt ordrer
                stmt.execute("INSERT INTO test.orders (order_id, carport_width, carport_length, height, payed, order_status, order_price, user_id, date, shed, tiles) VALUES" +
                        "(1, 600, 780, 220, true, 'Modtaget', 20000, 1, current_date, true, false)," +
                        "(2, 540, 700, 220, false, 'Tilbud sendt', 15000, 2, current_date, true, true)," +
                        "(3, 480, 600, 210, false, 'Betalt', 14000, 1, current_date, false, false)");

                // Indsæt order_details
                stmt.execute("INSERT INTO test.order_details (order_id, product_id, quantity, total_price, assembly_description, material_id) VALUES" +
                        "(1, 1, 10, 500, 'Monteres med skruer', 1)," +
                        "(1, 2, 100, 100, 'Skrues fast', 2)");

                // Sæt sekvenser korrekt
                stmt.execute("SELECT setval('test.roles_role_id_seq', COALESCE((SELECT MAX(role_id)+1 FROM test.roles), 1), false)");
                stmt.execute("SELECT setval('test.users_user_id_seq', COALESCE((SELECT MAX(user_id)+1 FROM test.users), 1), false)");
                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id)+1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.materials_material_id_seq', COALESCE((SELECT MAX(material_id)+1 FROM test.materials), 1), false)");
                stmt.execute("SELECT setval('test.products_product_id_seq', COALESCE((SELECT MAX(product_id)+1 FROM test.products), 1), false)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database data setup failed");
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
            //Arange
            User user = new User(1, "malene@hej.dk", "1234", 1,"Malene","12345678","Lyngbyvej 123");
            Order expected = new Order(1,"Modtaget",20000,true, LocalDate.now(),user,780,220,600, true, true);
            //Act
            Order dbOrder = OrderMapper.getOrderById(1, connectionPool);
            //Assert
            assertEquals(expected, dbOrder);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void getAllRequestsByUserId() {
        try
        {
            // 1) Arrange
            List<Order> orders = OrderMapper.getAllRequestsByUserId(1, connectionPool);
            int expected = 2;
            // 2) Act
            int actual = orders.size();
            // 3) Assert
            assertEquals(expected, actual);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void getOrderDetailsFromViewById() {
        try
        {
            // 1) Arrange
            List<OrderDetail> orderDetails = OrderMapper.getOrderDetailsFromViewById(1, connectionPool);
            int expected = 2;
            // 2) Act
            int actual = orderDetails.size();
            // 3) Assert
            assertEquals(expected, actual);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void addOrderDetails() {
        try
        {
            // 1) Arrange
            User user = new User(1, "malene@hej.dk", "1234", 1,"Malene","12345678","Lyngbyvej 123");
            OrderMapper.addRequest(new Order(4, "Betalt", 15000, true, java.time.LocalDate.now(), user, 780,220, 600,true, false ), connectionPool);
            List<OrderDetail> orderDetails = OrderMapper.getOrderDetailsFromViewById(4, connectionPool);
            int expected = 4;
            // 2) Act
            int actual = orderDetails.size();
            // 3) Assert
            assertEquals(expected, actual);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void addRequest() {
        try
        {
            // 1) Arrange
            User user = new User(1, "malene@hej.dk", "1234", 1,"Malene","12345678","Lyngbyvej 123");
            OrderMapper.addRequest(new Order(4, "Betalt", 15000, true, java.time.LocalDate.now(), user, 780,220, 600,true, false ), connectionPool);
            List<Order> orders = OrderMapper.getAllRequests(connectionPool);
            int expected = 4;
            // 2) Act
            int actual = orders.size();
            // 3) Assert
            assertEquals(expected, actual);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void deleteOrderDetailsAndOrder() {
        try
        {
            // 1) Arrange
            boolean deletedOrder = OrderMapper.deleteOrderDetailsAndOrder(1, connectionPool);
            List<Order> orders = OrderMapper.getAllRequests(connectionPool);
            int expected = 2;
            // 2) Act
            int actual = orders.size();
            // 3) Assert
            assertEquals(expected, actual);
            assertEquals(true, deletedOrder);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void updateStatus() {
        try
        {
            // 1) Arrange
            boolean statusUpdated = OrderMapper.updateStatus("Tilbud sendt", 1, connectionPool);
            Order order = OrderMapper.getOrderById(1, connectionPool);
            String expected = "Tilbud sendt";
            // 2) Act
            String actual = order.getOrderStatus();
            // 3) Assert
            assertEquals(expected, actual);
            assertEquals(true, statusUpdated);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void updatePayed() {
        try
        {
            // 1) Arrange
            boolean payedUpdated = OrderMapper.updatePayed(true, 1, connectionPool);
            Order order = OrderMapper.getOrderById(1, connectionPool);
            Boolean expected = true;
            // 2) Act
            Boolean actual = order.isPayed();
            // 3) Assert
            assertEquals(expected, actual);
            assertEquals(true, payedUpdated);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }

    }

    @Test
    void updatePrice() {
        try
        {
            // 1) Arrange
            boolean priceUpdated = OrderMapper.updatePrice(8000, 1, connectionPool);
            Order order = OrderMapper.getOrderById(1, connectionPool);
            int expected = 8000;
            // 2) Act
            int actual = order.getOrderPrice();
            // 3) Assert
            assertEquals(expected, actual);
            assertEquals(true, priceUpdated);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }

    }

    @Test
    void updateOrder_CheckPrice() {
        try
        {
            // 1) Arrange
            Order order = OrderMapper.getOrderById(1, connectionPool);
            boolean orderUpdated = OrderMapper.updateOrder(order, 8000, connectionPool);
            Order updatedOrder = OrderMapper.getOrderById(1, connectionPool);
            int expected = 8000;
            // 2) Act
            int actual = updatedOrder.getOrderPrice();
            // 3) Assert
            assertEquals(expected, actual);
            assertEquals(true, orderUpdated);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }
    }
    @Test
    void updateOrder_CheckStatus() {
        try
        {
            // 1) Arrange
            Order order = OrderMapper.getOrderById(1, connectionPool);
            boolean orderUpdated = OrderMapper.updateOrder(order, 8000, connectionPool);
            Order updatedOrder = OrderMapper.getOrderById(1, connectionPool);
            String expected = "Tilbud sendt";
            // 2) Act
            String actual = updatedOrder.getOrderStatus();
            // 3) Assert
            assertEquals(expected, actual);
            assertEquals(true, orderUpdated);
        }
        catch (DatabaseException e) {
            fail("Database fejl: " + e.getMessage());
        }



    }
}