package cn.sk.skhstablet.handler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.domain.ECG;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientDetail;
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
import cn.sk.skhstablet.protocol.down.SportDevControlResponse;
import cn.sk.skhstablet.protocol.down.SportDevFormResponse;
import cn.sk.skhstablet.protocol.up.LoginRequest;
import cn.sk.skhstablet.utlis.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import static cn.sk.skhstablet.app.AppConstants.DEV_NAME;
import static cn.sk.skhstablet.app.AppConstants.MON_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NAME_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NUMBER_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_FALSE;
import static cn.sk.skhstablet.app.AppConstants.SPORT_DEV_FORM;
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
                request=decodeChangeKeyAckResponse(reqBuf,CommandTypeConstant.CHANGE_KEY_ACK_RESPONSE);
                break;
            }

            case CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE:{
				request=decodePatientListDataResponse(reqBuf,CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE);
				break;
			}
			case CommandTypeConstant.PATIENT_LIST_NEW_DATA_RESPONSE:{
                request=decodePatientListNewDataResponse(reqBuf,CommandTypeConstant.PATIENT_LIST_NEW_DATA_RESPONSE);
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

			case CommandTypeConstant.SPORT_DEV_CONTROL_RESPONSE:{
				request=decodeSportDevControlResponse(reqBuf,CommandTypeConstant.SPORT_DEV_CONTROL_RESPONSE);
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

		byte [] deviceNumber=new byte[8];
		reqBuf.readBytes(deviceNumber);
		//System.out.println(ByteBufUtil.hexDump(deviceNumber)+"number");
		//patient.setDeviceNumber(ByteBufUtil.hexDump(deviceNumber));
		deviceId.deviceNumber = ByteBufUtil.hexDump(deviceNumber);
		return deviceId;
	}

	private AbstractProtocol decodeExerciseEquipmentDataRequest(ByteBuf reqBuf) {
		ExerciseEquipmentDataResponse request = new ExerciseEquipmentDataResponse();
		DeviceId deviceId = decodeDeviceId(reqBuf);
		request.setDeviceId(deviceId);
		request.setRFID(reqBuf.readLongLE());
		request.setPatientId(reqBuf.readIntLE());// 数据库主键，有符号
		request.setDataPacketNumber(reqBuf.readUnsignedIntLE());


		request.setPhysiologicalLength(reqBuf.readUnsignedShortLE());
		//byte phydata[]=new byte[request.getPhysiologicalLength()];
		//reqBuf.readBytes(phydata);
		//request.setPhysiologicalData(phydata);
		int size=reqBuf.readUnsignedByte();
		List<String> sportDevName=new ArrayList<String>();  //指的是参数名称，而不是设备名称
		List<String> phyDevName=new ArrayList<String>();
		List<String> sportDevValue=new ArrayList<String>();
		List<String> phyDevValue=new ArrayList<String>();
		for(int i=0;i<size;i++)
		{
			byte monitorType=reqBuf.readByte();
			List<MonitorDevForm> monitorDevForms=MON_DEV_FORM.get(monitorType);
			//j++;
			for(MonitorDevForm monitorDevForm:monitorDevForms)
			{
				phyDevName.add(monitorDevForm.getChineseName());
				int valuelength=monitorDevForm.getLength();
				long value=0;
				switch (valuelength)
				{
					case 1:
						value=reqBuf.readByte();
						break;
					case 2:
						value=reqBuf.readShortLE();
						break;
					case 4:
						value=reqBuf.readIntLE();
						break;
					case 8:
						value=reqBuf.readLongLE();
						break;
				}

				//byte [] value=new byte[valuelength];
				    //System.arraycopy(phydata,j,value,0,valuelength);
				//reqBuf.readBytes(value);
				phyDevValue.add(String.valueOf(value));///byte
				//j=j+valuelength;
			}
		}



		request.setExercisePlanCompletionRate(reqBuf.readUnsignedByte());
		request.setPerformedExecutionAmount(reqBuf.readUnsignedShortLE());
		request.setExercisePlanId(reqBuf.readIntLE());// 数据库主键，有符号
		request.setExercisePlanSectionNumber(reqBuf.readUnsignedByte());
		//byte[] sportdata = new byte[reqBuf.writerIndex() - request.getChecksum().length - reqBuf.readerIndex()];
		//reqBuf.readBytes(sportdata);
		//request.setEquipmentData(sportdata);

		PatientDetail patientDetail=new PatientDetail();
		patientDetail.setPatientID(request.getPatientId());
		patientDetail.setName(PATIENT_LIST_NAME_FORM.get(request.getPatientId()));
		patientDetail.setHospitalNumber(PATIENT_LIST_NUMBER_FORM.get(request.getPatientId()));
		patientDetail.setDev(DEV_NAME.get(deviceId.deviceType));
		patientDetail.setDevType(deviceId.deviceType);
		patientDetail.setDeviceNumber(deviceId.deviceNumber);
		patientDetail.setPercent(String.valueOf(request.getExercisePlanCompletionRate()));


		//size=sportdata.length;
		List<SportDevForm> sportDevForms=SPORT_DEV_FORM.get(deviceId.deviceType);
		int valuelength=0;

		//int pos=0;
		for(SportDevForm sportDevForm:sportDevForms)
		{
			sportDevName.add(sportDevForm.getChineseName());
			if(sportDevForm.getParaType()==CommandTypeConstant.SPORT_DEV_PARA_CERTAIN)
			{
				valuelength=sportDevForm.getLength();
				byte [] value=new byte[valuelength];
				reqBuf.readBytes(value);
				//System.arraycopy(sportdata,pos,value,0,valuelength);

				sportDevValue.add(String.valueOf(bytesToInt(value,0)));///byte
				//pos=pos+valuelength;
			}
			else
			{
				valuelength=sportDevForm.getLength();
				//byte [] value=new byte[valuelength];
				//System.arraycopy(sportdata,pos,value,0,valuelength);
				//pos=pos+valuelength;
				long value=0;
				switch (valuelength)
				{
					case 1:
						value=reqBuf.readByte();
						break;
					case 2:
						value=reqBuf.readShortLE();
						break;
					case 4:
						value=reqBuf.readIntLE();
						break;
					case 8:
						value=reqBuf.readLongLE();
						break;
				}

				int length=(int)value;//bytesToInt(value,0);
				byte [] realdata=new byte[length];
				reqBuf.readBytes(realdata);
				//System.arraycopy(sportdata,pos,value,0,length);
				sportDevValue.add(String.valueOf(value));///byte
				//pos=pos+length;
			}
		}

		request.setPatientDetail(patientDetail);
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
			short length=resBuf.readUnsignedByte();
			response.setUserNameLength((byte)length);
			byte[] name=new byte[length];
			resBuf.readBytes(name);
			response.setUserName(toUtf8(name));
		}
		return response;
	}
	private AbstractProtocol decodeChangeKeyAckResponse(ByteBuf resBuf,byte commandType)
	{
		LoginAckResponse response=new LoginAckResponse(commandType);
		//response.setUserID(resBuf.readIntLE());

		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(resBuf.readByte());
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
		//response.setDeviceType(resBuf.readByte());
		//response.setUserID(resBuf.readIntLE());
		//response.setRequestID(resBuf.readByte());
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
				byte [] deviceNumber=new byte[8];
				resBuf.readBytes(deviceNumber);
				//System.out.println(ByteBufUtil.hexDump(deviceNumber)+"number");
				patient.setDeviceNumber(ByteBufUtil.hexDump(deviceNumber));
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
				byte [] deviceNumber=new byte[8];
				resBuf.readBytes(deviceNumber);
				//System.out.println(ByteBufUtil.hexDump(deviceNumber)+"number");
				patient.setDeviceNumber(ByteBufUtil.hexDump(deviceNumber));
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
	private AbstractProtocol decodePatientListNewDataResponse(ByteBuf resBuf,byte commandType)
	{
		PatientListResponse response=new PatientListResponse(commandType);
		//response.setDeviceType(resBuf.readByte());
		//response.setUserID(resBuf.readIntLE());
		//response.setRequestID(resBuf.readByte());
		response.setState(PATIENT_LIST_SUCCESS);
		//if(response.getState()!=PATIENT_LIST_SUCCESS)
		//	return response;

		response.setPatientNumber(resBuf.readShortLE());
		int number=response.getPatientNumber();
		List<Patient> patientList=new ArrayList<>();
		System.out.println(String.valueOf(number)+"数量");
		for(short i=0;i<number;i++)
		{
			Patient patient=new Patient();

			patient.setPatientID(resBuf.readIntLE());
			System.out.println(patient.getPatientID()+"病人id");
			//if(commandType==PATIENT_LIST_DATA_RESPONSE)
			//{
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

			//}
			patient.setPhyConnectState(resBuf.readByte());
			if(patient.getPhyConnectState()==PHY_CONN_ONLINE)
			{
				patient.setMonConnectState(resBuf.readByte());
				System.out.println(1);
			}
			patient.setSportState(resBuf.readByte());
			if(patient.getSportState()!=SPORT_DEV_CONNECT_OFFLINE)
			{
				//System.out.println(2);
				byte [] deviceNumber=new byte[8];
				resBuf.readBytes(deviceNumber);
				//System.out.println(ByteBufUtil.hexDump(deviceNumber)+"number");
				patient.setDeviceNumber(ByteBufUtil.hexDump(deviceNumber));
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
		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(resBuf.readByte());
		if(response.getState()!=SUCCESS)
			return response;
		response.setDevNumber(resBuf.readShortLE());

		short number=response.getDevNumber();
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
				//monitorDevForm.setMonitoringEquipmentTypeId(devType);
				monitorDevForm.setParameterId(resBuf.readByte());
				int length=resBuf.readUnsignedByte();
				byte[] chineseName =new byte[length];
				resBuf.readBytes(chineseName);
				monitorDevForm.setChineseName(toUtf8(chineseName));

				length=resBuf.readUnsignedByte();
				byte[] englishName = new byte[length];
				resBuf.readBytes( englishName);
				monitorDevForm.setEnglishName(toUtf8(englishName));

				length=resBuf.readUnsignedByte();
				byte[] unit = new byte[length];
				resBuf.readBytes(unit);
				monitorDevForm.setUnit(toUtf8(unit));
				System.out.println(monitorDevForm.getChineseName());
				System.out.println(monitorDevForm.getEnglishName());
				System.out.println(monitorDevForm.getUnit());
				monitorDevForm.setOrder(resBuf.readByte());
				monitorDevForm.setLength(resBuf.readByte());
				System.out.println(monitorDevForm.getLength());
				monitorDevForm.setPosition(resBuf.readByte());
				monitorDevForms.add(monitorDevForm);
			}
			Collections.sort(monitorDevForms,Utils.monitorDevComp);//按照序号排序
			devData.put(devType,monitorDevForms);
		}
		response.setDevData(devData);
		return response;
	}
	private AbstractProtocol decodeSportDevFormResponse(ByteBuf resBuf,byte commandType)
	{
		SportDevFormResponse response=new SportDevFormResponse(commandType);
		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(resBuf.readByte());
		if(response.getState()!=SUCCESS)
			return response;

		response.setDevNumber(resBuf.readShortLE());
		short number=response.getDevNumber();
		HashMap<Byte,List<SportDevForm>> devData=new HashMap<>();
		for(byte i=0;i<number;i++)
		{
			byte devType=resBuf.readByte();
			//not have length

			int length=resBuf.readUnsignedByte();
			byte[] deviceNameBytes =new byte[length];
			resBuf.readBytes(deviceNameBytes);
			byte paraNumber=resBuf.readByte();
			List<SportDevForm> sportDevForms=new ArrayList<>();
			if(paraNumber>0)
			{
				for(byte j=0;j<paraNumber;j++)
				{
					SportDevForm sportDevForm=new SportDevForm();
					sportDevForm.setParameterId(resBuf.readIntLE());
					sportDevForm.setParaType(resBuf.readByte());
					sportDevForm.setDeviceName(toUtf8(deviceNameBytes));
					System.out.println(sportDevForm.getDeviceName());

					length=resBuf.readUnsignedByte();
					byte[] chineseName =new byte[length];
					resBuf.readBytes(chineseName);
					sportDevForm.setChineseName(toUtf8(chineseName));
					System.out.println(sportDevForm.getChineseName());

					length=resBuf.readUnsignedByte();
					byte[] englishName = new byte[length];
					resBuf.readBytes( englishName);
					sportDevForm.setEnglishName(toUtf8(englishName));

					length=resBuf.readUnsignedByte();
					byte[] unit = new byte[length];
					resBuf.readBytes(unit);
					sportDevForm.setUnit(toUtf8(unit));

					sportDevForm.setPrecision(resBuf.readDouble());
					sportDevForm.setCanControl(resBuf.readBoolean());
					if(sportDevForm.getCanControl())
					{
						length=resBuf.readUnsignedByte();
						byte[] parameterCode=new byte[length];
						resBuf.readBytes(parameterCode);
						sportDevForm.setParameterCode(toUtf8(parameterCode));
					}
					sportDevForm.setUpOrder(resBuf.readByte());
					sportDevForm.setMainControl(resBuf.readBoolean());
					sportDevForm.setLength(resBuf.readByte());
					//sportDevForm.setPosition(resBuf.readByte());
					sportDevForm.setRate(resBuf.readDouble());

					sportDevForms.add(sportDevForm);
				}

			}
			Collections.sort(sportDevForms,Utils.sportDevComp);
			devData.put(devType,sportDevForms);
		}
		response.setDevData(devData);

		return response;
	}

	private AbstractProtocol  decodeSportDevControlResponse(ByteBuf resBuf,byte commandType)
	{
		SportDevControlResponse response=new SportDevControlResponse(commandType);
		response.setDeviceType(resBuf.readByte());
		response.setUserID(resBuf.readIntLE());
		response.setRequestID(resBuf.readByte());
		response.setState(resBuf.readByte());

		byte [] deviceNumber=new byte[8];
		resBuf.readBytes(deviceNumber);
		response.setDeviceID(ByteBufUtil.hexDump(deviceNumber));
		response.setParameterCode(resBuf.readByte());
		response.setParaType(resBuf.readByte());
		response.setControlResultCode(resBuf.readByte());
		return  response;
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

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ((src[offset] & 0xFF)
				| ((src[offset+1] & 0xFF)<<8)
				| ((src[offset+2] & 0xFF)<<16)
				| ((src[offset+3] & 0xFF)<<24));
		return value;
	}
}
