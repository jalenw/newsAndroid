package com.zjw.base.UI;

import android.view.KeyEvent;

/**
 * Created by Frank on 2017/3/25.
 */

public interface ActivityKeyDownListener {
    /**
     * Activity onKeyDown事件传递
     * @param keyCode
     * @param event
     * @return
     */
    boolean onKeyDown(int keyCode, KeyEvent event);
}
