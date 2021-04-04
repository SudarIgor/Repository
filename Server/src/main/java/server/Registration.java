package server;

import java.io.File;
import java.sql.SQLException;

public class Registration {
    AuthServiceImpl authService;
    public Registration(String login, String pass) {
        authService = new AuthServiceImpl();
        authService.addUser(login ,pass);
        String clientDir= null;
        try {
            clientDir = "serverDir" + File.separator + "client "
                    .concat( authService.getUser().getUser_id(login)
                            .concat(File.separator + "ServerRoot"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        File theDir = new File(clientDir);
        if (!theDir.exists()){
            theDir.mkdirs();
        }



    }



}
