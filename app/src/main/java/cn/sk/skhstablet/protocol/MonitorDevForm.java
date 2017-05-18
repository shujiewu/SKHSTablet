package cn.sk.skhstablet.protocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class MonitorDevForm {

    /*private byte length;
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
    }*/



    private byte parameterId;
    private byte monitoringEquipmentTypeId;
    private String chineseName;
    private String englishName;
    private String unit;
    private int order;
    private int length;
    private int position;

    public byte getMonitoringEquipmentTypeIdByte() {
        return parameterId;
    }//(byte) parameterId.intValue();

    public  byte getParameterId() {
        return parameterId;
    }

    public void setParameterId( byte  parameterId) {
        this.parameterId = parameterId;
    }

    public  byte  getMonitoringEquipmentTypeId() {
        return monitoringEquipmentTypeId;
    }

    public void setMonitoringEquipmentTypeId( byte  monitoringEquipmentTypeId) {
        this.monitoringEquipmentTypeId = monitoringEquipmentTypeId;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
