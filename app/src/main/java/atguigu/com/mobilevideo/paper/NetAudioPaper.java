package atguigu.com.mobilevideo.paper;

import android.view.View;
import android.widget.TextView;

import atguigu.com.mobilevideo.fargment.BaseFragment;

/**
 * 作者：李银庆 on 2017/5/21 12:03
 */
public class NetAudioPaper extends BaseFragment {
    private TextView tv;
    @Override
    public View initView() {
        tv = new TextView(context);
        return tv;
    }

    @Override
    public void initData() {
        super.initData();
        tv.setText("网络音乐");
    }
}
