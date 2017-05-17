package cn.sk.skhstablet.protocol.down;

import java.util.List;

import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class PatientListResponse extends AbstractProtocol {
    public PatientListResponse(byte command) {
        super(command);
    }
    //private byte deviceType;
    //private int userID;
    private int patientNumber;
    private List<Patient> patientList;

    /*public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }*/

    public List<Patient> getPatientList() {
        return patientList;
    }

    public int getPatientNumber() {
        return patientNumber;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    public void setPatientNumber(int patientNumber) {
        this.patientNumber = patientNumber;
    }

    private byte state;

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
