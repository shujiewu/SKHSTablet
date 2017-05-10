package cn.sk.skhstablet.protocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class SportDevForm {
    private byte length;
    private String name;
    private String unit;
    private byte isAdjust;
    private byte max;
    private byte min;
    private byte precision;
    private byte adjustCode;

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public String getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public byte getAdjustCode() {
        return adjustCode;
    }

    public byte getIsAdjust() {
        return isAdjust;
    }

    public byte getLength() {
        return length;
    }

    public byte getMax() {
        return max;
    }

    public byte getMin() {
        return min;
    }

    public byte getPrecision() {
        return precision;
    }

    public void setAdjustCode(byte adjustCode) {
        this.adjustCode = adjustCode;
    }

    public void setIsAdjust(byte isAdjust) {
        this.isAdjust = isAdjust;
    }

    public void setMax(byte max) {
        this.max = max;
    }

    public void setMin(byte min) {
        this.min = min;
    }

    public void setPrecision(byte precision) {
        this.precision = precision;
    }
}
