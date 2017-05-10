package cn.sk.skhstablet.protocol.down;

import java.util.HashMap;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class DevNameResponse extends AbstractProtocol {
    public DevNameResponse(byte command) {
        super(command);
    }

    private int userID;
    private byte devNumber;
    private HashMap<Byte,String> devName;

    public byte getDevNumber() {
        return devNumber;
    }

    public HashMap<Byte, String> getDevName() {
        return devName;
    }

    public int getUserID() {
        return userID;
    }

    public void setDevName(HashMap<Byte, String> devName) {
        this.devName = devName;
    }

    public void setDevNumber(byte devNumber) {
        this.devNumber = devNumber;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
