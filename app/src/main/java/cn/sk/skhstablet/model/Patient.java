package cn.sk.skhstablet.model;
import java.io.Serializable;

import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_FALSE;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_MONITOR;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_TRUE;

/**
 * Created by ldkobe on 2017/4/16.
 */

public class Patient implements Comparable<Patient>, Serializable{

    private String rfid;
    private String idcard;

    private String selectStatus;



    public String getSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(String selectStatus) {
        this.selectStatus=selectStatus;
    }

    public Patient(String name, String gender, String rfid, String selectStatus)
    {
        this.name=name;
        this.gender=gender;
        this.rfid=rfid;
        this.selectStatus=selectStatus;
    }
    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getIdcard() {
        return idcard;
    }

    public String getRfid() {
        return rfid;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
    @Override
    public int compareTo(Patient patient) {
        return name.compareTo(patient.name);
    }


    private String name;
    private String gender;
    private String hospitalNumber;
    private int patientID;
    private byte devType;
    private long deviceNumber;
    private byte sportState;
    private byte connectState;
    private byte monConnectState;
    public Patient(){}

    public byte getConnectState() {
        return connectState;
    }

    public byte getDevType() {
        return devType;
    }

    public byte getMonConnectState() {
        return monConnectState;
    }

    public byte getSportState() {
        return sportState;
    }

    public long getDeviceNumber() {
        return deviceNumber;
    }

    public String getHospitalNumber() {
        return hospitalNumber;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setConnectState(byte connectState) {
        this.connectState = connectState;
    }

    public void setDeviceNumber(long deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public void setDevType(byte devType) {
        this.devType = devType;
    }

    public void setHospitalNumber(String hospitalNumber) {
        this.hospitalNumber = hospitalNumber;
    }

    public void setMonConnectState(byte monConnectState) {
        this.monConnectState = monConnectState;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public void setSportState(byte sportState) {
        this.sportState = sportState;
    }
}
