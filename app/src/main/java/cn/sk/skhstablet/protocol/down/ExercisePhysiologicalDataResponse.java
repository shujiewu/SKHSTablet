package cn.sk.skhstablet.protocol.down;

import java.util.HashMap;
import java.util.Map;


import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.domain.ECG;
import cn.sk.skhstablet.protocol.AbstractProtocol;

public class ExercisePhysiologicalDataResponse extends AbstractProtocol {

	public ExercisePhysiologicalDataResponse() {
		this(CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_REQUEST);
	}

	private ExercisePhysiologicalDataResponse(byte command) {
		super(command);
	}

	/** 数据包号 **/
	private long dataPacketNumber;
	/** 生理参数字节数 **/
	private int physiologicalLength;
	/** ecg ecg导联号->ecg **/
	private Map<Short, ECG> ecgs = new HashMap<>();
	/** 生理参数**/
	private byte[] physiologicalData;


	public long getDataPacketNumber() {
		return dataPacketNumber;
	}

	public void setDataPacketNumber(long dataPacketNumber) {
		this.dataPacketNumber = dataPacketNumber;
	}

	public int getPhysiologicalLength() {
		return physiologicalLength;
	}

	public void setPhysiologicalLength(int physiologicalLength) {
		this.physiologicalData = new byte[physiologicalLength];
		this.physiologicalLength = physiologicalLength;
	}

	public Map<Short, ECG> getEcgs() {
		return ecgs;
	}

	public void setEcgs(Map<Short, ECG> ecgs) {
		this.ecgs = ecgs;
	}

	//该时间为该对象被创建的时间，因为协议里没有任何时间信息，所以无法真正记录该运动设备数据在设备上产生的时间
	/*private LocalDateTime receiveDateTime = LocalDateTime.now();
	public LocalDateTime getReceiveDateTime() {
		return receiveDateTime;
	}
*/
	public byte[] getPhysiologicalData() {
		return physiologicalData;
	}

	public void setPhysiologicalData(byte[] physiologicalData) {
		this.physiologicalData = physiologicalData;
	}

}
