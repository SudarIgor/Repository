package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Registration {
    AuthServiceImpl authService;
    public Registration(String login, String pass) {
        authService = new AuthServiceImpl();
        authService.addUser(login ,pass);
        String clientDir= null;
        try {
            clientDir = "serverDir" + File.separator + "client "
                    .concat( authService.getUserDao().getUser_id(login));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        File theDir = new File(clientDir);
        if (!theDir.exists()){
            theDir.mkdirs();
        }



    }



}
