package server;

public interface AuthService {

    public void addUser(String name, String pass);

    boolean auth(String name, String pass);
}
