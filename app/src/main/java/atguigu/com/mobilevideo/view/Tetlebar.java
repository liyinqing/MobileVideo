package atguigu.com.mobilevideo.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.activity.SearchActivity;

/**
 * 作者：李银庆 on 2017/5/21 11:23
 */
public class Tetlebar extends LinearLayout implements View.OnClickListener {

    private final Context context;
    private TextView tv_sousuo;
    private RelativeLayout rl_game;
    private ImageView iv_record;

    public Tetlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_sousuo = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_record = (ImageView) getChildAt(3);

        tv_sousuo.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sousuo :
//                Toast.makeText(context, "全网搜素", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context,SearchActivity.class));
                break;
            case R.id.rl_game :
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record :
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
