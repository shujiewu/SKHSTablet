package cn.sk.skhstablet.domain;

public class ECG {
	private byte[] content;

	public ECG(int length) {
		this.content = new byte[length];
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	// public ECG createContent(short length) {
	// content = new byte[length];
	// return this;
	// }

	public int getLength() {
		return content.length;
	}

}
