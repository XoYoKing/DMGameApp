package com.stx.xhb.dmgameapp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.stx.core.base.BaseFragment;
import com.stx.core.widget.CustomRefreshListview;
import com.stx.core.widget.dialog.DialogMaker;
import com.stx.xhb.dmgameapp.R;
import com.stx.xhb.dmgameapp.adapter.GameVideoListAdapter;
import com.stx.xhb.dmgameapp.entity.GameVideoEntity;
import com.stx.xhb.dmgameapp.presenter.video.getGameVideoContract;
import com.stx.xhb.dmgameapp.presenter.video.getGameVideoListImpl;
import com.stx.xhb.dmgameapp.utils.ToastUtil;
import com.stx.xhb.dmgameapp.widget.CustomLoadMoreView;

import butterknife.Bind;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

/**
 * Author: Mr.xiao on 2017/9/18
 *
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe: 游戏详情视频
 */

public class GameVideoFragment extends BaseFragment implements getGameVideoContract.getVideoListView, CustomRefreshListview.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @Bind(R.id.id_stickynavlayout_innerscrollview)
    RecyclerView mRecyclerView;
    private String gameId;
    private String gameKey;
    private GameVideoListAdapter gameVideoListAdapter;

    public static GameVideoFragment newInstance(String id, String key) {
        Bundle bundle = new Bundle();
        GameVideoFragment fragment = new GameVideoFragment();
        bundle.putString("id", id);
        bundle.putString("key", key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_common_recyclerview;
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initData();
        setAdapter();
    }

    private void setAdapter() {
        gameVideoListAdapter = new GameVideoListAdapter(getActivity());
        gameVideoListAdapter.setOnLoadMoreListener(this, mRecyclerView);
        gameVideoListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        gameVideoListAdapter.setLoadMoreView(new CustomLoadMoreView());
        mRecyclerView.setAdapter(gameVideoListAdapter);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("id")) {
                gameId = bundle.getString("id");
            }
            if (bundle.containsKey("key")) {
                gameKey = bundle.getString("key");
            }
        }
    }

    @Override
    protected Class getLogicClazz() {
        return getGameVideoContract.class;
    }

    @Override
    public void getVideoListSuccess(GameVideoEntity videoListEntity) {
        if (videoListEntity != null) {
            if (currentpage == 1) {
                gameVideoListAdapter.setNewData(videoListEntity.getHtml());
            } else {
                gameVideoListAdapter.addData(videoListEntity.getHtml());
            }
            if (videoListEntity.getHtml().isEmpty()){
                gameVideoListAdapter.setEmptyView(R.layout.view_empty);
            }
        }
    }

    @Override
    public void getVideoListFailed(String msg) {
        ToastUtil.show(msg);
        gameVideoListAdapter.loadMoreFail();
    }

    @Override
    protected void lazyLoad() {
        currentpage = 1;
        ((getGameVideoListImpl) mPresenter).getVideoList(gameId, gameKey, "3", currentpage);
    }

    @Override
    public void showLoading() {
        if (currentpage == 1) {
            DialogMaker.showProgressDialog(getActivity(), "正在加载...");
        }
    }

    @Override
    public void hideLoading() {
        if (currentpage == 1) {
            DialogMaker.dismissProgressDialog();
        }
        gameVideoListAdapter.loadMoreComplete();
    }

    @Override
    public void onLoadingMore() {
        currentpage++;
        ((getGameVideoListImpl) mPresenter).getVideoList(gameId, gameKey, "3", currentpage);
    }

    @Override
    public void onLoadMoreRequested() {
        if (gameVideoListAdapter.getData().size() < PAGE_SIZE) {
            gameVideoListAdapter.loadMoreEnd(true);
        } else {
            gameVideoListAdapter.loadMoreEnd(false);
        }
    }
}
