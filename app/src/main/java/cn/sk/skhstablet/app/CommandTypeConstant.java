package cn.sk.skhstablet.app;

public class CommandTypeConstant {
	public static final byte HEART_BEAT_RESPONSE = (byte) 0x20;
	public static final byte EQUIPMENT_STATUS_RESPONSE = (byte) 0x21;
	public static final byte MONITORING_EQUIPMENT_RESPONSE = (byte) 0x30;
	public static final byte EXERCISE_PHYSIOLOGICAL_DATA_REQUSET = (byte) 0x31;
	public static final byte RF_PARAMETERS_APPLICATION_RESPONSE = (byte) 0x40;
	public static final byte DOCTOR_ADVICE_RESPONSE = (byte) 0x42;
	public static final byte EXERCISE_EQUIPMENT_DATA_REQUEST = (byte) 0x43;
	public static final byte REMOTE_CONTROL_REQUEST = (byte) 0x44;
	public static final byte RF_PARAMETERS_RESPONSE = (byte) 0xB2;
	public static final byte HEART_BEAT_REQUEST = (byte) 0xA0;
	public static final byte EQUIPMENT_STATUS_REQUEST = (byte) 0xA1;
	public static final byte DOCTOR_ADVICE_REQUEST = (byte) 0xC2;
	public static final byte REMOTE_CONTROL_RESPONSE = (byte) 0xC4;
	public static final byte RF_PARAMETERS_APPLICATION_REQUEST = (byte) 0xC0;
	public static final byte RF_PARAMETERS_REQUEST = (byte) 0xC1;
	public static final byte MONITORING_EQUIPMENT_REQUEST = (byte) 0xB0;
	/** 获取体外反博病人列表请求 **/
	public static final byte ECP_PATIENT_LIST_REQUEST = (byte) 0xC5;
	/** 获取体外反博病人列表响应 **/
	public static final byte ECP_PATIENT_LIST_RESPONSE = (byte) 0x45;


	//生理数据响应
	public static final byte EXERCISE_PHYSIOLOGICAL_DATA_RESPONSE = (byte) 0xB1;
	//运动设备数据响应
	public static final byte EXERCISE_EQUIPMENT_DATA_RESPONSE = (byte) 0xC3;
	//在其他地方登录状态
	public static final byte LOGIN_OTHER = (byte) 0x00;
	//登录状态
	public static final byte LOGIN_SUCCESS = (byte) 0x00;
	public static final byte LOGIN_MATCH_FAIL=(byte) 0x01;
	public static final byte LOGIN_NONE_FAIL=(byte) 0xFF;

	//通用状态
	public static final byte SUCCESS = (byte) 0x00;
	public static final byte NONE_FAIL = (byte) 0xFF;
	public static final byte NO_LOGIN = (byte) 0xFE;
	public static final byte OLD_KEY_FALSE = (byte) 0xFD;

	//病人列表响应的状态
	public static final byte PATIENT_LIST_SUCCESS= (byte) 0x00;
	public static final byte PATIENT_LIST_NONE_FAIL= (byte) 0xff;
	public static final byte PATIENT_LIST_NO_LOGIN= (byte) 0xfe;

	//生理仪状态
	public static final byte PHY_CONN_ONLINE= (byte) 0x00;
	public static final byte PHY_CONN_OFFLINE= (byte) 0x01;
	public static final byte PHY_NO_CONN= (byte) 0x03;  //未连接
	public static final byte PHY_CONN_NONE= (byte) 0x04;//无

	//监护设备状态
	public static final byte MON_CONN_ONLINE= (byte) 0x00;
	public static final byte MON_CONN_OFFLINE= (byte) 0x01;

	//连接状态
	public static final byte SPORT_DEV_CONNECT_ONLINE=(byte)0x00;  //已打卡

	public static final byte PHY_DEV_CONNECT_ONLINE=(byte)0x00;     //生理仪在线
	public static final byte PHY_DEV_CONNECT_OFFLINE=(byte)0x01;    //生理仪掉线
	public static final byte PHY_DEV_CONNECT_NONE=(byte)0x02;     //生理仪未连接
	public static final byte PHY_DEV_NONE=(byte)0x03;    //生理仪无

	public static final byte MON_DEV_CONNECT_ONLINE=(byte)0x00;     //监护设备正常
	public static final byte MON_DEV_CONNECT_OFFLINE=(byte)0x01;    //监护设备异常
	public static final byte MON_DEV_CONNECT_READY=(byte)0x02;    //监护设备准备中

	//运动状态
	public static final byte SPORT_NOMAL=(byte)0x01;                  //正常运动
	public static final byte SPORT_WARNING=(byte)0x02;				//预警运动
	public static final byte SPORT_NONE=(byte)0x00;					//未运动
	public static final byte 	SPORT_DEV_CONNECT_OFFLINE=(byte)0x03;  //未打卡

	public static final byte SPORT_CONN_ONLINE=(byte)0x00;           //运动设备在线
	public static final byte SPORT_CONN_OFFLINE=(byte)0x01;			//运动设备离线

	//命令类型
	public static final byte SINGLE_MONITOR_REQUEST=(byte)0x03;     //单人监控，其实就是为了获取生理数据

	public static final byte MUTI_MONITOR_REQUEST=(byte)0x02;		//多人监控，其实就是为了获取运动设备数据
	public static final byte PATIENT_LIST_REQUEST=(byte)0x01;		//病人列表获取命令

	public static final byte LOGIN_REQUEST=(byte)0x04;
	public static final byte LOGOUT_REQUEST=(byte)0x07;
	public static final byte CHANGE_KEY_REQUEST=(byte)0x0B;

	//public static final byte DEV_NAME_REQUEST=(byte)0xD9;
	public static final byte MONITOR_DEV_FORM_REQUEST=(byte)0x0A;
	public static final byte SPORT_DEV_FORM_REQUEST=(byte)0x09;

	public static final byte LOGIN_ACK_RESPONSE=(byte)0x40;
	public static final byte LOGIN_OTHER_RESPONSE=(byte)0x05;
	public static final byte LOGOUT_ACK_RESPONSE=(byte)0x70;
	public static final byte CHANGE_KEY_ACK_RESPONSE=(byte)0xB0;

	public static final byte PATIENT_LIST_DATA_RESPONSE=(byte)0x10;
	public static final byte PATIENT_LIST_UPDATE_RESPONSE=(byte)0x08;
	public static final byte PATIENT_LIST_NEW_DATA_RESPONSE=(byte)0x0D;//新簽到

	public static final byte MUTI_MONITOR_RESPONSE=(byte)0x20;
	public static final byte SINGLE_MONITOR_RESPONSE=(byte)0x30;

	public static final byte MONITOR_DEV_FORM_RESPONSE=(byte)0xA0;
	public static final byte SPORT_DEV_FORM_RESPONSE=(byte)0x90;


	//运动设备数据参数长度是否固定
	public static final byte SPORT_DEV_PARA_CERTAIN=0x01;
	public static final byte SPORT_DEV_PARA_NOT_CERTAIN=0x00;

	public static final byte SPORT_DEV_CONTROL_REQUEST=(byte) 0x44;
	public static final byte SPORT_DEV_CONTROL_RESPONSE=(byte) 0xC4;
	public static final byte SPORT_DEV_START_STOP=(byte)0x00;

	//控制命令增加，减少，调到，开始，停止
	public static final byte SPORT_DEV_CONTORL_ADD=(byte)0x00;
	public static final byte SPORT_DEV_CONTORL_CUT=(byte)0x01;
	public static final byte SPORT_DEV_CONTORL_TO=(byte)0x02;
	public static final byte SPORT_DEV_CONTORL_STATR=(byte)0x03;
	public static final byte SPORT_DEV_CONTORL_STOP=(byte)0x04;

	//控制命令结果，下面是服务器发送的结果
	public static final byte SPORT_DEV_CONTORL_SUCC=(byte)0x00;
	public static final byte SPORT_DEV_CONTORL_FAIL=(byte)0xFF;
	public static final byte SPORT_DEV_CONTORL_NOT_LOGIN=(byte)0xFE;
	public static final byte SPORT_DEV_CONTORL_NONE_DEV=(byte)0x9F;
	public static final byte SPORT_DEV_CONTORL_DEV_OFFLINE=(byte)0x9E;
	public static final byte SPORT_DEV_CONTORL_IN_CONTORL=(byte)0x9D;
	public static final byte SPORT_DEV_CONTORL_DEV_FREE=(byte)0x9C;
	public static final byte SPORT_DEV_CONTORL_UNREACH=(byte)0x9B;
	//控制命令结果，下面是设备发送的结果
	public static final byte SPORT_DEV_CONTORL_GREATER_MAX=(byte)0x01;
	public static final byte SPORT_DEV_CONTORL_LESS_MIN=(byte)0x02;
	public static final byte SPORT_DEV_CONTORL_FREE=(byte)0x03;
	public static final byte SPORT_DEV_CONTORL_READY=(byte)0x04;
	public static final byte SPORT_DEV_CONTORL_RUN=(byte)0x05;

	public static final byte SPORT_DEV_CANNOT_CONTROL=0x00;
	public static final byte SPORT_DEV_CAN_CONTROL=0x01;

	//心跳命令
	public static final byte IDLE_HEART_REQUEST=(byte)0x06;
	public static final byte IDLE_HEART_RESPONSE=(byte)0x60;

	//医嘱命令
	public static final byte EXERCISE_PLAN_REQUEST=(byte)0x0E;
	public static final byte EXERCISE_PLAN_RESPONSE=(byte)0xE0;

	public static final byte MON_ECG_EXCEPTION=(byte)0x11;
	public static final byte MON_SPO_EXCEPTION=(byte)0x13;
	public static final byte MON_PRE_EXCEPTION=(byte)0x15;

	public static final byte 舒张压上限预警=(byte)0xEF;
	public static final byte 舒张压超上限=(byte)0xEE;
	public static final byte 舒张压下限预警=(byte)0xED;
	public static final byte 舒张压超下限=(byte)0xEC;
	public static final byte 收缩压上限预警=(byte)0xEA;
	public static final byte 收缩压超上限=(byte)0xE9;
	public static final byte 收缩压下限预警=(byte)0xE8;
	public static final byte 收缩压超下限=(byte)0xE7;
	public static final byte 血氧下限预警=(byte)0xE3;
	public static final byte 血氧超下限=(byte)0xE2;
	public static final byte 心率上限预警=(byte)0xE0;
	public static final byte 心率超上限=(byte)0xDF;
	public static final byte 心率下限预警=(byte)0xDE;
	public static final byte 心率超下限=(byte)0xDD;

	public static final byte SIGN_OUT_RESPONSE = (byte) 0x0F;

}
