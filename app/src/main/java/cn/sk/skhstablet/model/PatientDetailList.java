package cn.sk.skhstablet.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class PatientDetailList {
    static List<String> phyName=new ArrayList<String>(Arrays.asList("HR","SpO2","NIBR","RESP","test1","test2","test3"));
    static List<String> phyValue=new ArrayList<String>(Arrays.asList("60","99","80/130","30","100","200","300"));

    static List<String> sportName=new ArrayList<String>(Arrays.asList("时间","距离","卡路里","速度","坡度"));
    static List<String> sportValue=new ArrayList<String>(Arrays.asList("13","40","30","5","6"));
    public final static List<PatientDetail> PATIENTS = new ArrayList<PatientDetail>(){{
        add(new PatientDetail("张三", "1", "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
        add(new PatientDetail("李四", "2",  "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
        add(new PatientDetail("王五", "3",  "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
        add(new PatientDetail("赵六", "4", "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
        add(new PatientDetail("赵六", "5", "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
        add(new PatientDetail("三个字", "6",  "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
        add(new PatientDetail("四个字", "7",  "跑步机","10%   第一段",phyName,phyValue,sportName,sportValue));
    }};
}
