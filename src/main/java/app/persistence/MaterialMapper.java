package app.persistence;

import app.entities.Material;
import app.entities.Product;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
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

    public static List<Product> getProductsByMaterialId(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE material_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                )
        {
            ps.setInt(1,materialId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int productId = rs.getInt("product_id");
                int length = rs.getInt("length");

                Product product = new Product(productId,length,materialId);
                products.add(product);
            }

        } catch (SQLException e) {
            throw new DatabaseException("getProductsByMaterialId() failed",e.getMessage());
        }

        return products;
    }



}
