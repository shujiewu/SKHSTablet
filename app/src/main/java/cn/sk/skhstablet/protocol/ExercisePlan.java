package cn.sk.skhstablet.protocol;

import java.util.List;

/**
 * Created by ldkobe on 2017/6/22.
 */

public class ExercisePlan {
    private int exercisePlanID;
    private String content;
    private List<String> segment;

    public void setContent(String content) {
        this.content = content;
    }

    public int getExercisePlanID() {
        return exercisePlanID;
    }

    public List<String> getSegment() {
        return segment;
    }

    public String getContent() {
        return content;
    }

    public void setExercisePlanID(int exercisePlanID) {
        this.exercisePlanID = exercisePlanID;
    }

    public void setSegment(List<String> segment) {
        this.segment = segment;
    }
}
