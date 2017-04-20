package cn.sk.skhstablet.model;
import java.io.Serializable;

/**
 * Created by ldkobe on 2017/4/16.
 */

public class Patient implements Comparable<Patient>, Serializable{
    private String name;
    private String rfid;
    private String idcard;
    private String gender;
    public Patient(String name, String gender,String rfid,String idcard)
    {
        this.name=name;
        this.gender=gender;
        this.rfid=rfid;
        this.idcard=idcard;
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
}
