package atguigu.com.mobilevideo.paper;

import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.fargment.BaseFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者：李银庆 on 2017/5/28 16:39
 */
public class RecyclerPaper extends BaseFragment {
    @InjectView(R.id.lv)
    ListView lv;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.tv_content)
    TextView tvContent;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.recycler_item, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
