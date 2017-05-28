package atguigu.com.mobilevideo.holder;

import android.view.View;
import android.widget.TextView;

import atguigu.com.mobilevideo.R;
import atguigu.com.mobilevideo.domain.NetAudioInfo;

/**
 * 作者：李银庆 on 2017/5/28 10:13
 */
public class TextHolder extends BaseVideoHoder {
    TextView tvContext;

    public TextHolder(View convertView) {
        super(convertView);
        //中间公共部分 -所有的都有
        tvContext = (TextView) convertView.findViewById(R.id.tv_context);


    }

    public void setData(NetAudioInfo.ListBean mediaItem) {
        super.setData(mediaItem);
        //设置文本-所有的都有
        tvContext.setText(mediaItem.getText() + "_" + mediaItem.getType());
    }
}
