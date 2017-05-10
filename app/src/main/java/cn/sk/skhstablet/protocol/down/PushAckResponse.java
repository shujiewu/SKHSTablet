package cn.sk.skhstablet.protocol.down;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class PushAckResponse  extends AbstractProtocol {
    public PushAckResponse(byte command) {
        super(command);
    }

    private byte deviceType;
    private int userID;
    private byte requestID;
    private byte state;

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return state;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
