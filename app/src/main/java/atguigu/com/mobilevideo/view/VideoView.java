package atguigu.com.mobilevideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


public class VideoView extends android.widget.VideoView {

    /**
     * 带有两个参数的构造方法
     * 在布局文件中一定不能少
     * @param context
     * @param attrs
     */
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //保存测量的结果
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 设置视频的宽和高的方法
     * @param width
     * @param height
     */
    public void setVideoSize(int width,int height){
        //getLayoutParams()获得布局参数 。
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = width;
        l.height = height;
        setLayoutParams(l);
    }
}
