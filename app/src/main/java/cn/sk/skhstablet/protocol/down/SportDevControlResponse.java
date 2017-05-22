package cn.sk.skhstablet.protocol.down;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/22.
 */

public class SportDevControlResponse extends AbstractProtocol {
    public SportDevControlResponse(byte command) {
        super(command);
    }
    private byte state;
    String deviceID;
    private byte parameterCode;
    private byte paraType;
    private byte controlResultCode;

    public void setParaType(byte paraType) {
        this.paraType = paraType;
    }

    public void setParameterCode(byte parameterCode) {
        this.parameterCode = parameterCode;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public byte getControlResultCode() {
        return controlResultCode;
    }

    public byte getParameterCode() {
        return parameterCode;
    }

    public byte getParaType() {
        return paraType;
    }

    public byte getState() {
        return state;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setControlResultCode(byte controlResultCode) {
        this.controlResultCode = controlResultCode;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
