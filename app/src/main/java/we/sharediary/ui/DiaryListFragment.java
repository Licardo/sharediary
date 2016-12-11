package we.sharediary.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sch.rfview.AnimRFRecyclerView;
import com.sch.rfview.manager.AnimRFLinearLayoutManager;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import we.sharediary.R;
import we.sharediary.adapter.DiaryListAdapter;
import we.sharediary.base.BaseFragment;
import we.sharediary.base.Constants;
import we.sharediary.listener.ClickNameListener;
import we.sharediary.result.RefreshEvent;
import we.sharediary.table.WEDiary;
import we.sharediary.table.WEUser;
import we.sharediary.util.BusProviderUtil;
import we.sharediary.util.DialogUtils;
import we.sharediary.util.StringUtils;

public class DiaryListFragment extends BaseFragment implements ClickNameListener{

    private AnimRFRecyclerView recyclerView;
    private List<WEDiary> mDiaryList;
    private DiaryListAdapter mAdapter;
    private ProgressDialog mDialog;
    private int loadCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_list, container, false);

        if (view instanceof AnimRFRecyclerView) {
            recyclerView = (AnimRFRecyclerView) view;
            initData();
        }
        return view;
    }

    private void initData(){
        mDialog = DialogUtils.getProgressBar(this.getActivity());
        BusProviderUtil.getBusInstance().register(this);
        mDiaryList = new ArrayList<>();
        mAdapter = new DiaryListAdapter(mDiaryList, this);
        recyclerView.setColor(Color.BLUE, Color.WHITE);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new AnimRFLinearLayoutManager(this.getActivity()));
        recyclerView.setLoadDataListener(new AnimRFRecyclerView.LoadDataListener() {
            @Override
            public void onRefresh() {
                loadCount = 0;
                getAllDiary();
            }

            @Override
            public void onLoadMore() {
                loadCount++;
                getAllDiary();

            }
        });
        recyclerView.setRefresh(true);
    }

    /**
     * 获取日记列表
     */
    private void getAllDiary(){
        String loverPhone = (String) readPreferences(Constants.LOVER_USER_PHONE, 0);
        String minePhone = (String) readPreferences(Constants.USER_PHONE, 0);
        BmobQuery<WEDiary> query = new BmobQuery();
        WEUser user = new WEUser();
        String objectid = (String) readPreferences(Constants.USER_OBJECTID, 0);
        user.setObjectId(objectid);
//        query.addWhereEqualTo("author", user);
        query.setSkip(loadCount*Constants.DEFAULT_SHOW_COUNT);
        query.setLimit(Constants.DEFAULT_SHOW_COUNT);
        if (!StringUtils.isEmpty(loverPhone)) {
            String[] values = {minePhone, loverPhone};
            query.addWhereContainedIn("phone", Arrays.asList(values));
        }else {
            query.addWhereEqualTo("phone", minePhone);
        }
        query.order("-updatedAt");
        query.include("author");
//        mDialog.show();
        query.findObjects(this.getActivity(), new FindListener<WEDiary>() {
            @Override
            public void onSuccess(List<WEDiary> list) {
//                mDialog.cancel();
                if (list != null) {
                    handleResult(list, loadCount==0);
                }
                if (loadCount==0) {
                    recyclerView.refreshComplate();
                }else {
                    recyclerView.loadMoreComplate();
                }
            }

            @Override
            public void onError(int i, String s) {
//                mDialog.cancel();
                Snackbar.make(recyclerView, i+"?"+s, Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scrollTop();
                        recyclerView.setRefresh(true);
                    }
                }).show();
            }
        });
    }

    private void handleResult(List<WEDiary> list, boolean isRefresh){
        if (isRefresh) {
            mDiaryList.clear();
        }
        if (list != null) {
            mDiaryList.addAll(list);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Subscribe
    public void refreshDialy(RefreshEvent event){
        if (event != null) {
            if (event.getCode() == Constants.REFRESH_CODE) {
                scrollTop();
                recyclerView.setRefresh(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProviderUtil.getBusInstance().unregister(this);
    }

    @Override
    public void onClickNameListener(WEUser user) {
        String userid = (String) readPreferences(Constants.USER_PHONE, 0);
        if (user.getMobilePhoneNumber().equals(userid)) {
            return;
        }
        mDialog.show();
        final BmobIMUserInfo userInfo = new BmobIMUserInfo();
        userInfo.setUserId(user.getObjectId());
        userInfo.setName(user.getUsername());
//        userInfo.setAvatar(user.getAvatar());
        BmobIM.getInstance().startPrivateConversation(userInfo, new ConversationListener() {
            @Override
            public void done(BmobIMConversation bmobIMConversation, BmobException e) {
                mDialog.cancel();
                if (e == null) {
                    //进入聊天界面
                    Intent intent = new Intent();
                    intent.setClass(DiaryListFragment.this.getActivity(), ChatActivity.class);
                    bmobIMConversation.setConversationTitle(userInfo.getName());
                    intent.putExtra(Constants.BMOB_CONVERSION, bmobIMConversation);
                    startActivity(intent);
                }else {
                    Snackbar.make(recyclerView, e.getErrorCode()+e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void refresh(){
        scrollTop();
        recyclerView.setRefresh(true);
    }

    /**
     * 先滑动到顶部再刷新
     */
    private void scrollTop(){
        recyclerView.smoothScrollToPosition(0);
        recyclerView.scrollToPosition(0);
    }
}
