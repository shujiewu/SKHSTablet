package cn.sk.skhstablet.handler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.domain.ECG;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.DeviceId;
import cn.sk.skhstablet.protocol.MonitorDevForm;
import cn.sk.skhstablet.protocol.SportDevForm;
import cn.sk.skhstablet.protocol.Version;
import cn.sk.skhstablet.protocol.down.DevNameResponse;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.protocol.down.LoginAckResponse;
import cn.sk.skhstablet.protocol.down.MonitorDevFormResponse;
import cn.sk.skhstablet.protocol.down.PatientListResponse;
import cn.sk.skhstablet.protocol.down.PushAckResponse;
import cn.sk.skhstablet.protocol.down.SportDevFormResponse;
import cn.sk.skhstablet.protocol.up.LoginRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_FALSE;
import static cn.sk.skhstablet.app.CommandTypeConstant.LOGIN_SUCCESS;
import static cn.sk.skhstablet.app.CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE;
import static cn.sk.skhstablet.app.CommandTypeConstant.PATIENT_LIST_SUCCESS;
import static cn.sk.skhstablet.app.CommandTypeConstant.PHY_CONN_ONLINE;
import static cn.sk.skhstablet.app.CommandTypeConstant.SPORT_DEV_CONNECT_OFFLINE;
import static cn.sk.skhstablet.app.CommandTypeConstant.SUCCESS;

@Sharable
public class ProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf reqBuf, List<Object> out) throws Exception {
		System.out.println(ByteBufUtil.hexDump(reqBuf));
		reqBuf.readUnsignedShortLE();
		reqBuf.readUnsignedShortLE();
		reqBuf.readUnsignedShortLE();
		AbstractProtocol request = null;
		Version version = decodeVersion(reqBuf);
		//System.out.println(version.reviseVersionNumber);
		byte commandType = reqBuf.readByte();
		//DeviceId deviceId = decodeDeviceId(reqBuf);
		//System.out.println(commandType);

		// System.out.println("request type:" + commandType);
		switch (commandType) {

		case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_REQUEST: {
			request = decodeExercisePhysiologicalDATARequest(reqBuf);
			break;
		}
		case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_REQUEST: {
			request = decodeExerciseEquipmentDataRequest(reqBuf);
			break;
		}
			case CommandTypeConstant.LOGIN_ACK_RESPONSE: {
				request=decodeLoginAckResponse(reqBuf,CommandTypeConstant.LOGIN_ACK_RESPONSE);
				break;
			}
            case CommandTypeConstant.LOGOUT_ACK_RESPONSE:{
                request=decodeLogoutAckResponse(reqBuf,CommandTypeConstant.LOGOUT_ACK_RESPONSE);
                break;
            }
            case CommandTypeConstant.CHANGE_KEY_ACK_RESPONSE:{
                request=decodeLoginAckResponse(reqBuf,CommandTypeConstant.CHANGE_KEY_ACK_RESPONSE);
                break;
            }

            case CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE:
			case CommandTypeConstant.PATIENT_LIST_NEW_DATA_RESPONSE:{
                request=decodePatientListDataResponse(reqBuf,CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE);
                break;
            }
            case CommandTypeConstant.PATIENT_LIST_UPDATE_RESPONSE:{
                request=decodePatientListUpdateDataResponse(reqBuf,CommandTypeConstant.PATIENT_LIST_UPDATE_RESPONSE);
                break;
            }


            /*case CommandTypeConstant.DEV_NAEM_RESPONSE:{
                request=decodeDevNameResponse(reqBuf,CommandTypeConstant.DEV_NAEM_RESPONSE);
                break;
            }*/
            case CommandTypeConstant.MONITOR_DEV_FORM_RESPONSE:{
                request=decodeMonitorDevFormResponse(reqBuf,CommandTypeConstant.MONITOR_DEV_FORM_RESPONSE);
                break;
            }
            case CommandTypeConstant.SPORT_DEV_FORM_RESPONSE:{
                request=decodeSportDevFormResponse(reqBuf,CommandTypeConstant.SPORT_DEV_FORM_RESPONSE);
                break;
            }


            case CommandTypeConstant.SINGLE_MONITOR_RESPONSE: {
                request=decodePushAckResponse(reqBuf,CommandTypeConstant.SINGLE_MONITOR_RESPONSE);
                break;
            }
            /*case CommandTypeConstant.PATIENT_LIST_RESPONSE:{
                request=decodePushAckResponse(reqBuf,CommandTypeConstant.PATIENT_LIST_RESPONSE);
                break;
            }*/

			case CommandTypeConstant.MUTI_MONITOR_RESPONSE: {
				request=decodePushAckResponse(reqBuf,CommandTypeConstant.MUTI_MONITOR_RESPONSE);
				break;
			}
		}
		request.setVersion(version);
		//request.setDeviceId(deviceId);
		out.add(request);
	}
	private ExercisePhysiologicalDataResponse decodeExercisePhysiologicalDATARequest(ByteBuf reqBuf) {
		ExercisePhysiologicalDataResponse request = new ExercisePhysiologicalDataResponse();
		request.setDataPacketNumber(reqBuf.readUnsignedIntLE());
		request.setPhysiologicalLength(reqBuf.readUnsignedShortLE());
		reqBuf.readBytes(request.getPhysiologicalData());
		short ecgAmount = reqBuf.readUnsignedByte();
		for (short i = 0; i < ecgAmount; i++) {
			short ecgNumber = reqBuf.readUnsignedByte();
			ECG ecg = new ECG(reqBuf.readUnsignedByte());
			System.out.println("ecg:"+ecg.getLength());
			reqBuf.readBytes(ecg.getContent());
			request.getEcgs().put(ecgNumber, ecg);
		}
		return request;
	}

	private Version decodeVersion(ByteBuf reqBuf) {
		Version version = new Version();
		version.majorVersionNumber = reqBuf.readUnsignedByte();
		version.secondVersionNumber = reqBuf.readUnsignedByte();
		version.reviseVersionNumber = reqBuf.readUnsignedShortLE();
		return version;
	}

	private DeviceId decodeDeviceId(ByteBuf reqBuf) {
		DeviceId deviceId = new DeviceId();
		deviceId.deviceType = reqBuf.readByte();
		deviceId.deviceModel = reqBuf.readByte();
		deviceId.deviceNumber = reqBuf.readLongLE();
		return deviceId;
	}

	private AbstractProtocol decodeExerciseEquipmentDataRequest(ByteBuf reqBuf) {
		ExerciseEquipmentDataResponse request = new ExerciseEquipmentDataResponse();
		request.setRFID(reqBuf.readLongLE());
		request.setPatientId(reqBuf.readIntLE());// 数据库主键，有符号
		request.setDataPacketNumber(reqBuf.readUnsignedIntLE());
		request.setPhysiologicalLength(reqBuf.readUnsignedShortLE());
		reqBuf.readBytes(request.getPhysiologicalData());
		request.setExercisePlanCompletionRate(reqBuf.readUnsignedByte());
		request.setPerformedExecutionAmount(reqBuf.readUnsignedShortLE());
		request.setExercisePlanId(reqBuf.readIntLE());// 数据库主键，有符号
		request.setExercisePlanSectionNumber(reqBuf.readUnsignedByte());
		byte[] data = new byte[reqBuf.writerIndex() - request.getChecksum().length - reqBuf.readerIndex()];
		reqBuf.readBytes(data);
		request.setEquipmentData(data);
		return request;
	}

	private AbstractProtocol decodeLoginAckResponse(ByteBuf resBuf,byte commandType)
	{
		LoginAckResponse response=new LoginAckResponse(commandType);
		//response.setUserID(resBuf.readIntLE());

		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());


		response.setState(resBuf.readByte());
		if(response.getState()==LOGIN_SUCCESS)
		{
			response.setUserID(resBuf.readIntLE());
			byte length=resBuf.readByte();
			response.setUserNameLength(length);
			byte[] name=new byte[length];
			resBuf.readBytes(name);
			response.setUserName(toUtf8(name));
		}
		return response;
	}
	private AbstractProtocol decodeLogoutAckResponse(ByteBuf resBuf,byte commandType)
	{
		LoginAckResponse response=new LoginAckResponse(commandType);
		//response.setUserID(resBuf.readIntLE());

		response.setState(resBuf.readByte());
		//response.setUserID(resBuf.readIntLE());
		//byte length=resBuf.readByte();
		//response.setUserNameLength(length);
		//byte[] name=new byte[length];
		//resBuf.readBytes(name);
		//response.setUserName(toUtf8(name));
		return response;
	}
	private AbstractProtocol decodePushAckResponse(ByteBuf resBuf,byte commandType)
	{
		PushAckResponse response=new PushAckResponse(commandType);
		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(resBuf.readByte());
		return response;
	}
	private AbstractProtocol decodePatientListUpdateDataResponse(ByteBuf resBuf,byte commandType)
	{
		PatientListResponse response=new PatientListResponse(commandType);
		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(PATIENT_LIST_SUCCESS);

		List<Patient> patientList=new ArrayList<>();
			Patient patient=new Patient();
			patient.setPatientID(resBuf.readIntLE());
			System.out.println(patient.getPatientID()+"病人id");
			patient.setPhyConnectState(resBuf.readByte());
			if(patient.getPhyConnectState()==PHY_CONN_ONLINE)
			{
				patient.setMonConnectState(resBuf.readByte());
				System.out.println(1);
			}
			patient.setSportState(resBuf.readByte());
			if(patient.getSportState()!=SPORT_DEV_CONNECT_OFFLINE)
			{
				System.out.println(2);
				patient.setDeviceNumber(resBuf.readLongLE());
				patient.setDevType(resBuf.readByte());
				patient.setDev("跑步机");
				patient.setConnectState(resBuf.readByte());
				patient.setSportPlanID(resBuf.readIntLE());
				patient.setSportPlanSegment(resBuf.readByte());
			}
			patient.setSelectStatus(PATIENT_SELECT_STATUS_FALSE);
			patientList.add(patient);
		response.setPatientList(patientList);
		return response;
	}
	private AbstractProtocol decodePatientListDataResponse(ByteBuf resBuf,byte commandType)
	{
		PatientListResponse response=new PatientListResponse(commandType);
		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(resBuf.readByte());
		if(response.getState()!=PATIENT_LIST_SUCCESS)
			return response;

		response.setPatientNumber(resBuf.readShortLE());
		int number=response.getPatientNumber();
		List<Patient> patientList=new ArrayList<>();
		System.out.println(String.valueOf(number)+"数量");
		for(short i=0;i<number;i++)
		{
			Patient patient=new Patient();

			patient.setPatientID(resBuf.readIntLE());
			System.out.println(patient.getPatientID()+"病人id");
			if(commandType==PATIENT_LIST_DATA_RESPONSE)
			{
				byte gender=resBuf.readByte();
				if(gender==0x00)
					patient.setGender("男");
				else
					patient.setGender("女");

				short nameLength=resBuf.readUnsignedByte();
				byte req[]=new byte[nameLength];
				resBuf.readBytes(req);
				patient.setName(toUtf8(req));
				System.out.println(patient.getName()+"姓名");

				short hospitalLength=resBuf.readUnsignedByte();
				System.out.println(String.valueOf(hospitalLength)+"hoslength");
				byte res[]=new byte[hospitalLength];
				resBuf.readBytes(res);
				patient.setHospitalNumber(toUtf8(res));
				System.out.println(patient.getHospitalNumber());

			}
			patient.setPhyConnectState(resBuf.readByte());
			if(patient.getPhyConnectState()==PHY_CONN_ONLINE)
			{
				patient.setMonConnectState(resBuf.readByte());
				System.out.println(1);
			}
			patient.setSportState(resBuf.readByte());
			if(patient.getSportState()!=SPORT_DEV_CONNECT_OFFLINE)
			{
				System.out.println(2);
				patient.setDeviceNumber(resBuf.readLongLE());
				patient.setDevType(resBuf.readByte());
				patient.setDev("跑步机");
				patient.setConnectState(resBuf.readByte());
				patient.setSportPlanID(resBuf.readIntLE());
				patient.setSportPlanSegment(resBuf.readByte());
			}
			patient.setSelectStatus(PATIENT_SELECT_STATUS_FALSE);
			patientList.add(patient);
		}
		response.setPatientList(patientList);
		return response;
	}

	/*private AbstractProtocol decodeDevNameResponse(ByteBuf resBuf,byte commandType)
	{
		DevNameResponse response=new DevNameResponse(commandType);
		response.setUserID(resBuf.readIntLE());
		response.setDevNumber(resBuf.readByte());

		byte number=response.getDevNumber();
		HashMap<Byte,String> devName=new HashMap<>();
		for(byte i=0;i<number;i++)
		{
			byte devType=resBuf.readByte();
			byte req[]=new byte[64];
			resBuf.readBytes(req);
			String name=toUtf8(req);

			devName.put(devType,name);
		}
		response.setDevName(devName);

		return response;
	}*/


	private AbstractProtocol decodeMonitorDevFormResponse(ByteBuf resBuf,byte commandType)
	{
		MonitorDevFormResponse response=new MonitorDevFormResponse(commandType);
		response.setUserID(resBuf.readIntLE());
		response.setDevNumber(resBuf.readByte());

		byte number=response.getDevNumber();
		HashMap<Byte,List<MonitorDevForm>> devData=new HashMap<>();
		for(byte i=0;i<number;i++)
		{
			byte devType=resBuf.readByte();
			//not have length
			byte paraNumber=resBuf.readByte();
			List<MonitorDevForm> monitorDevForms=new ArrayList<>();
			for(byte j=0;j<paraNumber;j++)
			{
				MonitorDevForm monitorDevForm=new MonitorDevForm();
				monitorDevForm.setLength(resBuf.readByte());
				byte req[]=new byte[64];
				resBuf.readBytes(req);
				String name=toUtf8(req);
				resBuf.readBytes(req);
				String unit=toUtf8(req);
				monitorDevForm.setName(name);
				monitorDevForm.setUnit(unit);
				monitorDevForms.add(monitorDevForm);
			}
			devData.put(devType,monitorDevForms);
		}
		response.setDevData(devData);
		return response;
	}
	private AbstractProtocol decodeSportDevFormResponse(ByteBuf resBuf,byte commandType)
	{
		SportDevFormResponse response=new SportDevFormResponse(commandType);
		response.setUserID(resBuf.readIntLE());
		response.setDevNumber(resBuf.readByte());

		byte number=response.getDevNumber();
		HashMap<Byte,List<SportDevForm>> devData=new HashMap<>();
		for(byte i=0;i<number;i++)
		{
			byte devType=resBuf.readByte();
			//not have length
			byte paraNumber=resBuf.readByte();
			List<SportDevForm> sportDevForms=new ArrayList<>();
			for(byte j=0;j<paraNumber;j++)
			{
				SportDevForm sportDevForm=new SportDevForm();
				sportDevForm.setLength(resBuf.readByte());
				byte req[]=new byte[64];   //64不知道够不，这里直接覆盖写，不知道是否可行
				resBuf.readBytes(req);
				String name=toUtf8(req);
				resBuf.readBytes(req);
				String unit=toUtf8(req);
				sportDevForm.setName(name);
				sportDevForm.setUnit(unit);

				sportDevForm.setIsAdjust(resBuf.readByte());
				if(sportDevForm.getIsAdjust()==0x00) //ketiaojie
				{
					sportDevForm.setMax(resBuf.readByte());
					sportDevForm.setMin(resBuf.readByte());
					sportDevForm.setPrecision(resBuf.readByte());
					sportDevForm.setAdjustCode(resBuf.readByte());
				}
				sportDevForms.add(sportDevForm);
			}
			devData.put(devType,sportDevForms);
		}
		response.setDevData(devData);

		return response;
	}
	public static String toUtf8(byte str[]) {
		String result = null;
		try {
			result = new String(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
