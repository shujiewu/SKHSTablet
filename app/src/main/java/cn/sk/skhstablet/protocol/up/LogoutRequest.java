package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LogoutRequest extends AbstractProtocol {
    protected LogoutRequest(byte command) {
        super(command);
    }
    private int userID;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }
}
