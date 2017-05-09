package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class DevNameRequest extends AbstractProtocol {
    protected DevNameRequest(byte command) {
        super(command);
    }

    private int userID;
}
