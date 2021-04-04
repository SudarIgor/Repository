package testClient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChangePass {

    public ChangePass() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("changePass.fxml"));
            Parent root3 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root3));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
