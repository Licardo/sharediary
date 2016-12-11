package we.sharediary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
        mViewList.add(view1);
        mViewList.add(view2);
        WelcomePageAdapter adapter = new WelcomePageAdapter();
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

    class WelcomePageAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
    }
}
