package testClient;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login {

    public Login() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
