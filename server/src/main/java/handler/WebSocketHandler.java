package handler;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsErrorHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.GameWebSocketService;

public class WebSocketHandler implements
        WsConnectHandler,
        WsMessageHandler,
        WsCloseHandler,
        WsErrorHandler {

    private final GameWebSocketService gameWebSocketService = new GameWebSocketService();

    @Override
    public void handleConnect(WsConnectContext ctx) {
      //ummm
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        gameWebSocketService.handleMessage(ctx);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        gameWebSocketService.handleClose(ctx);
    }

    @Override
    public void handleError(WsErrorContext ctx) {
        System.out.println("Websocket error: " + ctx.error().getMessage());
    }
}