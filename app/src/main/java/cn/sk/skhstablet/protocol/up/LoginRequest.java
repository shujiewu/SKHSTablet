package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LoginRequest extends AbstractProtocol {
    protected LoginRequest(byte command) {
        super(command);
    }

    private int userID;
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
}
