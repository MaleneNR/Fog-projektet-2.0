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

    public static Material getMaterialById(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        if (materialId <= 0) {
            throw new IllegalArgumentException("Ugyldigt materialId: " + materialId);
        }

        String sql = "SELECT * FROM materials WHERE material_id = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setInt(1,materialId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String title = rs.getString("material");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                String unit = rs.getString("unit");
                String description = rs.getString("description");
                int pricePerUnit = rs.getInt("price_per_unit");

                return new Material(materialId, title, width, height, unit, description, pricePerUnit);
            } else {
                throw new DatabaseException("Materiale med id " + materialId + " blev ikke fundet.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af materiale", e.getMessage());
        }


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




}
