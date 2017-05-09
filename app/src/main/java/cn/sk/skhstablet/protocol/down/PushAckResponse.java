package cn.sk.skhstablet.protocol.down;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class PushAckResponse  extends AbstractProtocol {
    protected PushAckResponse(byte command) {
        super(command);
    }

    private byte deviceType;
    private int userID;
    private byte requestID;
    private byte state;
}
