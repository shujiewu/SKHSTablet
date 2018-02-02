package cn.sk.skhstablet.protocol.down;

import java.util.ArrayList;
import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by ldkobe on 2017/9/24.
 */

public class SignOutResponse extends AbstractProtocol {
    public SignOutResponse(byte command) {
        super(command);
    }
    private List<Integer> patientNumber=new ArrayList<>();

    public List<Integer> getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(List<Integer> patientNumber) {
        this.patientNumber = patientNumber;
    }
}
