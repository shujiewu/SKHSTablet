package cn.sk.skhstablet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class PatientDetailList {
    public final static List<PatientDetail> PATIENTS = new ArrayList<PatientDetail>(){{
        add(new PatientDetail("张三", "1", "跑步机","10%"));
        add(new PatientDetail("李四", "2",  "跑步机","10%"));
        add(new PatientDetail("王五", "3",  "跑步机","10%"));
        add(new PatientDetail("赵六", "4", "跑步机","10%"));
        add(new PatientDetail("赵六", "5", "跑步机","10%"));
        add(new PatientDetail("三个字", "6",  "跑步机","10%"));
        add(new PatientDetail("四个字", "7",  "跑步机","10%"));
    }};
}
