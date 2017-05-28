package atguigu.com.mobilevideo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import atguigu.com.mobilevideo.Utils.DensityUtil;
import atguigu.com.mobilevideo.domain.Lyric;

/**
 * 作者：李银庆 on 2017/5/26 11:59
 */
public class LyricView extends TextView {


    private Paint paint;
    private Paint paintWhte;
    private long width;
    private long height;
    private ArrayList<Lyric> lyrics;
    private int index = 0;
    private float textHeight=20 ;
    private float currentPosition;
    private long sleepTime;
    private long timePoint;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView() {
       // lyrics = new ArrayList<>();
        textHeight = DensityUtil.dip2px(getContext(),20);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtil.dip2px(getContext(),16));
        paint.setTextAlign(Paint.Align.CENTER);

        paintWhte = new Paint();
        paintWhte.setColor(Color.WHITE);
        paintWhte.setAntiAlias(true);
        paintWhte.setTextSize(DensityUtil.dip2px(getContext(),16));
        paintWhte.setTextAlign(Paint.Align.CENTER);

//        //准备歌词
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 10000; i++) {
//            lyric.setContent("aaaaaa_" + i);
//            lyric.setTimePoint(2000*i);
//            lyric.setSleepTime(2000);
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }
    }

    /**
     * 绘制歌词
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {

            if(index != lyrics.size()-1){
                float push = 0;

                if (sleepTime == 0) {
                    push = 0;
                } else {
                    // 这一句花的时间： 这一句休眠时间  =  这一句要移动的距离：总距离(行高)
                    //这一句要移动的距离 = （这一句花的时间/这一句休眠时间） * 总距离(行高)
                    push = ((currentPosition - timePoint) / sleepTime) * textHeight;
                }
                canvas.translate(0, -push);
            }

            //有歌词
            //当前句-中心的哪一句
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);

            //得到中间句的坐标
            float tempY = height / 2;

            //绘制前面部分
            for (int i = index - 1; i >= 0; i--) {

                //得到前一部分多月的歌词内容
                String preContent = lyrics.get(i).getContent();

                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }

                //绘制内容
                canvas.drawText(preContent, width / 2, tempY, paintWhte);

            }

            tempY = height / 2;

            //绘制后面部分
            for (int i = index + 1; i < lyrics.size(); i++) {
                //得到后一部分多月的歌词内容
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }

                //绘制内容
                canvas.drawText(nextContent, width / 2, tempY, paintWhte);
            }


        } else {
            canvas.drawText("没有找到歌词...", width / 2, height / 2, paint);
        }


    }

    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 1; i < lyrics.size(); i++) {
           // long timePoint = lyrics.get(i).getTimePoint();
            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    //中间高亮显示的哪一句
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }else {
                index = i ;
            }
        }
        //导致onDraw
        invalidate();
    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }


}
