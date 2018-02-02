package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/22.
 */

public class SportDevControlRequest extends AbstractProtocol {
    public SportDevControlRequest(byte command) {
        super(command);
    }
    String deviceID;
    private byte parameterCode;
    private byte paraType;
    private short paraControlValue;

    public short getParaControlValue() {
        return paraControlValue;
    }

    public byte getParaType() {
        return paraType;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public byte getParameterCode() {
        return parameterCode;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public void setParaControlValue(short paraControlValue) {
        this.paraControlValue = paraControlValue;
    }

    public void setParameterCode(byte parameterCode) {
        this.parameterCode = parameterCode;
    }

    public void setParaType(byte paraType) {
        this.paraType = paraType;
    }
}
