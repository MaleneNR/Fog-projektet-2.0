package app.services;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private List<String> products = new ArrayList<>(); //listen skal bestå af entiteten product, når denne er oprettet
    private int width;
    private int length;

    public Calculator(int width, int length) {
        this.width = width;
        this.length = length;
    }

    // Stolper
    public void calcPosts(){

    }

    // Remme
    public void calcBeams(){

    }

    // Spær
    public void calcRafters(){

    }


}
