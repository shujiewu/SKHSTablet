package cn.sk.skhstablet.protocol.down;

import java.util.List;

import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class PatientListResponse extends AbstractProtocol {
    protected PatientListResponse(byte command) {
        super(command);
    }
    private byte deviceType;
    private int userID;
    private short patientNumber;
    private List<Patient> patientList;
}
