package cn.sk.skhstablet.protocol.down;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LoginAckResponse extends AbstractProtocol {
    public LoginAckResponse(byte command) {
        super(command);
    }

    //private int userID;
    private byte state;

   /* public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
       return userID;
    }*/

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    private byte userNameLength;
    private String userName;

    public byte getUserNameLength() {
        return userNameLength;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserNameLength(byte userNameLength) {
        this.userNameLength = userNameLength;
    }
}
