/*
package core.managers;

import core.MyLogger;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

*/
/**
 * Bluetooth Server implementation that uses Serial Port UUID. Can connect to devices and listen.
 * @deprecated No longer using bluetooth
 * For testing purposes only.
*//*

@Deprecated
public class BTServerManager implements Runnable {
    private final String connectionName = "KodiakPoCApplication";
    private final UUID uuid = new UUID("0000110100001000800000805F9B34FB", false);

    public BTServerManager(){}
    @Override
    public void run() {
        waitForConnection();
    }

    */
/** Waiting for connection from devices *//*

    private void waitForConnection() {
        LocalDevice local;
        StreamConnectionNotifier notifier;
        StreamConnection connection;

        // Listening for connection
        try {
            local = LocalDevice.getLocalDevice();
            MyLogger.log.debug("Local device address: {}", local.getBluetoothAddress());
            MyLogger.log.debug("Local device name: {}", local.getFriendlyName());

            if(!local.setDiscoverable(DiscoveryAgent.GIAC))
                MyLogger.log.debug("Failed to change to the discoverable mode");

            String url = "btspp://localhost:" + uuid + ";authenticate=false;encrypt=false;name=" + connectionName;
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // waiting for connection & then keeping it alive with Listener
        try {
            MyLogger.log.debug("waiting for connection...");
            connection = notifier.acceptAndOpen();
            RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(connection);
            MyLogger.log.debug("Remote device address: {}", remoteDevice.getBluetoothAddress());
            MyLogger.log.debug("Remote device name: {}", remoteDevice.getFriendlyName(true));
            MyLogger.log.debug("After AcceptAndOpen...");
            new Listener(connection).start(); //probably more for a server approach

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class Listener extends Thread{
        private OutputStream outputStreams;
        private InputStream inputStream;

        public Listener(StreamConnection connection){
            try {
                this.outputStreams = connection.openOutputStream();
                outputStreams.flush();
                this.inputStream = connection.openInputStream();
            } catch (IOException e) {
                MyLogger.log.debug("IOException Occurred! ----> {}", e.getMessage());
                e.printStackTrace();
            }
        }

        public void run(){
            StringBuilder builder;
            try{
                String greeting = "JSR-82 RFCOMM server says hello";
                outputStreams.write(greeting.getBytes());

                while(true){
                    builder = new StringBuilder();
                    int tmp = 0;
                    MyLogger.log.debug("Inside loop");
                    while((tmp = inputStream.read()) != -1){
                        builder.append((char) tmp);
                    }
                    MyLogger.log.debug("received: {}", builder);
                }
            }
            catch(IOException e){
                MyLogger.log.debug("IOException Occurred! ----> {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
*/
