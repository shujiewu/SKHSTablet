package cn.sk.skhstablet.model;

import java.util.List;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class PatientDetail {
    private String name;
    private String id;
    private String dev;
    private String percent;
    private List<String> sportDev;
    private List<String> phyDev;
    private String gender;
    public PatientDetail(String name, String id,String dev,String percent)
    {
        this.name=name;
        this.id=id;
        this.dev=dev;
        this.percent=percent;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhyDev() {
        return phyDev;
    }

    public List<String> getSportDev() {
        return sportDev;
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

    public void setPhyDev(List<String> phyDev) {
        this.phyDev = phyDev;
    }

    public void setSportDev(List<String> sportDev) {
        this.sportDev = sportDev;
    }
}
