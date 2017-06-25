package cn.sk.skhstablet.protocol.down;

import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.ExercisePlan;

/**
 * Created by ldkobe on 2017/6/22.
 */

public class ExercisePlanResponse extends AbstractProtocol {

    public ExercisePlanResponse(byte command) {
        super(command);
    }
    private byte state;
    private int patientID;
    private int patientNumber;
    private List<ExercisePlan> exercisePlanList;

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public byte getState() {
        return state;
    }

    public int getPatientNumber() {
        return patientNumber;
    }

    public List<ExercisePlan> getExercisePlanList() {
        return exercisePlanList;
    }

    public void setExercisePlanList(List<ExercisePlan> exercisePlanList) {
        this.exercisePlanList = exercisePlanList;
    }

    public void setPatientNumber(int patientNumber) {
        this.patientNumber = patientNumber;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
