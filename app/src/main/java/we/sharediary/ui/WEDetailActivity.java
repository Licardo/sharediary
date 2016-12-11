package we.sharediary.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import we.sharediary.R;
import we.sharediary.adapter.WEDetailAdapter;
import we.sharediary.base.BaseActivity;
import we.sharediary.base.Constants;
import we.sharediary.table.WEComment;
import we.sharediary.table.WEDiary;
import we.sharediary.table.WEUser;
import we.sharediary.util.DialogUtils;
import we.sharediary.util.Util;

public class WEDetailActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView rvComment;
    private SimpleDraweeView ivImg;
    private TextView tvContent, tvDate, tvWriter;
    private Toolbar mToolbar;
    private EditText etContent;
    private Button btnSend;
    private WEDetailAdapter mAdapter;
    private List<WEComment> commentList;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private ProgressDialog mDialog;
    private WEUser mWEUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedetail);

        initView();
        initData();
    }

    private void initView(){
        rvComment = (RecyclerView) findViewById(R.id.rv_comment);
        ivImg = (SimpleDraweeView) findViewById(R.id.iv_img);
        tvContent = (TextView) findViewById(R.id.tv_diary_content);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvWriter = (TextView) findViewById(R.id.tv_writer);
        etContent = (EditText) findViewById(R.id.et_content);
        btnSend = (Button) findViewById(R.id.btn_send);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvWriter.setOnClickListener(this);
    }
    private void initData(){
        mDialog = DialogUtils.getProgressBar(this, false);
        WEDiary diary = (WEDiary) getIntent().getSerializableExtra(WEMainFragment.DIARY_CONTENT);
        mWEUser = diary.getAuthor();
        if (diary.getAttachment() != null) {
            ivImg.setImageURI(Uri.parse(diary.getAttachment().getUrl()));
        }
        tvContent.setText(diary.getContent());
        if (diary.getAuthor() != null) {
            tvWriter.setText(String.format(getString(R.string.writer_text), diary.getAuthor().getUsername()));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM.d", Locale.ENGLISH);
        try {
            tvDate.setText(dateFormat.format(format.parse(diary.getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        commentList = new ArrayList<>();
        mAdapter = new WEDetailAdapter(this, commentList);
        rvComment.setAdapter(mAdapter);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setFocusable(false);
        getCommentsList(diary.getObjectId());

        btnSend.setOnClickListener(this);
        btnSend.setTag(diary.getObjectId());
    }

    private void getCommentsList(String objectid){
        BmobQuery<WEComment> query = new BmobQuery();
        WEDiary diary = new WEDiary();
        diary.setObjectId(objectid);
        query.addWhereEqualTo("post", diary);
        query.include("post,author");
        mDialog.show();
        query.findObjects(this, new FindListener<WEComment>() {
            @Override
            public void onSuccess(List<WEComment> list) {
                mDialog.cancel();
                commentList.clear();
                commentList.addAll(list);
                mAdapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) etContent.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(etContent.getApplicationWindowToken( ) , 0);
                }

            }

            @Override
            public void onError(int i, String s) {
                mDialog.cancel();
                Snackbar.make(rvComment, i+s, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                if (btnSend.getTag() != null) {
                    submitComment((String) btnSend.getTag());
                }
                break;
            case R.id.tv_writer:
                String phone = (String) readPreferences(Constants.USER_PHONE, 0);
                if (mWEUser.getMobilePhoneNumber().equals(phone)) {
                    return;
                }
                final BmobIMUserInfo userInfo = new BmobIMUserInfo();
                userInfo.setUserId(mWEUser.getObjectId());
                userInfo.setName(mWEUser.getUsername());
                BmobIM.getInstance().startPrivateConversation(userInfo, new ConversationListener() {
                    @Override
                    public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                        if (e == null) {
                            //进入聊天界面
                            Intent intent = new Intent();
                            intent.setClass(WEDetailActivity.this, ChatActivity.class);
                            bmobIMConversation.setConversationTitle(userInfo.getName());
                            intent.putExtra(Constants.BMOB_CONVERSION, bmobIMConversation);
                            startActivity(intent);
                        }else {
                            Snackbar.make(etContent, e.getErrorCode()+e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
    }

    /**
     * 提交评论
     */
    private void submitComment(final String objectid){
        String content = etContent.getText().toString().trim();
        if (Util.isBlank(content)) {
            Snackbar.make(etContent, "写点评论内容吧...", Snackbar.LENGTH_SHORT).show();
            return;
        }
        WEComment comment = new WEComment();
        WEDiary diary = new WEDiary();
        WEUser user = new WEUser();
        diary.setObjectId(objectid);
        String userid = (String) readPreferences(Constants.USER_OBJECTID, 0);
        user.setObjectId(userid);
        comment.setPost(diary);
        comment.setAuthor(user);
        comment.setContent(content);
        mDialog.show();
        comment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                mDialog.cancel();
                etContent.setText("");
                getCommentsList(objectid);
            }

            @Override
            public void onFailure(int i, String s) {
                mDialog.cancel();
                Snackbar.make(mToolbar, i+s, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
