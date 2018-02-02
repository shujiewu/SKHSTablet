package cn.sk.skhstablet.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sk.skhstablet.domain.ECG;

/**
 * Created by ldkobe on 2017/5/31.
 * 生理数据，用于单一患者监控界面数据显示，包含了生理数据和心电数据
 */

public class PatientPhyData {
    private List<String> phyDevName;
    private List<String> phyDevValue;
    private Map<Short, List<Short>> ecgs = new HashMap<>();
    private String id;
    private int patientID;

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public List<String> getPhyDevName() {
        return phyDevName;
    }

    public List<String> getPhyDevValue() {
        return phyDevValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhyDevName(List<String> phyDevName) {
        this.phyDevName = phyDevName;
    }

    public void setPhyDevValue(List<String> phyDevValue) {
        this.phyDevValue = phyDevValue;
    }

    public Map<Short, List<Short>> getEcgs() {
        return ecgs;
    }

    public void setEcgs(Map<Short, List<Short>> ecgs) {
        this.ecgs = ecgs;
    }
}
