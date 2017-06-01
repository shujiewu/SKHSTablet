package cn.sk.skhstablet.model;

import java.util.List;

/**
 * Created by ldkobe on 2017/5/31.
 */

public class PatientPhyData {
    private List<String> phyDevName;
    private List<String> phyDevValue;
    private String id;

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
}
