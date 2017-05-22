package atguigu.com.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_start = (TextView)findViewById(R.id.tv_start);

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2017/05/16/mp4/170516144657635280.mp4"),"video/*");
                startActivity(intent);
            }
        });
    }
}
