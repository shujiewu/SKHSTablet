package cn.sk.skhstablet.protocol.down;

import java.util.HashMap;
import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.MonitorDevForm;

/**
 * Created by wyb on 2017/5/9.
 */

public class MonitorDevFormResponse extends AbstractProtocol {
    public MonitorDevFormResponse(byte command) {
        super(command);
    }
    private int userID;
    private byte devNumber;
    private HashMap<Byte,List<MonitorDevForm>> devData;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setDevNumber(byte devNumber) {
        this.devNumber = devNumber;
    }

    public int getUserID() {
        return userID;
    }

    public byte getDevNumber() {
        return devNumber;
    }

    public HashMap<Byte,List<MonitorDevForm>> getDevData() {
        return devData;
    }

    public void setDevData(HashMap<Byte,List<MonitorDevForm>> devData) {
        this.devData = devData;
    }
}
