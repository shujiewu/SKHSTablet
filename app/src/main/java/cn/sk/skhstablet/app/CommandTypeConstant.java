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


	public static final byte LOGIN_SUCCESS = (byte) 0x10;
	public static final byte SUCCESS = (byte) 0x00;
	public static final byte FAIL = (byte) 0x11;

	public static final byte SINGLE_MONITOR_REQUEST=(byte)0xD2;
	public static final byte MUTI_MONITOR_REQUEST=(byte)0xD1;
	public static final byte PATIENT_LIST_REQUEST=(byte)0xD3;

	public static final byte LOGIN_REQUEST=(byte)0xD6;
	public static final byte LOGOUT_REQUEST=(byte)0xD7;
	public static final byte CHANGE_KEY_REQUEST=(byte)0xD8;

	public static final byte DEV_NAME_REQUEST=(byte)0xD9;
	public static final byte MONITOR_DEV_FORM_REQUEST=(byte)0xDC;
	public static final byte SPORT_DEV_FORM_REQUEST=(byte)0xDD;

	public static final byte LOGIN_ACK_RESPONSE=(byte)0xE6;
	public static final byte LOGOUT_ACK_RESPONSE=(byte)0xE7;
	public static final byte CHANGE_KEY_ACK_RESPONSE=(byte)0xE8;

	public static final byte PATIENT_LIST_DATA_RESPONSE=(byte)0xE4;
	public static final byte PATIENT_LIST_UPDATE_RESPONSE=(byte)0xE5;
	public static final byte DEV_NAEM_RESPONSE=(byte)0xE9;
	public static final byte MUTI_MONITOR_RESPONSE=(byte)0xE1;
	public static final byte SINGLE_MONITOR_RESPONSE=(byte)0xE2;
	public static final byte PATIENT_LIST_RESPONSE=(byte)0xE3;
	public static final byte MONITOR_DEV_FORM_RESPONSE=(byte)0xEC;
	public static final byte SPORT_DEV_FORM_RESPONSE=(byte)0xED;

	//连接状态
	public static final byte SPORT_DEV_CONNECT_ONLINE=(byte)0x00;  //已打卡
	public static final byte SPORT_DEV_CONNECT_OFFLINE=(byte)0x01; //未打卡
	public static final byte PHY_DEV_CONNECT_ONLINE=(byte)0x00;     //生理仪在线
	public static final byte PHY_DEV_CONNECT_OFFLINE=(byte)0x01;    //生理仪离线
	//运动状态
	public static final byte SPORT_NOMAL=(byte)0x00;                  //正常运动
	public static final byte SPORT_WARNING=(byte)0x01;				//预警运动
	public static final byte SPORT_NONE=(byte)0x02;					//未运动
}
