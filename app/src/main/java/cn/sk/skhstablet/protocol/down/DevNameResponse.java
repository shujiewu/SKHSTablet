package cn.sk.skhstablet.protocol.down;

import java.util.HashMap;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class DevNameResponse extends AbstractProtocol {
    protected DevNameResponse(byte command) {
        super(command);
    }

    private int userID;
    private byte devNumber;
    private HashMap<Byte,String> devName;
}
