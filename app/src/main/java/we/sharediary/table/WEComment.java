package we.sharediary.table;

import cn.bmob.v3.BmobObject;

/**
 * Created by zhanghao on 2016/11/21.
 */

public class WEComment extends BmobObject{
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论人
     */
    private WEUser author;
    /**
     * 评论的帖子
     */
    private WEDiary post;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public WEUser getAuthor() {
        return author;
    }

    public void setAuthor(WEUser author) {
        this.author = author;
    }

    public WEDiary getPost() {
        return post;
    }

    public void setPost(WEDiary post) {
        this.post = post;
    }
}
