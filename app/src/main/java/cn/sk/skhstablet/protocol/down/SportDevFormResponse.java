package cn.sk.skhstablet.protocol.down;

import java.util.HashMap;
import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.SportDevForm;

/**
 * Created by wyb on 2017/5/9.
 */

public class SportDevFormResponse extends AbstractProtocol {
    public SportDevFormResponse(byte command) {
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
    private HashMap<Byte,List<SportDevForm>> devData;

    public void setDevData(HashMap<Byte, List<SportDevForm>> devData) {
        this.devData = devData;
    }

    public short getDevNumber() {
        return devNumber;
    }


    public void setDevNumber(short devNumber) {
        this.devNumber = devNumber;
    }

    public HashMap<Byte, List<SportDevForm>> getDevData() {
        return devData;
    }


}
