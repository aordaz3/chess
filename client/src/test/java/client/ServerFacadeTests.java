package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



public class ServerFacadeTests {
    private static Server server;
    private static int port;
    private static String serverUrl;
    private static final HttpClient client = HttpClient.newHttpClient();
    private ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    @BeforeEach
    public void clearDatabase() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        facade = new ServerFacade(serverUrl);
    }
    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(auth);
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertFalse(auth.authToken().isBlank());
        Assertions.assertEquals("player1", auth.username());
    }

    @Test
    public void registerNegativeDuplicateUsername() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Exception ex = Assertions.assertThrows(Exception.class, () ->
                facade.register("player1", "differentPassword", "p2@email.com"));
        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("register failed")
                                    || ex.getMessage().toLowerCase().contains("already taken")
                                    || ex.getMessage().toLowerCase().contains("bad request"));
    }
    @Test
    public void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData auth = facade.login("player1", "password");
        Assertions.assertNotNull(auth);
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertFalse(auth.authToken().isBlank());
        Assertions.assertEquals("player1", auth.username());
    }

    @Test
    public void loginNegativeWrongPassword() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Exception ex = Assertions.assertThrows(Exception.class, () ->
                facade.login("player1", "wrongpassword"));
        Assertions.assertTrue(ex.getMessage().toLowerCase().contains("login failed")
                                    || ex.getMessage().toLowerCase().contains("unauthorized"));
    }
    @Test
    public void logoutPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    public void logoutNegative() {
        //hmmm
    }
}
