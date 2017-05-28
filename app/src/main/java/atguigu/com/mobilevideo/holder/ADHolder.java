package atguigu.com.mobilevideo.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import atguigu.com.mobilevideo.R;

/**
 * 作者：李银庆 on 2017/5/28 10:19
 */
public class ADHolder {
    TextView tvContext;
    ImageView ivImageIcon;
    Button btnInstall;

    public ADHolder(View convertView) {
        //中间公共部分 -所有的都有
        tvContext = (TextView) convertView.findViewById(R.id.tv_context);
        btnInstall = (Button) convertView.findViewById(R.id.btn_install);
        ivImageIcon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
    }
}
