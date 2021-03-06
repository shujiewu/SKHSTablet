package cn.sk.skhstablet.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static cn.sk.skhstablet.app.AppConstants.DEV_NAME;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_FALSE;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_MONITOR;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_TRUE;

/**
 * Created by ldkobe on 2017/4/16.
 * Model类，此类用于左侧全局患者监控列表的数据显示并且根据患者运动状态更新单人监控和多人监控界面
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
    private String deviceNumber;
    private byte sportState;
    private byte connectState;
    private byte monConnectState;
    private byte phyConnectState;
    private String dev;
    private int sportPlanID;
    private byte sportPlanSegment;

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

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

    public String getDeviceNumber() {
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

    public void setDeviceNumber(String deviceNumber) {
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

    public byte getPhyConnectState() {
        return phyConnectState;
    }
    public void setPhyConnectState(byte phyConnectState) {
        this.phyConnectState = phyConnectState;
    }

    public byte getSportPlanSegment() {
        return sportPlanSegment;
    }

    public int getSportPlanID() {
        return sportPlanID;
    }

    public void setSportPlanID(int sportPlanID) {
        this.sportPlanID = sportPlanID;
    }

    public void setSportPlanSegment(byte sportPlanSegment) {
        this.sportPlanSegment = sportPlanSegment;
    }

    public Patient(int patientID,String name, String gender, String hospitalNumber, byte phyConnectState,byte monConnectState,byte sportState,String deviceNumber,byte devType,byte connectState,String selectStatus)
    {
        this.patientID=patientID;
        this.name=name;
        this.gender=gender;
        this.hospitalNumber=hospitalNumber;
        this.phyConnectState=phyConnectState;
        this.monConnectState=monConnectState;
        this.sportState=sportState;
        this.deviceNumber=deviceNumber;
        this.devType=devType;
        this.dev=DEV_NAME.get(devType);
        this.connectState=connectState;
        this.selectStatus=selectStatus;
    }


    List<Byte> MonExceptionState=new ArrayList<>();
    public void setMonExceptionState(List<Byte> monExceptionState) {
        MonExceptionState = monExceptionState;
    }

    public List<Byte> getMonExceptionState() {
        return MonExceptionState;
    }
    List<Byte> ParaExceptionState=new ArrayList<>();

    public void setParaExceptionState(List<Byte> paraExceptionState) {
        ParaExceptionState = paraExceptionState;
    }

    public List<Byte> getParaExceptionState() {
        return ParaExceptionState;
    }
}
