package testClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, Closeable {

    private NetSer netSer;
    private ClientPath path;

    @FXML
    PasswordField passwordPF;
    @FXML
    public TextField loginTF;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        path = new ClientPath();
        netSer = new NetSer();
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
                netSer.Out().writeUTF("close");
                close();
                loginTF.getScene().getWindow().hide();
            }
            else {
                loginTF.clear();
                passwordPF.clear();
                loginTF.setText("logging error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void btReg(ActionEvent actionEvent) {
        new Regs();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Окно логина было закрыто...");
        netSer.close();

    }
}
