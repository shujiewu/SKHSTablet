package cn.sk.skhstablet.protocol;



public abstract class AbstractProtocol {
	// 协议字段index，0开始
	/**命令字段**/
	public static final int COMMAND_INDEX = 10;
	/**设备类型字段**/
	public static final int DEVICE_TYPE_INDEX = 11;
	/**设备型号字段**/
	public static final int DEVICE_MODEL_INDEX = 12;
	/**设备编号字段**/
	public static final int DEVICE_NUMBER_INDEX = 13;

	/** xxxx **/
	public final static byte[] start = new byte[] { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA };
	// 协议为两字节无符号数用int，为有符号数可用short
	/** 包长度 **/
	private int length;
	/** 协议版本 **/
	private Version version;
	/** 命令类型 **/
	private final byte command;
	/** 设备id **/
	private DeviceId deviceId;
	/** 累加和 **/
	private byte[] checksum = new byte[2];

	protected AbstractProtocol(byte command) {
		this.command = command;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getChecksum() {
		return checksum;
	}

	public void setChecksum(byte[] checksum) {
		this.checksum = checksum;
	}

	public byte getCommand() {
		return command;
	}

	public byte[] getStart() {
		return start;
	}

	public DeviceId getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(DeviceId deviceId) {
		this.deviceId = deviceId;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}


	protected byte deviceType;
	protected int userID;
	protected byte requestID;

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getUserID() {
		return userID;
	}

	public byte getDeviceType() {
		return deviceType;
	}

	public byte getRequestID() {
		return requestID;
	}

	public void setDeviceType(byte deviceType) {
		this.deviceType = deviceType;
	}

	public void setRequestID(byte requestID) {
		this.requestID = requestID;
	}

}
