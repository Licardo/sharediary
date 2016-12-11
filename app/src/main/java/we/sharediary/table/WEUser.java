package we.sharediary.table;

import cn.bmob.v3.BmobUser;

/**
 * Created by zhanghao on 2016/11/21.
 */

public class WEUser extends BmobUser{
    public WEUser() {
        this.setTableName("_User");
    }

    private String token;
    private String userid;
    private String loverPhone;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLoverPhone() {
        return loverPhone;
    }

    public void setLoverPhone(String loverPhone) {
        this.loverPhone = loverPhone;
    }
}
