package cn.sk.skhstablet.handler;

import java.util.List;

import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.domain.ECG;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.DeviceId;
import cn.sk.skhstablet.protocol.Version;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

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
		System.out.println(version.reviseVersionNumber);
		byte commandType = reqBuf.readByte();
		DeviceId deviceId = decodeDeviceId(reqBuf);
		System.out.println(commandType);
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
		}
		request.setVersion(version);
		request.setDeviceId(deviceId);
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
}
