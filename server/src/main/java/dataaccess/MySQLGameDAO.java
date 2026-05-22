package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO {

    public MySQLGameDAO() {
        try {
            DatabaseManager.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            System.err.println("Unable to configure game table: " + e.getMessage());
        }
    }

    public void createGame(int gameID, GameData gameInfo) throws DataAccessException {
        String query = "INSERT INTO game (gameID, json) VALUES(?, ?)";
        var serializer = new Gson();
        var json = serializer.toJson(gameInfo);

        try (Connection conex = DatabaseManager.getConnection();
             var statement = conex.prepareStatement(query)) {

            statement.setInt(1, gameID);
            statement.setString(2, json);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT json FROM game WHERE gameID = ?";
        var serializer = new Gson();

        try (Connection conx = DatabaseManager.getConnection();
             var statement = conx.prepareStatement(query)) {

            statement.setInt(1, gameID);
            try (var results = statement.executeQuery()) {
                if (results.next()) {
                    var gameInfo = results.getString("json");
                    return serializer.fromJson(gameInfo, GameData.class);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }

    public void updateGame(GameData gameInfo) throws DataAccessException {
        String query = "UPDATE game SET json = ? WHERE gameID = ?";
        var serializer = new Gson();
        var json = serializer.toJson(gameInfo);

        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement(query)) {

            statement.setString(1, json);
            statement.setInt(2, gameInfo.gameID());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        String query = "SELECT json FROM game";
        var serializer = new Gson();
        Collection<GameData> games = new ArrayList<>();

        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement(query);
             var results = statement.executeQuery()) {

            while (results.next()) {
                var json = results.getString("json");
                GameData game = serializer.fromJson(json, GameData.class);
                games.add(game);
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        String query = "TRUNCATE TABLE game";
        try (Connection cox = DatabaseManager.getConnection();
             var statement = cox.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing game table: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
       """            
       CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL,
            json TEXT NOT NULL,
            PRIMARY KEY (gameID)
       )
       """
    };
}
