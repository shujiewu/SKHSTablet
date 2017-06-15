package cn.sk.skhstablet.protocol.down;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by ldkobe on 2017/6/10.
 */

public class LoginOtherResponse extends AbstractProtocol {
    public LoginOtherResponse(byte command) {
        super(command);
    }
    private byte state;
    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
