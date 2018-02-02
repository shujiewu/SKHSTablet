package cn.sk.skhstablet.model;

import java.util.ArrayList;
import java.util.List;

import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_FALSE;

/**
 * Created by ldkobe on 2017/4/16.
 * 测试类，可删除
 */

public class PatientList {
    public final static List<Patient> PATIENTS = new ArrayList<Patient>(){{
        add(new Patient(1,"张三", "男", "001",(byte)0x00,(byte)0x00,(byte)0x01,"0001020304050607",(byte)0x03,(byte)0x00,PATIENT_SELECT_STATUS_FALSE));
        //add(new Patient(2,"李四", "男", "002",(byte)0x00,(byte)0x00,(byte)0x01,"0001020304050608",(byte)0x02,(byte)0x00,PATIENT_SELECT_STATUS_FALSE));
        add(new Patient(2,"王五", "男", "003",(byte)0x00,(byte)0x00,(byte)0x01,"0001020304050609",(byte)0x0e,(byte)0x00,PATIENT_SELECT_STATUS_FALSE));
        add(new Patient(3,"赵六", "男","004",(byte)0x00,(byte)0x00,(byte)0x01,"0001020304050610",(byte)0x01,(byte)0x00,PATIENT_SELECT_STATUS_FALSE));
    }};
}
