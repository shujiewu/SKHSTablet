package cn.sk.skhstablet.protocol;

/**
 * Created by ldkobe on 2017/5/22.
 * 控制命令响应，因为控制命令的返回包括了服务器给的结果响应和设备给的结果响应
 */

public class ControlState {
    byte resultState;
    byte controlState;
    public ControlState(byte resultState,byte controlState)
    {
        this.resultState=resultState;
        this.controlState=controlState;
    }
    public byte getControlState() {
        return controlState;
    }

    public byte getResultState() {
        return resultState;
    }

    public void setControlState(byte controlState) {
        this.controlState = controlState;
    }

    public void setResultState(byte resultState) {
        this.resultState = resultState;
    }
}
