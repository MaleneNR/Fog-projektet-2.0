package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.eclipse.jetty.server.Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where email=? and password=?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int role = rs.getInt("role_id");
                //TODO 7: gemme de nye parametre fx:
                String phoneNumber = rs.getString("telefon");
                String name = rs.getString("name");
                String adress = rs.getString("adresse");


                return new User(email, password, role, name, adress, phoneNumber); //TODO 8: Du har opdateret User klassen til at kunne indeholde de nye parametre. Disse skal indsættes her: return new User(email, password, tlf, role);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }


    public static void createUser(String email, String password, String navn, String telefon, String text, ConnectionPool connectionPool) throws DatabaseException {
        {
            //TODO 3. users tabellen i databasen skal udvides til at kunne indeholde de nye parametre.
            //TODO 4. sql stringen nedenfor skal tilpasses de nye parametre.
            String sql = "insert into users (email, password, role_id, name, adresse, telefon) values (?,?,1,?,?,?)";

            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            ) {
                ps.setString(1, email);
                ps.setString(2, password);
                //ps.setInt(3,1);
                ps.setString(3, navn);
                ps.setString(4, text);
                ps.setString(5, telefon);

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
                String navn = rs.getString("name");
                String phoneNumber = rs.getString("telefon");
                String adresse = rs.getString("adresse");


                return new User(email, password, role, navn, adresse, phoneNumber);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }
}
