package app.services;

import app.entities.Product;
import app.persistence.ConnectionPool;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        Calculator calculator = new Calculator(240,450, connectionPool);
        int expected = 4;

        // 2) Act
        int actual = calculator.calcPostQuantity();

        // 3) Assert
        assertEquals(expected,actual);
    }

    @Test
    void calcPostQuantity_WithLengthBetweenFourAndSixPosts_returnSix() {
        // 1) Arrange
        Calculator calculator = new Calculator(240,480, connectionPool);
        int expected = 6;

        // 2) Act
        int actual = calculator.calcPostQuantity();

        // 3) Assert
        assertEquals(expected,actual);
    }


    /***** Rafters/Spær *****/
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
    void findBestMatchingProduct() {
        Calculator calculator = new Calculator(240, 250, connectionPool);
        List<Product> products = List.of(new Product(240), new Product(270), new Product(300));

        int expectedLength = 270;

        int actualLength = calculator.findBestMatchingProduct(products,250).getLength();

        assertEquals(expectedLength,actualLength);


    }
}