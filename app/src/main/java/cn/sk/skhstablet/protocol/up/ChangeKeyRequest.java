package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class ChangeKeyRequest extends AbstractProtocol {
    public ChangeKeyRequest(byte command) {
        super(command);
    }

    private int userID;
    private byte[] userOldKey;
    private byte[] userNewKey;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public byte[] getUserNewKey() {
        return userNewKey;
    }

    public byte[] getUserOldKey() {
        return userOldKey;
    }

    public void setUserNewKey(byte[] userNewKey) {
        this.userNewKey = userNewKey;
    }

    public void setUserOldKey(byte[] userOldKey) {
        this.userOldKey = userOldKey;
    }
}
