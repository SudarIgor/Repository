package testClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetSer {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final int PORT = 1002;
    private static NetSer instance;

    public NetSer() throws IOException {
        socket = new Socket("localhost", PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

    }

    public static NetSer getInstance()  {
        if (instance == null){
            try {
                instance = new NetSer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public DataInputStream In() {
        return in;
    }

    public DataOutputStream Out() {
        return out;
    }
}
