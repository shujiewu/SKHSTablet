package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LoginRequest extends AbstractProtocol {
    public LoginRequest(byte command) {
        super(command);
    }

   /* private int userID;
    private byte[] userKey;

    public byte[] getUserKey() {
        return userKey;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserKey(byte[] userKey) {
        this.userKey = userKey;
    }
    */

    private byte loginNameLength;
    private String loginName;
    private byte loginKeyLength;
    private String loginKey;

    public byte getLoginKeyLength() {
        return loginKeyLength;
    }

    public byte getLoginNameLength() {
        return loginNameLength;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public void setLoginKeyLength(byte loginKeyLength) {
        this.loginKeyLength = loginKeyLength;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setLoginNameLength(byte loginNameLength) {
        this.loginNameLength = loginNameLength;
    }
}
