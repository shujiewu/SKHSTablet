package cn.sk.skhstablet.app;

public class CommandTypeConstant {
	public static final byte HEART_BEAT_RESPONSE = (byte) 0x20;
	public static final byte EQUIPMENT_STATUS_RESPONSE = (byte) 0x21;
	public static final byte MONITORING_EQUIPMENT_RESPONSE = (byte) 0x30;
	public static final byte EXERCISE_PHYSIOLOGICAL_DATA_RESPONSE = (byte) 0x31;
	public static final byte RF_PARAMETERS_APPLICATION_RESPONSE = (byte) 0x40;
	public static final byte DOCTOR_ADVICE_RESPONSE = (byte) 0x42;
	public static final byte EXERCISE_EQUIPMENT_DATA_RESPONSE = (byte) 0x43;
	public static final byte REMOTE_CONTROL_REQUEST = (byte) 0x44;
	// public static final byte PHYSIOLOGICAL_INSTRUMENT_BINGDING_RESPONSE =
	// (byte) 0x50;
	public static final byte RF_PARAMETERS_RESPONSE = (byte) 0xB2;
	public static final byte HEART_BEAT_REQUEST = (byte) 0xA0;
	public static final byte EQUIPMENT_STATUS_REQUEST = (byte) 0xA1;
	public static final byte EXERCISE_PHYSIOLOGICAL_DATA_REQUEST = (byte) 0xB1;
	public static final byte EXERCISE_EQUIPMENT_DATA_REQUEST = (byte) 0xC3;
	public static final byte DOCTOR_ADVICE_REQUEST = (byte) 0xC2;
	// public static final byte PHYSIOLOGICAL_INSTRUMENT_BINGDING_REQUEST =
	// (byte) 0xD0;
	public static final byte REMOTE_CONTROL_RESPONSE = (byte) 0xC4;
	public static final byte RF_PARAMETERS_APPLICATION_REQUEST = (byte) 0xC0;
	public static final byte RF_PARAMETERS_REQUEST = (byte) 0xC1;
	public static final byte MONITORING_EQUIPMENT_REQUEST = (byte) 0xB0;
	/** 获取体外反博病人列表请求 **/
	public static final byte ECP_PATIENT_LIST_REQUEST = (byte) 0xC5;
	/** 获取体外反博病人列表响应 **/
	public static final byte ECP_PATIENT_LIST_RESPONSE = (byte) 0x45;


	public static final byte LOGIN_SUCCESS = (byte) 0x00;
	public static final byte LOGIN_MATCH_FAIL=(byte) 0x01;
	public static final byte LOGIN_NONE_FAIL=(byte) 0xFF;

	public static final byte SUCCESS = (byte) 0x00;
	public static final byte NONE_FAIL = (byte) 0xFF;
	public static final byte NO_LOGIN = (byte) 0xFE;
	public static final byte OLD_KEY_FALSE = (byte) 0xFD;

	public static final byte PATIENT_LIST_SUCCESS= (byte) 0x00;
	public static final byte PATIENT_LIST_NONE_FAIL= (byte) 0xff;
	public static final byte PATIENT_LIST_NO_LOGIN= (byte) 0xfe;

	public static final byte PHY_CONN_ONLINE= (byte) 0x00;
	public static final byte PHY_CONN_OFFLINE= (byte) 0x01;
	public static final byte PHY_NO_CONN= (byte) 0x03;  //未连接
	public static final byte PHY_CONN_NONE= (byte) 0x04;//无

	public static final byte MON_CONN_ONLINE= (byte) 0x00;
	public static final byte MON_CONN_OFFLINE= (byte) 0x01;

	//连接状态
	public static final byte SPORT_DEV_CONNECT_ONLINE=(byte)0x00;  //已打卡
	public static final byte PHY_DEV_CONNECT_ONLINE=(byte)0x00;     //生理仪在线
	public static final byte PHY_DEV_CONNECT_OFFLINE=(byte)0x01;    //生理仪离线

	//运动状态
	public static final byte SPORT_NOMAL=(byte)0x01;                  //正常运动
	public static final byte SPORT_WARNING=(byte)0x02;				//预警运动
	public static final byte SPORT_NONE=(byte)0x00;					//未运动
	public static final byte SPORT_DEV_CONNECT_OFFLINE=(byte)0x03; //未打卡
	public static final byte SPORT_CONN_ONLINE=(byte)0x00;
	public static final byte SPORT_CONN_OFFLINE=(byte)0x01;

	public static final byte SINGLE_MONITOR_REQUEST=(byte)0x03;
	public static final byte MUTI_MONITOR_REQUEST=(byte)0x02;
	public static final byte PATIENT_LIST_REQUEST=(byte)0x01;

	public static final byte LOGIN_REQUEST=(byte)0x04;
	public static final byte LOGOUT_REQUEST=(byte)0x07;
	public static final byte CHANGE_KEY_REQUEST=(byte)0x0B;

	//public static final byte DEV_NAME_REQUEST=(byte)0xD9;
	public static final byte MONITOR_DEV_FORM_REQUEST=(byte)0x0A;
	public static final byte SPORT_DEV_FORM_REQUEST=(byte)0x09;

	public static final byte LOGIN_ACK_RESPONSE=(byte)0x40;
	public static final byte LOGOUT_ACK_RESPONSE=(byte)0x70;
	public static final byte CHANGE_KEY_ACK_RESPONSE=(byte)0xB0;

	public static final byte PATIENT_LIST_DATA_RESPONSE=(byte)0x10;
	public static final byte PATIENT_LIST_UPDATE_RESPONSE=(byte)0x08;
	public static final byte PATIENT_LIST_NEW_DATA_RESPONSE=(byte)0x0D;//新簽到

	public static final byte MUTI_MONITOR_RESPONSE=(byte)0x20;
	public static final byte SINGLE_MONITOR_RESPONSE=(byte)0x30;

	public static final byte MONITOR_DEV_FORM_RESPONSE=(byte)0xA0;
	public static final byte SPORT_DEV_FORM_RESPONSE=(byte)0x90;

	public static final byte DEV_NAEM_RESPONSE=(byte)0xE9;
	public static final byte PATIENT_LIST_RESPONSE=(byte)0xE3;//WUYONG

	public static final byte SPORT_DEV_PARA_CERTAIN=0x01;
	public static final byte SPORT_DEV_PARA_NOT_CERTAIN=0x00;

	public static final byte SPORT_DEV_CONTROL_REQUEST=(byte) 0x44;
	public static final byte SPORT_DEV_CONTROL_RESPONSE=(byte) 0xC4;
	public static final byte SPORT_DEV_START_STOP=(byte)0x00;
	//public static final byte SPORT_DEV_STOP=(byte)0x00;
}
