package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

/**
 * Обработчик входящих клиентов
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private String login;
    private String pass;
    private String clientDir = "serverDir" +File.separator + " 0";
    private AuthServiceImpl authService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream())){
            System.out.println("connect " + socket.getRemoteSocketAddress());

            while (true) {
                String command = in.readUTF();
                if (command.equals("ServDir")){
                    clientDir=in.readUTF();
                    System.out.println("test point \n" + clientDir);
                }
                else if (command.equals("auth")){
                    authService = AuthServiceImpl.getSample();
                     login = in.readUTF();
                     pass = in.readUTF();
                     if (authService.auth(login,pass)){
                         out.writeUTF("login");
                         if (login.equals("admin")){
                             clientDir = "serverDir";
                         }else {
                             clientDir="serverDir" + File.separator + "client "
                                     .concat( authService.getUserDao().getUser_id(login));
                             out.writeUTF(clientDir);
                         }

                     }else out.writeUTF("auth passed");

                }
                else if ("upload".equals(command)) {
                    try {
                        File file = new File(clientDir + File.separator + in.readUTF());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        long length = in.readLong();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[256];
                        for (int i = 0; i < (length + 255) / 256; i++) {
                            int read = in.read(buffer);
                            fos.write(buffer, 0, read);
                        }
                        fos.close();
                        out.writeUTF("DONE");
                    } catch (Exception e) {
                        out.writeUTF("ERROR");
                    }
                }
                else if ("download".equals(command)) {
                    try {
                        String filename = in.readUTF();
                        File file = new File(clientDir + File.separator + filename);
                        if (!file.exists()) out.writeUTF("x00DF");

                        else {
                            out.writeUTF("x00DD");
                            long length = file.length();
                            out.writeLong(length);
                            FileInputStream fis = new FileInputStream(file);
                            int read = 0;
                            byte[] buffer = new byte[256];
                            while ((read = fis.read(buffer)) != -1) {
                                out.write(buffer, 0, read);
                            }
                            out.flush();
                        }
                    } catch (Exception e) {
                        out.writeUTF("ERROR");
                }
                }
                else if ("remove".equals(command)) {
                    try {
                        File file = new File(clientDir + File.separator + in.readUTF());
                        if (file.exists()) {
                            file.delete();
                            out.writeUTF( "file removed");
                        }
                        else out.writeUTF( "the file is missing");

                    } catch (Exception e) {
                        out.writeUTF("ERROR");
                    }
                }
                else if("list-files".equals(command)){
                    try {
                        File dir = new File(clientDir);

                            for (File file : dir.listFiles()) {

                                byte[] bytes = file.getName().concat("\n").getBytes();
                                out.write(bytes);
//
                            }
                            out.write("end".getBytes());
//                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (SocketException e){
            System.out.println(clientDir + socket.getRemoteSocketAddress() +" drop");
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}