package cn.sk.skhstablet.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.protocol.MonitorDevForm;
import cn.sk.skhstablet.protocol.SportDevForm;

/**
 * Created by ldkobe on 2017/3/21.
 * 包含了APP用到的常量
 */

public class AppConstants {
    //多人监控和单人监控的页面状态
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_SUCCESS = 4;


    //public static  final int WECHA_SEARCH = 1000;
    //观察者需要的常量
    public static  final int LOGIN_STATE=1001;       //观察登录状态响应
    public static  final int LOGOUT_STATE=1002;      //观察注销状态响应
    public static  final int CHANGE_KEY_STATE=1003; //观察修改密码状态响应
    public static  final int LOGIN_OTHER_STATE=1004;//观察在其他地方登录的响应

    public static  final int MUTI_DATA=2000;         //观察多人监控的运动设备数据
    public static  final int MUTI_PHY_DATA=2002;         //观察多人监控的运动设备数据
    public static  final int MUTI_REQ_STATE=2001;   //观察运动设备数据请求的响应
    public static  final int SINGLE_REQ_STATE=3001; //观察生理数据请求的响应
    public static  final int CONTORL_REQ_STATE=3002; //观察控制命令的响应
    public static  final int SINGLE_DATA=3000;        //观察单人监控需要的运动设备数据
    public static  final int PHY_DATA=3005;            //观察生理数据
    public static  final int EXERCISE_PLAN_STATE=3006;//观察医嘱请求响应
    public static  final int PATIENT_LIST_REQ_STATE=6001;
    public static  final int PATIENT_LIST_DATA_STATE=6002;//观察全局病人列表请求的响应

    public static  String url="192.168.2.180";     //服务器地址
    public static  int port=10000;                   //端口

    public static final int RE_SEND_REQUEST=4000;          //观察是否需要重新发送命令
    public static final int RE_SEND_REQUEST_FAIL=5000;

    public static final String PATIENT_SELECT_STATUS_FALSE="未选择";
    public static final String PATIENT_SELECT_STATUS_TRUE="已选择";
    public static final String PATIENT_SELECT_STATUS_MONITOR="正在监控";
    //public static final String PATIENT_SELECT_STATUS_SINGLE_MONITOR="单人监控";

    //用户id
    public static int USER_ID=0;
    //01代表移动端设备类型
    public static final byte DEV_TYPE=0x01;
    //请求的id
    public static byte SINGLE_REQ_ID=0;
    public static byte MUTI_REQ_ID=0;
    public static byte SINGLE_SPORT_REQ_ID=1;
    public static byte PATIENT_LIST_REQ_ID=0;
    public static byte LOGIN_REQ_ID=0;
    public static byte CHANGE_KEY_REQ_ID=0;
    public static byte LOGOUT_REQ_ID=0;
    public static byte SPORT_FORM_REQ_ID=0;
    public static byte PHY_FORM_REQ_ID=0;
    public static byte CONTROL_REQ_ID=0;

    //登录成功后记录用户名和密码，重新登录不需要重新输入
    public static String LOGIN_NAME;
    public static String LOGIN_KEY;
    //设备类型和名称的映射
    public static HashMap<Byte,String> DEV_NAME=new HashMap<>();
    //运动设备和解析格式的映射
    public static HashMap<Byte,List<SportDevForm>> SPORT_DEV_FORM=new HashMap<>();
    //监护设备和解析格式的映射
    public static HashMap<Byte,List<MonitorDevForm>> MON_DEV_FORM=new HashMap<>();
    //public static HashMap<String,>

    public static boolean canModify=true;
    public static List<Patient> PATIENT_LIST_DATA=new ArrayList<>();//新到的病人列表信息
    public static List<Integer> SIGN_OUT_PATIENT_LIST=new ArrayList<>();
    public static HashMap<Integer,String> PATIENT_LIST_NAME_FORM=new HashMap<>();  //病人列表id和姓名的映射，主要为了在运动设备数据解析时可以通过id获取到病人姓名
    public static HashMap<Integer,String> PATIENT_LIST_NUMBER_FORM=new HashMap<>();//病人列表id和住院号的映射

    public static HashMap<Integer,Integer> hasMutiPatient=new HashMap<>();//多人监控界面的患者id和所处位置
    public static String singleMonitorID;                                  //单人监控的患者id
    public static List<PatientDetail> mutiDatas=new ArrayList<>();         //多人监控界面的患者数据
    public static int mutiPosition=0;                                      //多人监控界面最后一位患者所处的位置

    //上一次请求多人监控的患者id，主要是为了重新发送命令
    public static List<Integer> lastMutiPatientID=new ArrayList<>();
    //上一次请求单人监控的患者id
    public static String lastSinglePatientID;
    //判断是否是退出的命令，主要用于重新发送命令
    public static Boolean isLogout=false;
    //判断是否是取消单人监控的命令
    public static Boolean isCancelSingle=false;
    //判断是否是登录的命令
    public static Boolean isLoginOther=false;

    //和服务器的网络连接状态，已连接 未连接 正在连接
    public static byte netState=AppConstants.STATE_DIS_CONN;
    public static byte STATE_CONN=0x00;
    public static byte STATE_DIS_CONN=(byte) 0xff;
    public static byte STATE_IN_CONN=0x01;

    //权限
    public static final int CODE_READ_FILE = 0;
    public static final int CODE_WRITE_FILE = 1;
    public static final int CODE_CREATE_FILE = 2;
}
