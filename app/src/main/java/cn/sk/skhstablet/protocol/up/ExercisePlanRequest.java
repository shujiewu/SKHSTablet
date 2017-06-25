package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by ldkobe on 2017/6/22.
 */

public class ExercisePlanRequest extends AbstractProtocol {

    public ExercisePlanRequest(byte command) {
        super(command);
    }
    private int patientID;

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
}
