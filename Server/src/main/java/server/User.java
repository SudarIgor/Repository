package server;

public class User {
    private Integer user_id;
    private String login;
    private String nick;
    private String password;

    public User(String login, String password) {
        this.login = login;
        nick = login;
        this.password = password;
    }


    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer id) {
        this.user_id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + user_id +
                ", login='" + login + '\'' +
                ", name='" + nick + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
