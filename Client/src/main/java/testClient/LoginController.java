package testClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private NetSer netSer;
    private ClientPath path;

    @FXML
    PasswordField passwordPF;
    @FXML
    public TextField loginTF;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        path = new ClientPath();


        try {
            netSer = new NetSer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btLogin(ActionEvent actionEvent) {
        try {
            netSer.Out().writeUTF("auth");
            netSer.Out().writeUTF(loginTF.getText());
            netSer.Out().writeUTF(passwordPF.getText());
            String ans = netSer.In().readUTF();
            if(ans.equals("login")){

                path.setPath(netSer.In().readUTF());
                System.out.println(path.getPath());
                System.out.println("in login");
                new Client();
                loginTF.getScene().getWindow().hide();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void btReg(ActionEvent actionEvent) {
        new Regs();
    }
}
