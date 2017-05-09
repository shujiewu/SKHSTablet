package cn.sk.skhstablet.protocol.up;

import java.util.List;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class MutiMonitorRequest extends AbstractProtocol {
    protected MutiMonitorRequest(byte command) {
        super(command);
    }

    private byte deviceType;
    private int userID;
    private byte requestID;
    private short patientNumber;
    private List<Integer> patientID;
}
