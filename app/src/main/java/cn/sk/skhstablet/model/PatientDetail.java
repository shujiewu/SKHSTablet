package cn.sk.skhstablet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldkobe on 2017/4/18.
 * 此类在患者基本信息的基础上包含了运动数据和设备数据，用于多患者监控和单患者监控界面数据显示
 */

public class PatientDetail {
    private String name;
    private String id;
    private String dev;
    private String percent;
    private List<String> sportDevName=new ArrayList<>();  //指的是参数名称，而不是设备名称
    private List<String> phyDevName=new ArrayList<>();;
    private List<String> sportDevValue=new ArrayList<>();;
    private List<String> phyDevValue=new ArrayList<>();;
    private String gender;
    public PatientDetail(String name, String id,String dev,String percent,List<String> phyDevName,List<String> phyDevValue,List<String> sportDevName,List<String> sportDevValue )
    {
        this.name=name;
        this.id=id;
        this.dev=dev;
        this.percent=percent;
        this.phyDevName=phyDevName;
        this.phyDevValue=phyDevValue;
        this.sportDevName=sportDevName;
        this.sportDevValue=sportDevValue;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhyDevName() {
        return phyDevName;
    }

    public List<String> getPhyDevValue() {
        return phyDevValue;
    }

    public List<String> getSportDevName() {
        return sportDevName;
    }

    public List<String> getSportDevValue() {
        return sportDevValue;
    }

    public void setPhyDevName(List<String> phyDevName) {
        this.phyDevName = phyDevName;
    }

    public void setPhyDevValue(List<String> phyDevValue) {
        this.phyDevValue = phyDevValue;
    }

    public void setSportDevName(List<String> sportDevName) {
        this.sportDevName = sportDevName;
    }

    public void setSportDevValue(List<String> sportDevValue) {
        this.sportDevValue = sportDevValue;
    }

    public String getDev() {
        return dev;
    }

    public String getId() {
        return id;
    }

    public String getPercent() {
        return percent;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public PatientDetail(){}
    private String hospitalNumber;
    private int patientID;
    private byte devType;
    private String deviceNumber;
    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
    public int getPatientID() {
        return patientID;
    }
    public void setDevType(byte devType) {
        this.devType = devType;
    }
    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
    public void setHospitalNumber(String hospitalNumber) {
        this.hospitalNumber = hospitalNumber;
    }
    public String getDeviceNumber() {
        return deviceNumber;
    }
    public String getHospitalNumber() {
        return hospitalNumber;
    }
    public byte getDevType() {
        return devType;
    }

    private int exercisePlanId;
    private int exercisePlanSectionNumber;

    public int getExercisePlanId() {
        return exercisePlanId;
    }

    public int getExercisePlanSectionNumber() {
        return exercisePlanSectionNumber;
    }

    public void setExercisePlanId(int exercisePlanId) {
        this.exercisePlanId = exercisePlanId;
    }

    public void setExercisePlanSectionNumber(int exercisePlanSectionNumber) {
        this.exercisePlanSectionNumber = exercisePlanSectionNumber;
    }
}
