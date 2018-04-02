package com.intexh.news.moudle.mine.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProtacolActivity extends AppBaseActivity {

    @BindView(R.id.content_tv)
    TextView contentTv;
    String content;
    @BindView(R.id.twinkle)
    TwinklingRefreshLayout twinkle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_protacol;
    }

    @Override
    public void initView() {
        super.initView();
        twinkle.setPureScrollModeOn();
        content = getIntent().getStringExtra("content");
        contentTv.setText(content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_iv)
    public void onViewClicked() {
        this.finish();
    }
}
