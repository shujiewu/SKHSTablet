package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LoginRequest extends AbstractProtocol {
    protected LoginRequest(byte command) {
        super(command);
    }

    private int userID;
    private byte[] userKey;
}
