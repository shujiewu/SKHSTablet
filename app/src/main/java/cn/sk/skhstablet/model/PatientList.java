package cn.sk.skhstablet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldkobe on 2017/4/16.
 */

public class PatientList {
    public final static List<Patient> PATIENTS = new ArrayList<Patient>(){{
        add(new Patient("张三", "Muse", "142333199900001800","未选择"));
        add(new Patient("李四", "Hurts", "142333199900001800","未选择"));
        add(new Patient("王五", "The Kills", "142333199900001800","未选择"));
        add(new Patient("赵六", "Thousand Foot Krunch","142333199900001800","未选择"));
        add(new Patient("赵六", "Thousand Foot Krunch", "121","未选择"));
        add(new Patient("三个字", "Hurts", "121","未选择"));
        add(new Patient("四个字", "Korn", "121","未选择"));
    }};
}
