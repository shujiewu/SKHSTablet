package cn.sk.skhstablet.handler;

import java.io.UnsupportedEncodingException;
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
import cn.sk.skhstablet.protocol.up.SportDevControlRequest;
import cn.sk.skhstablet.protocol.up.SportDevFormRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static java.lang.Long.MIN_VALUE;

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
		out.writeByte(msg.getDeviceType());
		out.writeIntLE(msg.getUserID());
		out.writeByte(msg.getRequestID());
		switch (msg.getCommand()) {
		/*case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_RESPONSE: {
			encodeExercisePhysiologicalDataResponse((ExercisePhysiologicalDataResponse) request, out);
			break;
		}
		case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_RESPONSE: {
			encodeExerciseEquipmentDataResponse((ExerciseEquipmentDataResponse)request, out);
			break;
		}*/

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

            /*case CommandTypeConstant.DEV_NAME_REQUEST:{
                encodeDevNameRequest((DevNameRequest) request,out);
                break;
            }*/
            case CommandTypeConstant.SPORT_DEV_FORM_REQUEST:{
                encodeSportDevFormRequest((SportDevFormRequest) request, out);
                break;
            }
            case CommandTypeConstant.MONITOR_DEV_FORM_REQUEST:{
                encodeMonitorDevFormRequest((MonitorDevFormRequest) request, out);
				break;
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
			case CommandTypeConstant.SPORT_DEV_CONTROL_REQUEST:{
				encodeSportDevControlRequest((SportDevControlRequest)request, out);
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
	private byte[] toBytes(String str)
	{
		byte[] srtbyte = null;
		try {
			srtbyte = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return srtbyte;
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

	/*private void encodeDeviceId(DeviceId deviceId, ByteBuf out) {
		out.writeByte(deviceId.deviceType);
		out.writeByte(deviceId.deviceModel);
		out.write(deviceId.deviceNumber);
	}*/

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	private void encodeLoginRequest(LoginRequest request,ByteBuf out)
	{
		byte[] name=toBytes(request.getLoginName());
		out.writeByte(name.length);
		out.writeBytes(name);

		byte[] key=toBytes(request.getLoginKey());
		out.writeByte(key.length);
		out.writeBytes(key);
	}
	private void encodeLogoutRequest(LogoutRequest request,ByteBuf out)
	{
		//out.writeIntLE(request.getUserID());
	}
	private void encodeChangeKeyRequest(ChangeKeyRequest request,ByteBuf out)
	{
		byte[] name=toBytes(request.getLoginName());
		out.writeByte(name.length);
		out.writeBytes(name);

		byte[] key=toBytes(request.getUserOldKey());
		out.writeByte(key.length);
		out.writeBytes(key);

		byte[] newkey=toBytes(request.getUserNewKey());
		out.writeByte(newkey.length);
		out.writeBytes(newkey);
	}
	private void encodePatientListRequest(PatientListRequest request,ByteBuf out)
	{
		//out.writeByte(request.getDeviceType());
		//out.writeIntLE(request.getUserID());
		//out.writeByte(request.getRequestID());
	}
	private void encodeMutiMonitorRequest(MutiMonitorRequest request,ByteBuf out)
	{
		//out.writeByte(request.getDeviceType());
		//out.writeIntLE(request.getUserID());
		//out.writeByte(request.getRequestID());
		out.writeShortLE(request.getPatientNumber());
		for (Integer patientID:request.getPatientID())
		{
			out.writeIntLE(patientID);
		}
	}

	private void encodeSingleMonitorRequest(SingleMonitorRequest request, ByteBuf out)
	{
		//out.writeByte(request.getDeviceType());
		//out.writeIntLE(request.getUserID());
		//out.writeByte(request.getRequestID());
		out.writeShortLE(request.getPatientNumber());
		if(request.getPatientNumber()!=0)
			out.writeIntLE(request.getPatientID());
	}

	/*private void encodeDevNameRequest(DevNameRequest request, ByteBuf out)
	{
		out.writeIntLE(request.getUserID());
	}*/
	private void encodeSportDevFormRequest(SportDevFormRequest request, ByteBuf out)
	{
		System.out.println("sport"+request.getUserID());
		//out.writeIntLE(request.getUserID());
	}
	private void encodeMonitorDevFormRequest(MonitorDevFormRequest request, ByteBuf out)
	{
		System.out.println("sport"+request.getUserID());
		//out.writeIntLE(request.getUserID());
	}
	private void encodeSportDevControlRequest(SportDevControlRequest request,  ByteBuf out) throws UnsupportedEncodingException {
		//byte [] deviceID=new;
		/*byte [] deviceID=new byte[8];
		for(int i=0;i<8;i++)
		{
			deviceID[i]=Byte.parseByte(request.getDeviceID().substring(i*2,i*2+2));//这里需要判断
			System.out.println(deviceID[i]);
		}*/
		long deviceNumber=parseUnsignedLong(request.getDeviceID(),16);


		/*byte[] bytes=request.getDeviceID().getBytes("US-ASCII");
		System.out.println(bytes.length);
		for(int i=bytes.length-1;i>=0;i--){
			bytes[i]-=(byte)'0';
		}
		for(int i=0;i<bytes.length-1;i++){
			System.out.print(bytes[i]);
		}*/
		//out.writeBytes(deviceID);
		out.writeLong(deviceNumber);
		out.writeByte(request.getParameterCode());
		out.writeByte(request.getParaType());
		//System.out.println(bytes);

		if(request.getParameterCode()!=CommandTypeConstant.SPORT_DEV_START_STOP)
			out.writeByte(request.getParaControlValue());

	}


	public static int compareUnsigned(long x, long y) {
		return compare(x + MIN_VALUE, y + MIN_VALUE);
	}
	public static int compare(long x, long y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}
	public static long parseUnsignedLong(String s, int radix)
			throws NumberFormatException {
		if (s == null)  {
			throw new NumberFormatException("null");
		}

		int len = s.length();
		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar == '-') {
				throw new
						NumberFormatException(String.format("Illegal leading minus sign " +
						"on unsigned string %s.", s));
			} else {
				if (len <= 12 || // Long.MAX_VALUE in Character.MAX_RADIX is 13 digits
						(radix == 10 && len <= 18) ) { // Long.MAX_VALUE in base 10 is 19 digits
					return Long.parseLong(s, radix);
				}

				// No need for range checks on len due to testing above.
				long first = Long.parseLong(s.substring(0, len - 1), radix);
				int second = Character.digit(s.charAt(len - 1), radix);
				if (second < 0) {
					throw new NumberFormatException("Bad digit at end of " + s);
				}
				long result = first * radix + second;
				if (compareUnsigned(result, first) < 0) {
                    /*
                     * The maximum unsigned value, (2^64)-1, takes at
                     * most one more digit to represent than the
                     * maximum signed value, (2^63)-1.  Therefore,
                     * parsing (len - 1) digits will be appropriately
                     * in-range of the signed parsing.  In other
                     * words, if parsing (len -1) digits overflows
                     * signed parsing, parsing len digits will
                     * certainly overflow unsigned parsing.
                     *
                     * The compareUnsigned check above catches
                     * situations where an unsigned overflow occurs
                     * incorporating the contribution of the final
                     * digit.
                     */
					throw new NumberFormatException(String.format("String value %s exceeds " +
							"range of unsigned long.", s));
				}
				return result;
			}
		}
		else
		{
			throw new NumberFormatException("Bad digit at end of " + s);
		}
	}
}
