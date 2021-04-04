package testClient;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.io.*;
import java.net.URL;
import java.util.*;


public class ClientController implements Initializable {

    private NetSer netSer;
    private String pathDirClientOnServer;
    private ListView <String> clientListView;
    List<String> list;
    private static String pathClient;
    private String pathServer;
    private String pathRootServer;
    private String sizeServerRep;
    private long sizeFile;

    @FXML
    Pane serverPane, clientPane;

    @FXML
    TextField pathFieldServer, pathFieldClient;

    @FXML
    TextField pathField;

    @FXML
    Label serverSize;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            netSer = new NetSer();
            //получаем директорию после логирования
            pathDirClientOnServer = ClientPath.getPath();
            netSer.Out().writeUTF("ServDir");
            netSer.Out().writeUTF(pathDirClientOnServer);
            pathServer = pathDirClientOnServer;
            pathRootServer = pathDirClientOnServer.substring(0, pathDirClientOnServer.length()-10);
            pathClient = "ClientRoot";

            getSizeServerRep();
            updateClientList();
            System.out.println("updateClientList()");
            updateServerList();
            System.out.println("updateServerList()");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSizeServerRep() {
        //получение размера используемого пространства в репозитории
        try {
            netSer.Out().writeUTF("getSizeRep");
            sizeServerRep = netSer.In().readUTF();
            serverSize.setText(sizeServerRep);
        } catch (IOException e) {
        e.printStackTrace();
    }
    }

    private void updateClientList() {
        list = new ArrayList<>();
        File file = new File(pathClient);
        String[] files = file.list();
        File file1;
        for (String fileName : files) {
            file1 = new File(pathClient+File.separator+fileName);
            if(file1.isDirectory()) list.add("Directory: " + fileName);
            else list.add("File: " + fileName);
        }

        Collections.sort(list);
        ObservableList<String> langs = FXCollections.observableArrayList(list);
        ListView<String> langsListView = new ListView<String>(langs);
        clientPane.getChildren().addAll(langsListView);
        clientListView = new ListView<String>(langs);

        MultipleSelectionModel<String> langsSelectionModel = langsListView.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        langsSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue){
                String selectedItems = "";
                ObservableList<String> selected = langsSelectionModel.getSelectedItems();
                for (String item : selected){
                    selectedItems += item;
                }
                if(selectedItems.startsWith("File: ")) pathField.setText(selectedItems.split("File: ")[1]);
                else {
                   pathClient += File.separator.concat(selectedItems.split("Directory: ")[1]);
                   updateClientList();
               }
            }
        });

        if (file.getParent()== null) {
            pathFieldClient.setText("ClientRoot");
        }else pathFieldClient.setText(file.getPath());
    }

    private void updateServerList() {
        List<String> list = downloadFileList();
        ObservableList<String> langs = FXCollections.observableArrayList(list);
        ListView<String> langsListView = new ListView<String>(langs);


        MultipleSelectionModel<String> langsSelectionModel = langsListView.getSelectionModel();
        langsSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        langsSelectionModel.selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue){
                String selectedItems = "";
                ObservableList<String> selected = langsSelectionModel.getSelectedItems();
                for (String item : selected){
                    selectedItems += item;
                }
                if(selectedItems.startsWith("File: ")) pathField.setText(selectedItems.split("File: ")[1]);
                else if (selectedItems.startsWith("Directory: ")) {
                    pathServer+=File.separator + selectedItems.split("Directory: ")[1];
                    System.out.println("new Dir " + pathServer);
                    try {
                        netSer.Out().writeUTF("new Dir "+selectedItems.split("Directory: ")[1]);
                        updateServerList();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else return;
            }
        });


        pathFieldServer.setText(pathServer.replace(pathRootServer,""));

        serverPane.getChildren().addAll(langsListView);
        updateClientList();
    }

    private List<String> downloadFileList() {
        List<String> list = new ArrayList<>();
        try {
            StringBuilder sb = new StringBuilder();
            netSer.Out().writeUTF("list-files");

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
            Collections.sort(list);
        }catch (FileNotFoundException e){
            System.out.println(" FileNotFoundException");
        }catch (IOException e) {
            e.printStackTrace();
    }

    return list;
}

    private long getFileSize(){
        String filename = pathField.getText();
        File file = new File(pathClient + File.separator + filename);
        sizeFile =file.length();
        System.out.println("out size file: " + sizeFile );
        return sizeFile;
    }

    public void btUpload(ActionEvent actionEvent) {
        String filename = pathField.getText();
        if(!filename.equals("")) {
            try {
                 File file = new File(pathClient + File.separator + filename);
                 if (file.exists()) {
                     netSer.Out().writeUTF("upload");
                     boolean flag;
                     // отправляем на сервер размер файла
                     netSer.Out().writeLong(getFileSize());
                     System.out.println("out size file on server");
                     flag = netSer.In().readBoolean();
                     System.out.println("получили ответ по флагу ");
                     // если в репозитории достаточно места передаем файл
                     if (flag) {
                            System.out.println(flag);
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
                            getSizeServerRep();
                    }else {
                         pathField.clear();
                         pathField.setText("Репозиторий переполнен");
                     }
                 }else {
                      pathField.setText("File is not exists");
                 }

            } catch (FileNotFoundException e) {
                System.out.println("FileNotFoundException");
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
            getSizeServerRep();
            updateServerList();
            updateClientList();
        }
    }

    public void btDownload(ActionEvent actionEvent) {
        try {
            String filename = pathField.getText();
            netSer.Out().writeUTF("download");
            netSer.Out().writeUTF(filename);
            netSer.Out().flush();
            if ("x00DF".equals( netSer.In().readUTF())) {
                pathField.setText("the file is missing");
            }
            else{
                File file = new File(pathClient + File.separator + filename);
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

    public void btPathUpServer(ActionEvent actionEvent) {
        try {
            System.out.println("in SbtPathUpServer");
            netSer.Out().writeUTF("getParentDir");
            pathServer = netSer.In().readUTF();
            System.out.println("new dir " + pathServer);
            pathFieldServer.setText(pathServer.replace(pathRootServer,""));
            updateServerList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btPathUpClient(ActionEvent actionEvent) {
        File file = new File(pathClient);
        if(!(file.getParent() == null)) {pathClient= file.getParent();
        updateClientList();
        }
    }

//    выходим из клиентского окна и открываем окно логина
    public void ReLogin(ActionEvent actionEvent) {
        new Login();
        try {
            netSer.Out().writeUTF("close");
            netSer.close();
        } catch (IOException e) {
        e.printStackTrace();
    }
        pathField.getScene().getWindow().hide();
    }

    public void changePassword(ActionEvent actionEvent) {

        new ChangePass();
    }
}
