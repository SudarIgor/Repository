package server;

import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    private static AuthServiceImpl sample;
    private AuthServiceHandler user;

    public AuthServiceImpl() {
        user = new AuthServiceHandler();
    }

    public static AuthServiceImpl getSample()  {
        if (sample == null){
            sample = new AuthServiceImpl();
        }
        return sample;
    }

    public AuthServiceHandler getUser() {
        return user;
    }

    @Override
    public void addUser(String login, String pass) {
        try {
            user.updateUser(new User(login, pass));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean auth(String login, String pass) {
        System.out.println(login + " " + pass);
        try {
            if(user.userExists(login) && pass.equals(user.getUserByLogin(login).getPassword()) ){
             return true;
            }
        } catch (SQLException throwables) {
            return false;
        }

        return false;

    }
}
