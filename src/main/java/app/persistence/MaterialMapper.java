package app.persistence;

import app.entities.Material;
import app.entities.OrderDetail;
import app.entities.Product;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//måske skal vi lave et view i db med materials og products evt. (Orderdetails) dvs to tabeller skal joines

public class MaterialMapper {

    public static Material getMaterialById (int materialId, ConnectionPool connectionPool) throws DatabaseException {
        Material material = null;
        String sql = "SELECT * FROM materials WHERE material_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        )
        {
            ps.setInt(1,materialId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String title = rs.getString("material");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String unit = rs.getString("unit");
                String description = rs.getString("description");
                int pricePerUnit = rs.getInt("price_per_unit");

                material = new Material(materialId, title, width, height, unit, description, pricePerUnit);
            }
            if(materialId == 0){
                throw new NullPointerException("materialId is 0");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't find material, getMaterialById()",e.getMessage());
        }
        return material;
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
            while(rs.next()){
                int productId = rs.getInt("product_id");
                int length = rs.getInt("length");

                Material material = getMaterialById(materialId,connectionPool);
                Product product = new Product(productId,length,material);
                products.add(product);
            }

        } catch (SQLException e) {
            throw new DatabaseException("getProductsByMaterialId() failed",e.getMessage());
        }

        return products;
    }

    public static Product getProductById(int productId, ConnectionPool connectionPool) throws DatabaseException {
        Product product = null;
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        )
        {
            ps.setInt(1,productId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int materialId = rs.getInt("material_id");
                int length = rs.getInt("length");

                Material material = getMaterialById(materialId,connectionPool);
                product = new Product(productId,length,material);

            }

        } catch (SQLException e) {
            throw new DatabaseException("getProductsByMaterialId() failed",e.getMessage());
        }

        return product;
    }


}
