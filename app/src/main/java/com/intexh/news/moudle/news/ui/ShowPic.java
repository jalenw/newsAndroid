package com.intexh.news.moudle.news.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;
import com.intexh.news.R;
import com.intexh.news.base.AppBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangzhelin on 2018/3/27.
 */

public class ShowPic extends AppBaseActivity {
    @BindView(R.id.pv)
    PhotoView pv;

    @Override
    protected int getLayoutId() {
        return R.layout.show_pic;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        Intent intent=getIntent();
        if (intent!=null)
        {
            byte[] b = intent.getByteArrayExtra("bitmap");
            pv.setImageBitmap(Bytes2Bimap(b));
            pv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
}
