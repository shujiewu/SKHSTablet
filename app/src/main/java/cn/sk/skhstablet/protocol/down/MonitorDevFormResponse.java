package cn.sk.skhstablet.protocol.down;

import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.MonitorDevForm;

/**
 * Created by wyb on 2017/5/9.
 */

public class MonitorDevFormResponse extends AbstractProtocol {
    protected MonitorDevFormResponse(byte command) {
        super(command);
    }
    private int userID;
    private byte devNumber;
    private List<List<MonitorDevForm>> devData;
}
