package we.sharediary.ui;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.liuguangqiang.ipicker.IPicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import we.sharediary.R;
import we.sharediary.base.BaseActivity;
import we.sharediary.base.Constants;
import we.sharediary.mvp.presenter.MainPresenter;
import we.sharediary.mvp.view.IMainView;
import we.sharediary.result.CityInfoResult;
import we.sharediary.result.RefreshEvent;
import we.sharediary.table.WEDiary;
import we.sharediary.table.WEUser;
import we.sharediary.util.BusProviderUtil;
import we.sharediary.util.DialogUtils;
import we.sharediary.util.Util;

import static android.support.design.widget.Snackbar.make;

public class AddDiaryActivity extends BaseActivity implements View.OnClickListener, IMainView {

    private EditText etConent;
    private SimpleDraweeView ivOpen;
    private ImageView ivAdd;
    private ScrollView root;
    private MainPresenter mPresenter;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String imgPath;
    private BmobFile mBmobFile;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.diary_submit));//设置Toolbar标题
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etConent = (EditText) findViewById(R.id.et_content);
        ivOpen = (SimpleDraweeView) findViewById(R.id.iv_select);
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        root = (ScrollView) findViewById(R.id.activity_add_diary);
        FrameLayout flLayout = (FrameLayout) findViewById(R.id.fl_layout);
        controlKeyboardLayout(root, flLayout);
    }

    private void initData() {
        mDialog = DialogUtils.getProgressBar(this, false);
        mPresenter = new MainPresenter(this);
        mPresenter.getWeatherInfo();
        ivAdd.setOnClickListener(this);
        IPicker.setOnSelectedListener(new IPicker.OnSelectedListener() {
            @Override
            public void onSelected(List<String> paths) {
                if (paths != null && paths.size() > 0) {
                    imgPath = paths.get(0);
                    mBmobFile = new BmobFile(new File(imgPath));
                    ivOpen.setImageURI(Uri.parse("file://" + imgPath));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                IPicker.open(AddDiaryActivity.this);
                IPicker.setLimit(1);
                break;
        }
    }

    /**
     * 写日记
     */
    private void writeDiary() {
        //没有图片
        if (Util.isBlank(imgPath)) {
            WEDiary diary = new WEDiary();
            initDiary(diary);
            saveDiary(diary);
        }else {
            mDialog.show();
            mBmobFile = new BmobFile(new File(imgPath));
            mBmobFile.upload(AddDiaryActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    mDialog.cancel();
                    WEDiary diary = new WEDiary();
                    initDiary(diary);
                    diary.setAttachment(mBmobFile);
                    saveDiary(diary);
                }

                @Override
                public void onFailure(int i, String s) {
                    Snackbar.make(etConent, i + s, Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }

    /**
     * 插入数据库
     */
    private void saveDiary(WEDiary diary) {
        mDialog.show();
        diary.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                mDialog.cancel();
                BusProviderUtil.getBusInstance().post(new RefreshEvent(Constants.REFRESH_CODE));
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                mDialog.cancel();
                Snackbar.make(etConent, i + s, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 初始化已写的日记的数据
     *
     * @param diary
     */
    private void initDiary(WEDiary diary) {
        WEUser user = new WEUser();
        String userid = (String) readPreferences(Constants.USER_OBJECTID, 0);
        String phone = (String) readPreferences(Constants.USER_PHONE, 0);
        user.setObjectId(userid);
        diary.setAuthor(user);
        diary.setPhone(phone);
        diary.setContent(etConent.getText().toString().trim());
        String date = mDateFormat.format(new Date());
        diary.setDate(date);
        diary.setLowtemp((String) readPreferences(Constants.LOW_TEMP, 0));
        diary.setToptemp((String) readPreferences(Constants.HIGH_TEMP, 0));
        diary.setWeather((String) readPreferences(Constants.WEATHER, 0));
        try {
            if (diary.getWeather().contains("雨")){
                diary.setLovetip("下班时分有雨，记得带把伞哦~");
            }else if (diary.getWeather().contains("晴")){
                diary.setLovetip("出来晒晒太阳哦~");
            }else if (diary.getWeather().contains("阴")){
                diary.setLovetip("可能会下雨，下班赶紧回家哦~");
            }else if (diary.getWeather().contains("多云")){
                diary.setLovetip("抬头看下云彩，奇迹可能就在眼前哦~");
            }
        }catch (NullPointerException e){
            diary.setLovetip("美好的一天，请自信一点，开心一点~");
        }
    }

    @Override
    public void loadView(CityInfoResult result) {
        if (result != null && result.getRetData() != null) {
            CityInfoResult.RetDataBean data = result.getRetData();
            writePreferences(Constants.LOW_TEMP, data.getL_tmp());
            writePreferences(Constants.HIGH_TEMP, data.getH_tmp());
            writePreferences(Constants.WEATHER, data.getWeather());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_submit:
                if (Util.isBlank(imgPath) || Util.isBlank(etConent.getText().toString())) {
                    make(etConent, "图片和日记不能不写吧！！！", Snackbar.LENGTH_SHORT).show();
                }else {
                    writeDiary();
                }
                break;
        }
        return true;
    }

    /**
     * @param root 最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                    root.scrollTo(0, srollHeight);
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }
}
