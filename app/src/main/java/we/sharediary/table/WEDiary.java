package we.sharediary.table;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by zhanghao on 2016/11/20.
 */

public class WEDiary extends BmobObject {
    /**
     * 附件
     */
    private BmobFile attachment;
    /**
     * 内容
     */
    private String content;

    /**
     * 显示日期
     */
    private String date;

    /**
     * 最高温度
     */
    private String toptemp;

    /**
     * 最低温度
     */
    private String lowtemp;

    /**
     * 天气
     */
    private String weather;

    /**
     * 温馨提示
     */
    private String lovetip;
    /**
     * 作者
     */
    private WEUser author;

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BmobFile getAttachment() {
        return attachment;
    }

    public void setAttachment(BmobFile attachment) {
        this.attachment = attachment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLovetip() {
        return lovetip;
    }

    public void setLovetip(String lovetip) {
        this.lovetip = lovetip;
    }

    public String getToptemp() {
        return toptemp;
    }

    public void setToptemp(String toptemp) {
        this.toptemp = toptemp;
    }

    public String getLowtemp() {
        return lowtemp;
    }

    public void setLowtemp(String lowtemp) {
        this.lowtemp = lowtemp;
    }

    public WEUser getAuthor() {
        return author;
    }

    public void setAuthor(WEUser author) {
        this.author = author;
    }
}
