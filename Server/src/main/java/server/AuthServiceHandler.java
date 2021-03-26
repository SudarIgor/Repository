package server;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

public class AuthServiceHandler implements Closeable {

    private Connection connection;
    private Statement statement;


    private static final String AUTH_TABLE =

                "create table  if not exists USERS\n" +
                "(\n" +
                    "USER_ID INTEGER  primary key autoincrement,\n" +
                    "LOGIN TEXT not null,\n" +
                    "PASSWORD TEXT not null,\n" +
                    "NICK TEXT default 'Name' not null\n" +
                ");";

    public AuthServiceHandler() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:UserAuthDB");
        statement = connection.createStatement();
        statement.execute(AUTH_TABLE);
    }


    public boolean userExists(String login) throws SQLException {
        String sql = String.format("SELECT LOGIN FROM USERS WHERE LOGIN = '%s'", login);
        ResultSet rs = statement.executeQuery(sql);
        return rs.next();
    }

    public boolean userExists(int id) throws SQLException {
        String sql = String.format("SELECT ID FROM USERS WHERE USER_ID = %d", id);
        ResultSet rs = statement.executeQuery(sql);
        return rs.next();
    }


    public User getUserByLogin(String login) throws SQLException {
        String sql = String.format("SELECT * FROM USERS WHERE LOGIN = '%s'", login);
        User user = new User(null,null);

        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            user.setUser_id(rs.getInt("USER_ID"));
            user.setLogin(rs.getString("LOGIN"));
            user.setPassword(rs.getString("PASSWORD"));
            user.setNick(rs.getString("NICK"));
        }
        return user;
    }

    public User getUserByUser_id (Integer id) throws SQLException {
        String sql = String.format("SELECT * FROM USERS WHERE USER_ID = %d", id);
        User user = new User(null,null);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            user.setUser_id(rs.getInt("USER_ID"));
            user.setLogin(rs.getString("LOGIN"));
            user.setPassword(rs.getString("PASSWORD"));
            user.setNick(rs.getString("NICK"));
        }
        return user;
    }

    public void updateUser(User user) throws SQLException {
        String sql;
        if ( userExists(user.getLogin())){
            sql = String.format("UPDATE USERS SET PASSWORD = '%s',  NICK = '%s' WHERE LOGIN = '%s'; ",
                    user.getPassword(),
                    user.getNick(),
                    user.getLogin());

            System.out.println("Update user: " + user);
        }else {
            sql = String.format("INSERT INTO USERS (LOGIN, PASSWORD, NICK) " +
                            "VALUES ('%s', '%s', '%s');",
                            user.getLogin(),
                            user.getPassword(),
                            user.getNick());
            System.out.println("Input User: " + user);

        }
        statement.execute(sql);
    }

    public String getUser_id(String login) throws SQLException {

        String sql = String.format("SELECT USER_ID FROM USERS WHERE LOGIN = '%s'", login);
       int id=0;
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
          id = rs.getInt("USER_ID");

        }

        return String.valueOf(id);

//
    }


    public String getUserNick(User user) throws SQLException {
        String sql = String.format("SELECT NICK FROM USERS WHERE LOGIN = '%s'", user.getLogin());
        String nick = null;
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
          nick = rs.getString("NICK");
        }

        return nick;
    }

    public String getUserPassword (User user) throws SQLException {
        String sql = String.format("SELECT PASSWORD FROM USERS WHERE LOGIN = '%s'", user.getLogin());
        String pass = null;
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()){
            pass = rs.getString("PASSWORD");
        }

        return pass;
    }


    public void changeUserNick(User user, String nick) throws SQLException {
        String sql;

            sql = String.format("UPDATE USERS SET NICK = '%s' WHERE LOGIN = '%s'; ",
                   nick,
                   user.getLogin());

        statement.execute(sql);
    }

    public void changeUserPassword(User user, String password) throws SQLException {
        String sql;

        sql = String.format("UPDATE USERS SET PASSWORD = '%s' WHERE LOGIN = '%s'; ",
                password,
                user.getLogin());
        statement.execute(sql);
    }


    @Override
    public void close() throws IOException {
        try {
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
