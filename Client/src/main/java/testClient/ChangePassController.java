package testClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangePassController implements Initializable, Closeable {

    private int user_id;
//    private String stUser_id;
    NetSer netSer;
    @FXML
    PasswordField oldPassword, newPassword, newPasswordRep;
    @FXML
    Label infoLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        netSer = new NetSer();

        user_id = Integer.parseInt(ClientPath.getPath().split("\\\\")[1].split(" ")[1]);
//        stUser_id=ClientPath.getPath().split("\\\\")[1].split(" ")[1];
        System.out.println(user_id);
    }


    public void changePass(ActionEvent actionEvent) {
        System.out.println(user_id + "  " + oldPassword.getText() + "  " +  newPassword.getText());
        if(comparePassword()){
            try {
                boolean flag;
                infoLabel.setText("");
                netSer.Out().writeUTF("changePass");
                System.out.println("отправляем id");
                netSer.Out().writeInt(user_id);
                netSer.Out().writeUTF(oldPassword.getText());
                flag = netSer.In().readBoolean();
                if(!flag){
                    infoLabel.setText("invalid current password");
                }else {
                    netSer.Out().writeUTF(newPassword.getText());
                    infoLabel.setText("password changed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            clearPasswordFields();
            infoLabel.setText("Error: different passwords");
        }


    }

    private boolean comparePassword() {
        if(newPassword.getText().equals(newPasswordRep.getText())) return true;
        return false;
    }
    private void clearPasswordFields(){
        oldPassword.clear();
        newPassword.clear();
        newPasswordRep.clear();
    }

    @Override
    public void close() throws IOException {
        netSer.Out().writeUTF("close");
        netSer.close();
    }
}
