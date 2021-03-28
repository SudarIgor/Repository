package testClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegsControler implements Initializable, Closeable {

    private NetSer netSer;

    @FXML
    TextField loginRegTF;
    @FXML
    PasswordField passworRegdPF;
    @FXML
    PasswordField passwordRepeatPF;
    @FXML
    Button registration;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        netSer = new NetSer();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Окно регистрации было закрыто...");
        netSer.close();
    }


    public void registration(ActionEvent actionEvent) {
        boolean checkLog;
        boolean checkPass;
        checkLog = checkLogin(loginRegTF.getText());
        checkPass= checkPassword(passworRegdPF.getText(), passwordRepeatPF.getText());

        if (checkPass && checkLog){
            try {
                System.out.println("Проверено: логин свободен регистрируемся");
                System.out.println("отправляю команду - регистрация");
                netSer.Out().writeUTF("registration");
                System.out.println("отправляю логин");
                netSer.Out().writeUTF(loginRegTF.getText());
                System.out.println("отправляю пароль");
                netSer.Out().writeUTF(passworRegdPF.getText());
                System.out.println("отправил пароль");

            } catch (IOException e) {
            e.printStackTrace();
            }
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loginRegTF.getScene().getWindow().hide();

        }
    }

    private boolean checkLogin(String login) {
        try {
            boolean flag;
            netSer.Out().writeUTF("checkLogin");
            netSer.Out().writeUTF(login);
            flag = netSer.In().readBoolean();
            if(flag){
                loginRegTF.clear();
                passworRegdPF.clear();
                passwordRepeatPF.clear();
                loginRegTF.setText("login is busy");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("логин свободен");
        return true;

    }

    private boolean checkPassword(String pass1, String pass2) {
            if(!pass1.equals(pass2)){
            loginRegTF.clear();
            loginRegTF.setText("password entry error");
            passwordRepeatPF.clear();
            passworRegdPF.clear();
            return false;
        }
        return true;
    }


}
