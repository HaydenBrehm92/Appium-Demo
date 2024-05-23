package core;
import com.google.gson.Gson;
import core.JsonHelpers.JsonMessageConstants;
import core.JsonHelpers.JsonToTestData;
import core.JsonHelpers.UserConnected;
import core.managers.TestManager;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * SocketServer opens and closes connection to clients, and it receives and broadcasts messages to clients. Will be using port
 * 8080 for now.
 * @author Hayden Brehm
 */
public class SocketServer extends WebSocketServer {
    private int retry = 1;
    private JsonToTestData jsonToTestData;

    /**
     * Constructor that uses only the port information to create the object.
     * @param port the port we wish to use to open the socket
     * @throws UnknownHostException when IP cannot be found on the host.
     * @author Hayden Brehm
     */
    public SocketServer(int port) throws UnknownHostException{
        super(new InetSocketAddress(port));
    }

    /**
     * Constructor that uses the InetSocketAddress address to create the object.
     * @param address the address we wish to use to open the socket.
     * @author Hayden Brehm
     */
    public SocketServer(InetSocketAddress address){
        super(address);
    }

    /**
     * Constructor that uses the port & Draft_6455 to create the object.
     * @param port  the port we wish to use to open the socket.
     * @param draft the RFC6455 websocket protocol draft we wish to use to open the socket.
     * @author Hayden Brehm
     */
    public SocketServer(int port, Draft_6455 draft){
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    /**
     * Runs when SocketServer opens a connection with a Client.
     * @param conn      The <tt>WebSocket</tt> instance this event is occurring on.
     * @param handshake The handshake of the websocket instance
     * @author Hayden Brehm
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        UserConnected userConnected = new UserConnected();
        conn.send("[SERVER] Hi from tool!");
        //MyLogger.log.debug("[SERVER] String is: {} and {}", userConnected.getDevicesArrayList().get(0).getDeviceID(), userConnected.getTestInfoArrayList().get(0).getTestName());
        conn.send(userConnected.toJson());
        MyLogger.log.debug("[SERVER] opened connection");
    }

    /**
     * Runs when SocketServer socket is closed be either the Server or Client.
     * @param conn   The <tt>WebSocket</tt> instance this event is occurring on.
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether the closing of the connection was initiated by the remote
     *               host.
     * @author Hayden Brehm
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        MyLogger.log.debug(
                "[SERVER] Connection closed by {} Code: {} Reason: {}",
                (remote ? "remote peer" : "us"), code, reason);
    }

    /**
     * Runs when SocketServer receives a message of the type String.
     * @param conn    The <tt>WebSocket</tt> instance this event is occurring on.
     * @param message The UTF-8 decoded message that was received.
     * @author Hayden Brehm
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        MyLogger.log.debug("[SERVER] Received: {}", message);

        if (message.contains(JsonMessageConstants.UserInput)) {
            TestManager.jsonToTestData = new Gson().fromJson(message, JsonToTestData.class);
        }
        else if (message.contains(JsonMessageConstants.Run)){
            MyLogger.log.debug("[SERVER] Received Run Message");
        }else if (message.contains(JsonMessageConstants.Cancel)) {
            MyLogger.log.debug("[SERVER] Received Cancel Message");
        }else if (message.contains(JsonMessageConstants.Shutdown)){
            MyLogger.log.debug("[SERVER] Received Shutdown Message");
            System.exit(0);
        }
        else MyLogger.log.debug("[SERVER] Received Unhandled Message: {}", message);
    }

    /**
     * Runs when SocketServer receives a message of the type ByteBuffer.
     * @param conn    The <tt>WebSocket</tt> instance this event is occurring on.
     * @param message The binary message that was received.
     * @author Hayden Brehm
     */
    // DON'T USE THIS IF POSSIBLE
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        //broadcast(message.array());
        MyLogger.log.debug("[SERVER] Received: {}", message);
    }

    /**
     * Runs when SocketServer encounters an exception with the websocket.
     * @param conn Can be null if their error does not belong to one specific websocket. For example
     *             if the servers port could not be bound.
     * @param ex   The exception causing this error
     * @author Hayden Brehm
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Runs when SocketServer is started.
     * @author Hayden Brehm
     */
    @Override
    public void onStart() {
        MyLogger.log.debug("[SERVER] Server Started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }



}
