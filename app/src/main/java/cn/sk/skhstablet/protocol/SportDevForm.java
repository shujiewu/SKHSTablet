package cn.sk.skhstablet.protocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class SportDevForm {
    /*private byte length;
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
    }*/
    private int parameterId;
    private byte deviceTypeId;
    private String deviceName;
    private String chineseName;
    private String englishName;
    private String unit;
    private Double precision;
    private Boolean canControl;
    private int upOrder;
    private String parameterCode;
    private Boolean mainControl;
    private int length;
    private byte paraType;

    public byte getParaType() {
        return paraType;
    }

    public void setParaType(byte paraType) {
        this.paraType = paraType;
    }

    private Double rate;

    public int getDeviceTypeIdByte() {
        return parameterId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }



    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public int getParameterId() {
        return parameterId;
    }

    public void setParameterId(int parameterId) {
        this.parameterId = parameterId;
    }

    public byte getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(byte deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
    }

    public Boolean getCanControl() {
        return canControl;
    }

    public void setCanControl(Boolean canControl) {
        this.canControl = canControl;
    }

    public int getUpOrder() {
        return upOrder;
    }

    public void setUpOrder(int upOrder) {
        this.upOrder = upOrder;
    }

    public String getParameterCode() {
        return parameterCode;
    }

    public void setParameterCode(String parameterCode) {
        this.parameterCode = parameterCode;
    }

    public Boolean getMainControl() {
        return mainControl;
    }

    public void setMainControl(Boolean mainControl) {
        this.mainControl = mainControl;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
