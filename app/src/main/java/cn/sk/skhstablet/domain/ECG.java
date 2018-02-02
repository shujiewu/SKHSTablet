package cn.sk.skhstablet.domain;

//心电
public class ECG {
	private short[] content;

	public ECG(int length) {
		this.content = new short[length];
	}

	public short[] getContent() {
		return content;
	}

	public void setContent(short[] content) {
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
