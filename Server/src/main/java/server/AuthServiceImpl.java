package server;

import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    private static AuthServiceImpl sample;
    private AuthServiceHandler userDao;

    public AuthServiceImpl() throws SQLException, ClassNotFoundException {
       userDao = new AuthServiceHandler();
    }

    public static AuthServiceImpl getSample()  {
        if (sample == null){
            try {
                sample = new AuthServiceImpl();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sample;
    }

    public AuthServiceHandler getUserDao() {
        return userDao;
    }

    @Override
    public void addUser(String login, String pass) {
        try {
            userDao.updateUser(new User(login, pass));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean auth(String login, String pass) {
        System.out.println(login + " " + pass);
        try {
            if(userDao.userExists(login) && pass.equals(userDao.getUserByLogin(login).getPassword()) ){
             return true;
            }
        } catch (SQLException throwables) {
            return false;
        }

        return false;

    }
}
