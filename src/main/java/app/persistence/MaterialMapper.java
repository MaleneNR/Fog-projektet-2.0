package app.persistence;

import app.entities.Material;

import java.util.List;

//måske skal vi lave et view i db med materials og products evt. (Orderdetails) dvs to tabeller skal joines

public class MaterialMapper {

    public static List<Integer> getAllLengthsByMaterialId (int materialId, ConnectionPool connectionPool){
        //den skal bruges i beregneren
        //fra db skal den hente i products tabellen alle længderne på det givne materiale id
        return null;
    }

    public static List<Material> getAllDetails (ConnectionPool connectionPool){
        //henter alle kolonner i material tabellen som er beregnet til ønskede carport
        //Admin bruger denne til stk. liste
return null;
    }



}
