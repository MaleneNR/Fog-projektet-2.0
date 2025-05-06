package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.eclipse.jetty.server.Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static User login(String email, String password, ConnectionPool cp) throws DatabaseException {
        String sql = "select * from users where email=? and password=?";
        try (
                Connection connection = cp.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int role = rs.getInt("role_id");

                return new User(email, password, role);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }


    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        {
            String sql = "insert into users (email, password, role_id) values (?,?,2)";

            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            ) {
                ps.setString(1, email);
                ps.setString(2, password);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1) {
                    throw new DatabaseException("Fejl ved oprettelse af ny bruger");
                }
            } catch (SQLException e) {
                String msg = "Der er sket en fejl. Prøv igen";
                if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                    msg = "Brugernavnet findes allerede. Vælg et andet";
                }
                throw new DatabaseException(msg, e.getMessage());
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static User getUserById(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where user_id=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int role = rs.getInt("role_id");
                String email = rs.getString("email");
                String password = rs.getString("password");

                return new User(email, password, role);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }
}
