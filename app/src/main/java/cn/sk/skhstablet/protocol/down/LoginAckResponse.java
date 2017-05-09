package cn.sk.skhstablet.protocol.down;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LoginAckResponse extends AbstractProtocol {
    protected LoginAckResponse(byte command) {
        super(command);
    }

    private int userID;
    private byte state;
}
