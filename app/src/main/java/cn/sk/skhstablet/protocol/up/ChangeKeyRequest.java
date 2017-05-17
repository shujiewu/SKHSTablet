package cn.sk.skhstablet.protocol.up;

import cn.sk.skhstablet.protocol.AbstractProtocol;

/**
 * Created by wyb on 2017/5/9.
 */

public class ChangeKeyRequest extends AbstractProtocol {
    public ChangeKeyRequest(byte command) {
        super(command);
    }

    private String loginName;
    private String userOldKey;
    private String userNewKey;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserNewKey() {
        return userNewKey;
    }

    public String getUserOldKey() {
        return userOldKey;
    }

    public void setUserNewKey(String userNewKey) {
        this.userNewKey = userNewKey;
    }

    public void setUserOldKey(String userOldKey) {
        this.userOldKey = userOldKey;
    }
}
