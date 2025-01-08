package pl.tmoasz.wso2.inbound.socketio;

import java.net.URISyntaxException;
import java.util.Properties;

import io.socket.client.IO;
import io.socket.client.IO.Options;
import io.socket.client.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.inbound.InboundProcessorParams;
import org.json.JSONObject;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericInboundListener;

public class SocketIOListener extends GenericInboundListener {
  private static final Log log = LogFactory.getLog(SocketIOListener.class);
  private Properties properties;
  private String address;
  private Socket socketIO;

  public SocketIOListener(InboundProcessorParams params) {
    super(params);

    this.params = params;
    this.properties = params.getProperties();
    this.address = properties.getProperty(SocketIOConstants.SOCKETIO_ADDRESS);
  }

  public void destroy() {
    if (socketIO != null) {
      socketIO.disconnect();
    }
  }

  public void init() {
    try {
      validateProperties();
      Options socketIoOptions = fromProp(properties);
      socketIO = IO.socket(address, socketIoOptions);
      setupEventHandlers();
      socketIO.connect();
      log.info("SocketIO connection initialized successfully");
    } catch (URISyntaxException e) {
      log.error("Failed to initialize SocketIO connection", e);
      handleException("SocketIO initialization failed", e);
      throw new SocketIOInboundException("SocketIO initialization failed", e);
    }
  }

  private Options fromProp(Properties properties) {
    Options options = new Options();

    // Parse port
    options.port = Integer.parseInt(properties.getProperty(
        SocketIOConstants.SOCKETIO_PORT,
        SocketIOConstants.SOCKETIO_PORT_DEFAULT));
    // Set reconnection options
    String reconnectionProperty = properties.getProperty(SocketIOConstants.SOCKETIO_RECONNECTION, "true");
    options.reconnection = Boolean.valueOf(reconnectionProperty);

    String reconnectionAttempts = properties.getProperty(SocketIOConstants.SOCKETIO_RECONNECTION_ATTEMPTS);
    if (reconnectionAttempts != null) {
      options.reconnectionAttempts = Integer.parseInt(reconnectionAttempts);
    }

    // Set timeout
    String timeout = properties.getProperty(SocketIOConstants.SOCKETIO_TIMEOUT);
    if (timeout != null) {
      options.timeout = Integer.parseInt(timeout);
    }

    return options;
  }

  private void setupEventHandlers() {
    socketIO.on(Socket.EVENT_CONNECT, args -> {
      log.info("Connected to SocketIO server");
      // Implement connection logic
    }).on("event", args -> {
      try {
        if (args.length > 0 && args[0] instanceof JSONObject) {
          JSONObject jsonObject = (JSONObject) args[0];
          handleMessage(jsonObject);
        } else {
          log.warn("Received invalid message format");
        }
      } catch (Exception e) {
        log.error("Error processing message", e);
      }
    }).on(Socket.EVENT_DISCONNECT, args -> {
      log.info("Disconnected from SocketIO server");
      // Implement disconnect handling
    });
  }

  private void validateProperties() {
    if (address == null || address.trim().length() == 0) {
      throw new IllegalStateException("SocketIO address is required");
    }

    try {
      int port = Integer.parseInt(properties.getProperty(SocketIOConstants.SOCKETIO_PORT,
          SocketIOConstants.SOCKETIO_PORT_DEFAULT));
      if (port < 0 || port > 65535) {
        throw new IllegalStateException("Invalid port number: " + port);
      }
    } catch (NumberFormatException e) {
      throw new SocketIOInboundException("Invalid port number format", e);
    }
  }

  private void handleMessage(JSONObject message) {
    try {
      // TODO: Implement message handling logic
      // This should integrate with WSO2 message injection
      log.debug("Received message: " + message.toString());
    } catch (Exception e) {
      log.error("Failed to process message: " + message, e);
    }
  }
}
