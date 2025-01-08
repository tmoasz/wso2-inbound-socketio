package pl.tmoasz.wso2.inbound.socketio;

import org.apache.synapse.SynapseException;

public class SocketIOInboundException extends SynapseException {
  public SocketIOInboundException(String message) {
    super(message);
  }

  public SocketIOInboundException(String message, Throwable cause) {
    super(message, cause);
  }
}