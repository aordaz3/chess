package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;

public class MySQLGameDAO {

    public void createGame(int gameID, GameData gameInfo){
        try(var conex = DatabaseManager.getConnection()){
            var serializer = new Gson();
            var json = serializer.toJson(gameInfo);
            try(var statement = conex.prepareStatement("INSERT INTO game (gameID, json) VALUES(?,?)")){
                statement.setInt(1, gameID);
                statement.setString(2, json);
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public GameData getGame(int gameID){
        var serializer = new Gson();
        String query = "SELECT gameID, json FROM game WHERE gameID = ?";
        try(var conx = DatabaseManager.getConnection();
            var statement = conx.prepareStatement(query)){
            statement.setInt(1, gameID);
            try (var results = statement.executeQuery()){
                if (results.next()) {
                    var gameInfo = results.getString("json");
                    return serializer.fromJson(gameInfo, GameData.class);
                }
            }
            return null;
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateGame(GameData gameInfo){

    }
    public void clear(){

    }
}
