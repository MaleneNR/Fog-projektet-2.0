package app.services;

import app.entities.Order;
import app.entities.Product;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "Cupcake";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance("", "", "", "");


    @BeforeAll
            static void setup(){

    }

    /***** Posts/Stolper *****/
    @Test
    void calcPostQuantity_WithHighestLength_returnQuantity() {
        // 1) Arrange
        Calculator calculator = new Calculator(600,780, connectionPool);
        int expected = 6;

        // 2) Act
        int actual = calculator.calcPostQuantity();

        // 3) Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcPostQuantity_WithSmallestLength_returnQuantity() {
        // 1) Arrange
        Calculator calculator = new Calculator(240,240, connectionPool);
        int expected = 4;

        // 2) Act
        int actual = calculator.calcPostQuantity();

        // 3) Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcPostQuantity_WithLengthBetweenFourAndSixPosts_returnFour() {
        // 1) Arrange
        Calculator calculator = new Calculator(240,600, connectionPool);
        int expected = 4;

        // 2) Act
        int actual = calculator.calcPostQuantity();

        // 3) Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcPostQuantity_WithLengthBetweenFourAndSixPosts_returnSix() {
        // 1) Arrange
        Calculator calculator = new Calculator(240,630, connectionPool);
        int expected = 6;

        // 2) Act
        int actual = calculator.calcPostQuantity();

        // 3) Assert
        assertEquals(expected,actual);
    }


    /***** Rafters/Spær *****/
    @Test
    void calcRaftersQuantity_OverMaxLength() {
        Calculator calculator = new Calculator(600, 840, connectionPool);
        int expected = 15;

        int actual = calculator.calcRaftersQuantity();

        assertEquals(expected, actual);
    }


    @Test
    void calcRaftersQuantity_WithMaxLength() {
        Calculator calculator = new Calculator(600, 780, connectionPool);
        int expected = 14;

        int actual = calculator.calcRaftersQuantity();

        assertEquals(expected, actual);
    }

    @Test
    void calcRaftersQuantity_WithMinLength() {
        Calculator calculator = new Calculator(240, 240, connectionPool);
        int expected = 5;

        int actual = calculator.calcRaftersQuantity();

        assertEquals(expected, actual);
    }

    @Test
    void calcRaftersQuantity_UnderMinLength() {
        Calculator calculator = new Calculator(240, 50, connectionPool);
        //Spær tilføjes for hver 60. cm + et spær i enden
        int expected = 1;

        int actual = calculator.calcRaftersQuantity();

        assertEquals(expected, actual);
    }

    @Test
    void findBestMatchingProduct() {
        Calculator calculator = new Calculator(240, 250, connectionPool);
        List<Product> products = List.of(new Product(240), new Product(270), new Product(300));

        int expectedLength = 270;

        int actualLength = calculator.findBestMatchingProduct(products,250).getLength();

        assertEquals(expectedLength,actualLength);


    }

    @Test
    void calcOrderPrice() throws DatabaseException {
        //Arrange
        User user = new User(1, "malene@hej.dk", "1234", 1,"Malene","12345678","Lyngbyvej 123");
        Order order = new Order(1,"Modtaget",20000,true, LocalDate.now(),user,780,270,600, true, true);
        Calculator calculator = new Calculator(600, 780, connectionPool);
        calculator.calcCarport(order);

        int expectedPrice = MaterialMapper.getMaterialById(1, connectionPool).getPricePerUnit()*(order.getHeight()/100) * calculator.calcPostQuantity();
        expectedPrice += MaterialMapper.getMaterialById(2, connectionPool).getPricePerUnit()*(order.getWidth()/100)*(calculator.calcRaftersQuantity());
        expectedPrice += MaterialMapper.getMaterialById(2, connectionPool).getPricePerUnit()*(order.getLength()/100)*2;
                                                        //30 pga. der er et overlap på 30 cm, hvis man skal have to rækker tagplader
        expectedPrice += MaterialMapper.getMaterialById(3,connectionPool).getPricePerUnit()*((order.getLength()+30)/100)* calculator.calcTilesQuantity();

        //Act
        int actual = calculator.getOrderPrice();

        //Assert
        assertEquals(expectedPrice, actual);
    }


}