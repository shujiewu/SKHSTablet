package cn.sk.skhstablet.protocol;

import java.math.BigInteger;

public class DeviceId {
	public byte deviceType;
	public byte deviceModel;
	public String deviceNumber;

	public DeviceId(byte deviceType, byte deviceModel, String deviceNumber) {
		this.deviceType = deviceType;
		this.deviceModel = deviceModel;
		this.deviceNumber = deviceNumber;
	}

	public DeviceId() {
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (null == obj || !(obj instanceof DeviceId))
//			return false;
//		DeviceId target = (DeviceId) obj;
//		if (this.deviceModel == target.deviceModel && this.deviceType == target.deviceType
//				&& this.deviceNumber == target.deviceNumber) {
//			return true;
//		}
//		return false;
//	}

	public int getDeviceType() {
		if (deviceType < 0) {
			return 256 + deviceType;
		} else
			return deviceType;
	}

	@Override
	public String toString() {
		return "DeviceId [deviceType=" + deviceType + ", deviceModel=" + deviceModel + ", deviceNumber=" + deviceNumber
				+ "]";
	}

	public int getDeviceModel() {
		if (deviceModel < 0) {
			return 256 + deviceModel;
		} else
			return deviceModel;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = (byte) deviceType;
	}

	public void setDeviceModel(int deviceModel) {
		this.deviceModel = (byte) deviceModel;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	// public static void main(String[] args) {
	// System.out.println(new DeviceId().equals(1));
	// System.out.println(new DeviceId().equals(null));
	// DeviceId d1 = new DeviceId();
	// d1.deviceType = 0x00;
	// d1.deviceModel=0x01;
	// DeviceId d2 = new DeviceId();
	// d2.deviceType = 0x00;
	// d2.deviceModel=0x02;
	// System.out.println(d1.equals(d2));
	// }

}
