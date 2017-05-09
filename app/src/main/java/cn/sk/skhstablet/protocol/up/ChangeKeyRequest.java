package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class ChangeKeyRequest extends AbstractProtocol {
    protected ChangeKeyRequest(byte command) {
        super(command);
    }

    private int userID;
    private byte[] userOldKey;
    private byte[] userNewKey;
}
