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
    private static String rootDir;
    private String clientDir = "serverDir" +File.separator + "client 0" + File.separator.concat("ServerRoot");
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

                }
                else if(command.equals("close")){
                    socket.close();
                    in.close();
                    out.close();
                }
                else if(command.equals("checkLogin")){
                    boolean checkLogin;
                    System.out.println("into check login");
                    String login = in.readUTF();
                    System.out.println("read login");
                    checkLogin = authService.getSample().getUserDao().userExists(login);
                    System.out.println("check login");
                    if(checkLogin){
                       out.writeBoolean(true);
                        System.out.println("True");
                    }else {
                        out.writeBoolean(false);
                        System.out.println("false");

                    }
                }
                else if(command.equals("registration")){
                    String login;
                    String pass;
                    login = in.readUTF();
                    pass = in.readUTF();
                    System.out.println("login on server: " + login + " " + "password on server: "+ pass);
                    new Registration(login,pass);

                }
                else if (command.equals("auth")){
                    authService = AuthServiceImpl.getSample();
                     login = in.readUTF();
                     pass = in.readUTF();
                     if (authService.auth(login,pass)){
                         out.writeUTF("login");
                         clientDir="serverDir" + File.separator + "client "
                                   .concat( authService.getUserDao().getUser_id(login))
                                   .concat(File.separator + "ServerRoot");
                         rootDir ="serverDir" + File.separator + "client "
                                 .concat( authService.getUserDao().getUser_id(login))
                                 .concat(File.separator + "ServerRoot");

                         out.writeUTF(clientDir);
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
                else  if (command.startsWith("new Dir ")){
                    clientDir+=File.separator + command.split("new Dir ")[1];
                    System.out.println("new dir on server: " + clientDir);
                }
                else  if(command.equals("getParentDir")){
                    System.out.println("in getParentDir");
                    File file = new File(clientDir);

                    if(!file.getPath().equals(rootDir)) {
                        clientDir = file.getParent();
                        out.writeUTF(clientDir);
                    }
                    else {
                        out.writeUTF(clientDir);
                    }
                    System.out.println("afte " + file.getPath());
                }
                else if("list-files".equals(command)){
                    try {
                        File dir = new File(clientDir);
                        byte[] bytes = null;
                        if(!isDirectoryEmpty(dir)){
                            for (File file : dir.listFiles()) {
                                if(file.isDirectory()) bytes = ("Directory: " + file.getName().concat("\n")).getBytes();
                                else bytes = ("File: " + file.getName().concat("\n")).getBytes();
                                out.write(bytes);
                            }
                            out.write("end".getBytes());
                        }else out.write("\nend".getBytes());
//
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

    public boolean isDirectoryEmpty(File directory) {
        String[] files = directory.list();
        return files.length == 0;
    }

}
