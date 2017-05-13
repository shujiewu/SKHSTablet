package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class PatientListRequest extends AbstractProtocol {
    public PatientListRequest(byte command) {
        super(command);
    }

    private byte deviceType;
    private int userID;
    private byte requestID;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public byte getRequestID() {
        return requestID;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public void setRequestID(byte requestID) {
        this.requestID = requestID;
    }
}
