package we.sharediary.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import we.sharediary.R;
import we.sharediary.base.BaseActivity;
import we.sharediary.util.Util;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout llDot;
    private ViewPager mViewPager;
    private List<View> mViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        initData();
    }

    private void initView(){
        llDot = (LinearLayout) findViewById(R.id.ll_dot);
        mViewPager = (ViewPager) findViewById(R.id.vp_pager);
    }
    private void initData(){
        mViewList = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.activity_welcome_view1, null);
        View view2 = inflater.inflate(R.layout.activity_welcome_view2, null);
        ImageView welcomeImage1 = (ImageView) view1.findViewById(R.id.iv_content);
        ImageView welcomeImage2 = (ImageView) view2.findViewById(R.id.iv_content);
        loadImage(welcomeImage1, "http://fourbeautiful.cn/data/welcome1.jpg");
        loadImage(welcomeImage2, "http://fourbeautiful.cn/data/welcome2.jpg");
        mViewList.add(view1);
        mViewList.add(view2);
        WelcomePageAdapter adapter = new WelcomePageAdapter(this);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                initDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Button btnStart = (Button) view2.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        initDot(0);
    }

    private void initDot(int position){
        llDot.removeAllViews();
        int width = Util.dip2px(this, 8);
        int height = Util.dip2px(this, 8);
        for (int i = 0; i < mViewList.size(); i++) {
            ImageView dot = new ImageView(this);
            dot.setScaleType(ImageView.ScaleType.FIT_XY);
            if (position == i){
                dot.setImageResource(R.drawable.shape_dot_red);
            }else {
                dot.setImageResource(R.drawable.shape_dot_white);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.setMargins(0,0,width*2,0);
            dot.setLayoutParams(params);
            llDot.addView(dot);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                Intent intent = new Intent(WelcomeActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    static class WelcomePageAdapter extends PagerAdapter{

        private SoftReference<WelcomeActivity> refrence;
        private WelcomeActivity mActivity;

        public WelcomePageAdapter(WelcomeActivity activity) {
            refrence = new SoftReference<>(activity);
            mActivity = refrence.get();
        }

        @Override
        public int getCount() {
            return mActivity.mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mActivity.mViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mActivity.mViewList.get(position));
            return mActivity.mViewList.get(position);
        }
    }

    /**
     * 下载图片
     * @param url
     * @return
     */
    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 封装的加载图片
     * @param imageView
     * @param url
     */
    private void loadImage(ImageView imageView, String url){
        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(this, imageView);
        imageAsyncTask.execute(url);
    }

    /**
     * 自定义图片异步处理
     */
    static class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap>{

        private SoftReference<WelcomeActivity> mReference;
        private WelcomeActivity mWelcomeActivity;
        private ImageView mImageView;

        public ImageAsyncTask(WelcomeActivity activity, ImageView imageView) {
            mReference = new SoftReference<>(activity);
            mWelcomeActivity = mReference.get();
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return mWelcomeActivity.returnBitMap(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}
