package com.ya;

public class Courier {

    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public void forgetLogin() {
        this.login = null;
    }

    public void forgetPassword() {
        this.password = null;
    }

    public void forgetFirstName() {
        this.firstName = null;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

}
