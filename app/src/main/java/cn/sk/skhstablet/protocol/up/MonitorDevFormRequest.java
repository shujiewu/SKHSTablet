package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class MonitorDevFormRequest extends AbstractProtocol {
    protected MonitorDevFormRequest(byte command) {
        super(command);
    }

    private int userID;
}
