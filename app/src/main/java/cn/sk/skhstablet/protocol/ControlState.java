package cn.sk.skhstablet.protocol;

/**
 * Created by ldkobe on 2017/5/22.
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
