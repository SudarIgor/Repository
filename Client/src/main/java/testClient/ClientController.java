package testClient;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class ClientController implements Initializable {

    private NetSer netSer;
    private String serverDir;

    @FXML
    Pane serverPane, clientPane;

    @FXML
    TextField pathFieldServer, pathFieldClient;


    public ListView <String> clientListView;

    @FXML
    TextField pathField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        try {
            netSer = new NetSer();
            serverDir = ClientPath.getPath();
            netSer.Out().writeUTF("ServDir");
            netSer.Out().writeUTF(serverDir);
            updateClientList();
            System.out.println("updateClientList()");
            updateServerList();
            System.out.println("updateServerList()");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void updateClientList() {
        List<String> list = new ArrayList<>();
        File file = new File("DirClient");
        String[] files = file.list();

        for (String fileName : files) {
            list.add(fileName);}
        ObservableList<String> langs = FXCollections.observableArrayList(list);
        ListView<String> langsListView = new ListView<String>(langs);
        clientPane.getChildren().addAll(langsListView);

        clientListView = new ListView<String>(langs);


    }

    private void updateServerList() {
        List<String> list = downloadFileList();
        ObservableList<String> langs = FXCollections.observableArrayList(list);
        ListView<String> langsListView = new ListView<String>(langs);
        serverPane.getChildren().addAll(langsListView);

    }

    public static List<String> list = new ArrayList<>();


    private List<String> downloadFileList() {
        List<String> list = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            netSer.Out().writeUTF("list-files");
//            if(netSer.In().readUTF().equals("sleep")) return list;
            while (true) {
                byte[] buffer = new byte[512];
                int size = netSer.In().read(buffer);
                sb.append(new String(buffer, 0, size));
                if (sb.toString().endsWith("end")) {
                    break;
                }
            }
            String fileString = sb.substring(0, sb.toString().length() - 4);
            list = Arrays.asList(fileString.split("\n"));
        }catch (FileNotFoundException e){
            System.out.println(" FileNotFoundException");
        }catch (IOException e) {
            e.printStackTrace();
    }

    return list;
}

    public void btUpload(ActionEvent actionEvent) {
        String filename = pathField.getText();
        if(!filename.equals("")) {
            try {

                File file = new File("DirClient" + File.separator + filename);
                if (file.exists()) {
                    netSer.Out().writeUTF("upload");
                    netSer.Out().writeUTF(filename);
                    long length = file.length();
                    netSer.Out().writeLong(length);
                    FileInputStream fis = new FileInputStream(file);
                    int read = 0;
                    byte[] buffer = new byte[256];
                    while ((read = fis.read(buffer)) != -1) {
                        netSer.Out().write(buffer, 0, read);
                    }
                    netSer.Out().flush();
                    String status = netSer.In().readUTF();

                } else {
                    pathField.setText("File is not exists");
                }
            } catch (FileNotFoundException e) {
                System.out.println(" FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Something error");
            }
            updateServerList();
        }

    }

    public void btRemove(ActionEvent actionEvent) {
        String filename = pathField.getText();
        if (!filename.equals("")) {
            try {
                netSer.Out().writeUTF("remove");
                netSer.Out().writeUTF(filename);
                netSer.Out().flush();
                System.out.println(netSer.In().readUTF());
            } catch (FileNotFoundException e) {
                System.out.println(" FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateServerList();
            updateClientList();
        }
    }

    public void btDounload(ActionEvent actionEvent) {
        try {
            String filename = pathField.getText();
            netSer.Out().writeUTF("download");
            netSer.Out().writeUTF(filename);
            netSer.Out().flush();
            if ("x00DF".equals( netSer.In().readUTF())) {
                pathField.setText("the file is missing");
            }
            else{
                File file = new File("DirClient" + File.separator + filename);
                if (!file.exists()) {
                    file.createNewFile();
                }
                long length = netSer.In().readLong();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[256];
                for (int i = 0; i < (length + 255) / 256; i++) {
                    int read = netSer.In().read(buffer);
                    fos.write(buffer, 0, read);
                }
                fos.flush();

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("SOME ERROR");
        }
        updateClientList();

    }

    public void exitFromClient(ActionEvent actionEvent) {
        Platform.exit();
    }
    public void btPathUp(ActionEvent actionEvent) {

    }
}
