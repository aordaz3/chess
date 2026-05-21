package server;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;

public class ServerMain {
    public static void main(String[] args) {
        try {
            MySQLAuthDAO authDAO = new MySQLAuthDAO();
            MySQLGameDAO gameDAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
