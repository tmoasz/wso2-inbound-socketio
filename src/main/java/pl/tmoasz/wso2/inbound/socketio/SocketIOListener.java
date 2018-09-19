package pl.tmoasz.wso2.inbound.socketio;

import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import io.socket.client.IO;
import io.socket.client.IO.Options;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.inbound.InboundProcessorParams;
import org.json.JSONObject;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericInboundListener;

public class SocketIOListener extends GenericInboundListener {
	private static final Log log = LogFactory.getLog(SocketIOListener.class);
	private Properties properties;
	private String address;
	
	public SocketIOListener(InboundProcessorParams params) {
	       super(params);
	       
	       this.params = params;
	       this.properties = params.getProperties();
	       this.address = properties.getProperty(SocketIOConstans.SOCKETIO_ADDRESS);
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void init() {
		try {
			ExecutorService exec = params.getSynapseEnvironment().getExecutorService();
			
			Options socketIoOptions = fromProp(properties);
			final Socket socket = IO.socket(address, socketIoOptions);
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				  //@Override
				  public void call(Object... args) {
				    socket.emit("foo", "hi");
				    socket.disconnect();
				  }

				}).on("event", new Emitter.Listener() {

				  public void call(Object... args) {
					  JSONObject obj = (JSONObject)args[0];
				  }

				}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				  //@Override
				  public void call(Object... args) {}

				});
				socket.connect();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.error("Init socket.io: "+ e.getMessage());
		}
	}

	private Options fromProp(Properties properties2) {
		Options option = new Options();
		//option.
		return null;
	}
	
}
