package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by ldkobe on 2017/6/6.
 */

public class IdleHeartRequest extends AbstractProtocol {

    public IdleHeartRequest(byte command) {
        super(command);
    }
}
