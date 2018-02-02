package cn.sk.skhstablet.protocol.up;

import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class SingleMonitorRequest extends AbstractProtocol {
    public SingleMonitorRequest(byte command) {
        super(command);
    }
    private byte deviceType;
    private int userID;
    private byte requestID;
    private short patientNumber;


    private List<Integer> patientID;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setPatientNumber(short patientNumber) {
        this.patientNumber = patientNumber;
    }

    public void setPatientID(List<Integer> patientID) {
        this.patientID = patientID;
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public short getPatientNumber() {
        return patientNumber;
    }

    public byte getRequestID() {
        return requestID;
    }

    public List<Integer> getPatientID() {
        return patientID;
    }

    public int getUserID() {
        return userID;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public void setRequestID(byte requestID) {
        this.requestID = requestID;
    }
}
