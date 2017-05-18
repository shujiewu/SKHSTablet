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

    private byte state;

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return state;
    }

    private short devNumber;
    private HashMap<Byte,List<MonitorDevForm>> devData;

    public void setDevNumber(short devNumber) {
        this.devNumber = devNumber;
    }


    public short getDevNumber() {
        return devNumber;
    }

    public HashMap<Byte,List<MonitorDevForm>> getDevData() {
        return devData;
    }

    public void setDevData(HashMap<Byte,List<MonitorDevForm>> devData) {
        this.devData = devData;
    }
}
