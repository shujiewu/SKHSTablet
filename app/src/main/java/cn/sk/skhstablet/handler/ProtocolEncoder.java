package cn.sk.skhstablet.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.DeviceId;
import cn.sk.skhstablet.protocol.Version;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.protocol.up.ChangeKeyRequest;
import cn.sk.skhstablet.protocol.up.DevNameRequest;
import cn.sk.skhstablet.protocol.up.LoginRequest;
import cn.sk.skhstablet.protocol.up.LogoutRequest;
import cn.sk.skhstablet.protocol.up.MonitorDevFormRequest;
import cn.sk.skhstablet.protocol.up.MutiMonitorRequest;
import cn.sk.skhstablet.protocol.up.PatientListRequest;
import cn.sk.skhstablet.protocol.up.SingleMonitorRequest;
import cn.sk.skhstablet.protocol.up.SportDevFormRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class ProtocolEncoder extends MessageToByteEncoder<AbstractProtocol> {

	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractProtocol msg, ByteBuf out) throws Exception {
		out.writeBytes(msg.getStart());
		int startIndex = out.writerIndex();
		out.writeShortLE(1);
		Version version=new Version();
		version.majorVersionNumber=1;
		version.secondVersionNumber=2;
		version.reviseVersionNumber=3;
		encodeVersion(version, out);
		out.writeByte(msg.getCommand());
		//encodeDeviceId(msg.getDeviceId(), out);
		AbstractProtocol request = (AbstractProtocol) msg;
		/*if (msg instanceof AbstractProtocol) {
			response = (AbstractProtocol) msg;
			out.writeByte(response.getStatus());
		}*/
		switch (msg.getCommand()) {
		case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_RESPONSE: {
			encodeExercisePhysiologicalDataResponse((ExercisePhysiologicalDataResponse) request, out);
			break;
		}
		case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_RESPONSE: {
			encodeExerciseEquipmentDataResponse((ExerciseEquipmentDataResponse)request, out);
			break;
		}

			case CommandTypeConstant.LOGIN_REQUEST: {
				encodeLoginRequest((LoginRequest)request, out);
				break;
			}

			case CommandTypeConstant.CHANGE_KEY_REQUEST:{
                encodeChangeKeyRequest((ChangeKeyRequest) request,out);
                break;
			}
            case CommandTypeConstant.LOGOUT_REQUEST:{
                encodeLogoutRequest((LogoutRequest) request, out);
                break;
            }

            case CommandTypeConstant.DEV_NAME_REQUEST:{
                encodeDevNameRequest((DevNameRequest) request,out);
                break;
            }
            case CommandTypeConstant.SPORT_DEV_FORM_REQUEST:{
                encodeSportDevFormRequest((SportDevFormRequest) request, out);
                break;
            }
            case CommandTypeConstant.MONITOR_DEV_FORM_REQUEST:{
                encodeMonitorDevFormRequest((MonitorDevFormRequest) request, out);
            }

            case CommandTypeConstant.PATIENT_LIST_REQUEST:{
                encodePatientListRequest((PatientListRequest) request, out);
                break;
            }
            case CommandTypeConstant.SINGLE_MONITOR_REQUEST:{
                encodeSingleMonitorRequest((SingleMonitorRequest) request,out);
                break;
            }
			case CommandTypeConstant.MUTI_MONITOR_REQUEST: {
				encodeMutiMonitorRequest((MutiMonitorRequest)request, out);
				break;
			}
		}
		out.writeShortLE(0);
		out.setShortLE(startIndex, out.writerIndex() - startIndex - 2);
		accumulation(out);
		// System.out.println("response :" + ByteBufUtil.hexDump(out));
		// System.out.println("response type:" + response.getCommand());
		// System.out.println("response length:" + (out.writerIndex() -
		// startIndex - 2));
	}

	/**
	 * 计算累加和
	 **/
	private void accumulation(ByteBuf out) {
		long start = System.currentTimeMillis();
		int sumIndex = out.writerIndex() - 2;
		int sum = 0;
		for (int i = 4; i < sumIndex; i++) {
			sum += out.getUnsignedByte(i);
		}
		out.setShortLE(sumIndex, sum);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	private void outWrite(Integer length, ByteBuf out, Integer value) {
		switch (length) {
		case 1: {
			if (null != value)
				out.writeByte(value);
			else
				out.writeByte(0);
			break;
		}
		case 2: {
			if (null != value)
				out.writeShortLE(value);
			else
				out.writeByte(0);
			break;
		}
		case 4: {
			if (null != value)
				out.writeIntLE(value);
			else
				out.writeByte(0);
			break;
		}
		}
	}


	private void encodeVersion(Version version, ByteBuf out) {
		out.writeByte(version.majorVersionNumber);
		out.writeByte(version.secondVersionNumber);
		out.writeShortLE(version.reviseVersionNumber);
	}


	private void encodeExerciseEquipmentDataResponse(ExerciseEquipmentDataResponse response, ByteBuf out) {
		out.writeIntLE((int) response.getDataPacketNumber());
	}

	private void encodeExercisePhysiologicalDataResponse(ExercisePhysiologicalDataResponse response, ByteBuf out) {
		out.writeIntLE((int) response.getDataPacketNumber());
	}

	private void encodeDeviceId(DeviceId deviceId, ByteBuf out) {
		out.writeByte(deviceId.deviceType);
		out.writeByte(deviceId.deviceModel);
		out.writeLongLE(deviceId.deviceNumber);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	private void encodeLoginRequest(LoginRequest request,ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
		out.writeBytes(request.getUserKey());
	}
	private void encodeLogoutRequest(LogoutRequest request,ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
	}
	private void encodeChangeKeyRequest(ChangeKeyRequest request,ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
		out.writeBytes(request.getUserOldKey());
		out.writeBytes(request.getUserNewKey());
	}
	private void encodePatientListRequest(PatientListRequest request,ByteBuf out)
	{
		out.writeByte(request.getDeviceType());
		out.writeIntLE(request.getUserID());
		out.writeByte(request.getRequestID());
	}
	private void encodeMutiMonitorRequest(MutiMonitorRequest request,ByteBuf out)
	{
		out.writeByte(request.getDeviceType());
		out.writeIntLE(request.getUserID());
		out.writeByte(request.getRequestID());
		out.writeShortLE(request.getPatientNumber());
		for (Integer patientID:request.getPatientID())
		{
			out.writeIntLE(patientID);
		}
	}

	private void encodeSingleMonitorRequest(SingleMonitorRequest request, ByteBuf out)
	{
		out.writeByte(request.getDeviceType());
		out.writeIntLE(request.getUserID());
		out.writeByte(request.getRequestID());
		out.writeShortLE(request.getPatientNumber());
		out.writeIntLE(request.getPatientID());
	}

	private void encodeDevNameRequest(DevNameRequest request, ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
	}
	private void encodeSportDevFormRequest(SportDevFormRequest request, ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
	}
	private void encodeMonitorDevFormRequest(MonitorDevFormRequest request, ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
	}

}
