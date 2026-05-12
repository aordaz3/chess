package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void createGame(Integer gameID, GameData gameInfo){
        games.put(gameID, gameInfo);
    }
    public GameData getGame(Integer gameID){
        return games.get(gameID);
    }
    public Collection<GameData> listGames(){
        return games.values();
    }
    public void updateGame(GameData gameInfo){
        games.put(gameInfo.gameID(), gameInfo);
    }
    public void clear(){
        games.clear();
    }
}
