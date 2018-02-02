package cn.sk.skhstablet.protocol.down;



import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.protocol.AbstractProtocol;

public class ExerciseEquipmentDataResponse extends AbstractProtocol {

	public ExerciseEquipmentDataResponse() {
		this(CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_RESPONSE);
	}

	private ExerciseEquipmentDataResponse(byte command) {
		super(command);
	}

	/** 病人RFID **/
	private long RFID;
	/** 数据库主键id **/
	private int patientId;
	/** 数据包号 **/
	private long dataPacketNumber;
	/** 生理参数字节数 **/
	private int physiologicalLength;
	/** 生理数据 **/
	private byte[] physiologicalData;
	/** 运动方案完成率 **/
	private short exercisePlanCompletionRate;
	/** 运动方案已执行量 **/
	private int performedExecutionAmount;
	/** 运动方案id **/
	private int exercisePlanId;
	/** 运动方案段号 **/
	private short exercisePlanSectionNumber;
	/** 设备数据 **/
	private byte[] equipmentData;
	



	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	// public byte[] getRFID() {
	// return RFID;
	// }
	//
	// public void setRFID(byte[] rFID) {
	// RFID = rFID;
	// }

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

	public byte[] getPhysiologicalData() {
		return physiologicalData;
	}

	public void setPhysiologicalData(byte[] physiologicalData) {
		this.physiologicalData = physiologicalData;
	}

	public short getExercisePlanCompletionRate() {
		return exercisePlanCompletionRate;
	}

	public void setExercisePlanCompletionRate(short exercisePlanCompletionRate) {
		this.exercisePlanCompletionRate = exercisePlanCompletionRate;
	}

	public int getPerformedExecutionAmount() {
		return performedExecutionAmount;
	}

	public void setPerformedExecutionAmount(int performedExecutionAmount) {
		this.performedExecutionAmount = performedExecutionAmount;
	}

	public int getExercisePlanId() {
		return exercisePlanId;
	}

	public void setExercisePlanId(int exercisePlanId) {
		this.exercisePlanId = exercisePlanId;
	}

	/*public void setReceiveDateTime(LocalDateTime receiveDateTime) {
		this.receiveDateTime = receiveDateTime;
	}
	//该时间为该对象被创建的时间，因为协议里没有任何时间信息，所以无法真正记录该运动设备数据在设备上产生的时间
	private LocalDateTime receiveDateTime = LocalDateTime.now();

	public LocalDateTime getReceiveDateTime() {
		return receiveDateTime;
	}*/

	public byte[] getEquipmentData() {
		return equipmentData;
	}

	public void setEquipmentData(byte[] equipmentData) {
		this.equipmentData = equipmentData;
	}

	public short getExercisePlanSectionNumber() {
		return exercisePlanSectionNumber;
	}

	public void setExercisePlanSectionNumber(short exercisePlanSectionNumber) {
		this.exercisePlanSectionNumber = exercisePlanSectionNumber;
	}

	public long getRFID() {
		return RFID;
	}

	public void setRFID(long rFID) {
		RFID = rFID;
	}

	private PatientDetail patientDetail;

	public PatientDetail getPatientDetail() {
		return patientDetail;
	}
	public void setPatientDetail(PatientDetail patientDetail) {
		this.patientDetail = patientDetail;
	}
}
