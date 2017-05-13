package cn.sk.skhstablet.protocol.up;

import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class MutiMonitorRequest extends AbstractProtocol {
    public MutiMonitorRequest(byte command) {
        super(command);
    }

    private byte deviceType;
    private int userID;
    private byte requestID;
    private short patientNumber;
    private List<Integer> patientID;

    public void setRequestID(byte requestID) {
        this.requestID = requestID;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getRequestID() {
        return requestID;
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public int getUserID() {
        return userID;
    }

    public List<Integer> getPatientID() {
        return patientID;
    }

    public short getPatientNumber() {
        return patientNumber;
    }

    public void setPatientID(List<Integer> patientID) {
        this.patientID = patientID;
    }

    public void setPatientNumber(short patientNumber) {
        this.patientNumber = patientNumber;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
