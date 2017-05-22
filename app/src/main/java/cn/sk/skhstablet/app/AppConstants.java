package cn.sk.skhstablet.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.protocol.MonitorDevForm;
import cn.sk.skhstablet.protocol.SportDevForm;

/**
 * Created by ldkobe on 2017/3/21.
 */

public class AppConstants {
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_SUCCESS = 4;
    //需要APIKEY请去 http://www.tianapi.com/#wxnew 申请,复用会减少访问可用次数。还有很多别的接口大家可以研究。
    public static final String KEY_API = "e6d6ec3ba2f9d7a3051a6c09f0524738";

    public static  final int WECHA_SEARCH = 1000;
    public static  final int LOGIN_STATE=1001;
    public static  final int LOGOUT_STATE=1002;
    public static  final int CHANGE_KEY_STATE=1003;

    public static  final int MUTI_DATA=2000;
    public static  final int MUTI_REQ_STATE=2001;
    public static  final int SINGLE_REQ_STATE=3001;
    public static  final int SINGLE_DATA=3000;
    public static  final int PATIENT_LIST_REQ_STATE=6001;
    public static  final int PATIENT_LIST_DATA_STATE=6002;

    public static  final  String url="10.250.110.19";
    public static  final  int port=10000;

    public static final int RE_SEND_REQUEST=4000;
    public static final int RE_SEND_REQUEST_FAIL=5000;

    public static final String PATIENT_SELECT_STATUS_FALSE="未选择";
    public static final String PATIENT_SELECT_STATUS_TRUE="已选择";
    public static final String PATIENT_SELECT_STATUS_MONITOR="正在监控";
    //public static final String PATIENT_SELECT_STATUS_SINGLE_MONITOR="单人监控";

    public static int USER_ID=0;
    public static final byte DEV_TYPE=0x01;
    public static byte SINGLE_REQ_ID=0;
    public static byte MUTI_REQ_ID=0;
    public static byte PATIENT_LIST_REQ_ID=0;
    public static byte LOGIN_REQ_ID=0;
    public static byte CHANGE_KEY_REQ_ID=0;
    public static byte LOGOUT_REQ_ID=0;
    public static byte SPORT_FORM_REQ_ID=0;
    public static byte PHY_FORM_REQ_ID=0;

    public static HashMap<Byte,String> DEV_NAME=new HashMap<>();
    public static HashMap<Byte,List<SportDevForm>> SPORT_DEV_FORM=new HashMap<>();
    public static HashMap<Byte,List<MonitorDevForm>> MON_DEV_FORM=new HashMap<>();


    public static List<Patient> PATIENT_LIST_DATA=new ArrayList<>();
    public static HashMap<Integer,String> PATIENT_LIST_NAME_FORM=new HashMap<>();
    public static HashMap<Integer,String> PATIENT_LIST_NUMBER_FORM=new HashMap<>();
}
