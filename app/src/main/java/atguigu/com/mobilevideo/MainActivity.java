package atguigu.com.mobilevideo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;

import atguigu.com.mobilevideo.fargment.BaseFragment;
import atguigu.com.mobilevideo.paper.LocalAudioPaper;
import atguigu.com.mobilevideo.paper.LocalVideoPaper;
import atguigu.com.mobilevideo.paper.NetAudioPaper;
import atguigu.com.mobilevideo.paper.NetVideoPaper;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    FrameLayout fl_content;
    RadioGroup rg_main;
    ArrayList<BaseFragment> fragments;
    //标识每个fragment的坐标
    private int position;
    //优化每个Fragment执行都执行一边生命周期方法,布局加载一次，消耗性能，创建缓存
    private Fragment tempFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        initData();
        rg_main.setOnCheckedChangeListener(this);
        rg_main.check(R.id.rb_local_video);
    }

    private void initData() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoPaper());
        fragments.add(new LocalAudioPaper());
        fragments.add(new NetAudioPaper());
        fragments.add(new NetVideoPaper());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_local_video:
                position = 0;
                break;
            case R.id.rb_local_audio:
                position = 1;
                break;
            case R.id.rb_net_audio:
                position = 2;
                break;
            case R.id.rb_net_video:
                position = 3;
                break;
        }
        BaseFragment baseFragment = fragments.get(position);
        showFragment(baseFragment);
    }

    private void showFragment(BaseFragment baseFragment) {
        if (tempFragment != baseFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (baseFragment != null) {
                if (!baseFragment.isAdded()) {
                    if (tempFragment != null) {
                        ft.hide(tempFragment);
                    }
                    ft.add(R.id.fl_content, baseFragment);
                } else {
                    if (tempFragment != null) {
                        ft.hide(tempFragment);
                    }
                    ft.show(baseFragment);
                }
                ft.commit();
                tempFragment = baseFragment;
            }
        }


//        ft.replace(R.id.fl_content,baseFragment);
//        ft.commit();
    }
}
