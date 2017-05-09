package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class PatientListRequest extends AbstractProtocol {
    protected PatientListRequest(byte command) {
        super(command);
    }

    private byte deviceType;
    private int userID;
    private byte requestID;
}
