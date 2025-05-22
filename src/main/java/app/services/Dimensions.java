package app.services;

import java.util.ArrayList;
import java.util.List;

public class Dimensions {
//Til drop down menuerne, kaldes i orderController
    public static List<Integer> options (int start, int end, int interval){
        List<Integer> options = new ArrayList<>();
        for(int i = start; i <= end; i += interval){
            options.add(i);
        }
        return options;
    }


}