package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class LogoutRequest extends AbstractProtocol {
    protected LogoutRequest(byte command) {
        super(command);
    }
    private int userID;

}
