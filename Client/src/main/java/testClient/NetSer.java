package testClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetSer {
    private  Socket socket;
    private  DataInputStream in;
    private  DataOutputStream out;
    private  int PORT = 1002;
    private static NetSer instance;

    public NetSer()  {
        try {
            socket = new Socket("localhost", PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NetSer getInstance()  {
        if (instance == null){
            instance = new NetSer();
        }
        return instance;
    }

    public DataInputStream In() {
        return in;
    }

    public DataOutputStream Out() {
        return out;
    }
    public void close(){


        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}