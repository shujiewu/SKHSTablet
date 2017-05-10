package cn.sk.skhstablet.protocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class MonitorDevForm {

    private byte length;
    private String name;
    private String unit;

    public byte getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
