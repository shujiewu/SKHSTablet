package cn.sk.skhstablet.model;

/**
 * Created by wyb on 2017/4/25.
 * 用户类，未用到，为了代码整洁可以使用此类
 */

public class User {
    private String account;
    private String password;
    public User(String account,String password)
    {
        this.account=account;
        this.password=password;
    }
    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
