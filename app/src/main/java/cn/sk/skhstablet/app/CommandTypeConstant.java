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



	public static final byte SINGLE_MONITOR_REQUEST=(byte)0xD2;
	public static final byte MUTI_MONITOR_REQUEST=(byte)0xD1;
	public static final byte PATIENT_LIST_REQUEST=(byte)0xD3;

	public static final byte LOGIN_REQUEST=(byte)0xD6;
	public static final byte LOGOUT_REQUEST=(byte)0xD7;
	public static final byte CHANGE_KEY_REQUEST=(byte)0xD8;

	public static final byte DEV_NAME_REQUEST=(byte)0xD9;
	public static final byte MONITOR_DEV_FORM_REQUEST=(byte)0xDC;
	public static final byte SPORT_DEV_FORM_REQUEST=(byte)0xDD;


}
