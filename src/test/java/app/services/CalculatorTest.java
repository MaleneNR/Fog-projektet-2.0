package app.services;

import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    @Test
    void calcPostQuantity() {
        Calculator calculator = new Calculator(600,780, connectionPool);
        assertEquals(6,calculator.calcPostQuantity());
    }

    @Test
    void calcRaftersQuantity() {
        Calculator calculator = new Calculator(600, 780, connectionPool);
        assertEquals(14, calculator.calcRaftersQuantity());
    }
}