package yunovo.com.dragdemo;

import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "DragDemo";
    private ImageView image;
    private ImageView image2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        image2 = (ImageView) findViewById(R.id.image2);
        image.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 创建DragShadowBuilder，我把控件本身传进去
                DragShadowBuilder builder = new DragShadowBuilder(v);
                // 剪切板数据，可以在DragEvent.ACTION_DROP方法的时候获取。
                ClipData data = ClipData.newPlainText("dot", "Dot : " + v.toString());
                // 开始拖拽
                v.startDrag(data, builder, v, 0);
                return true;
            }
        });


        image.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();

                Log.d(TAG, "image1 v event:"+ event.getX());

               // Log.d(TAG, "ionDrag");

                switch (action) {
                    // 开始拖拽
                    case DragEvent.ACTION_DRAG_STARTED:
                        image.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "image1 ACTION_DRAG_STARTED");
                        break;
                    // 结束拖拽
                    case DragEvent.ACTION_DRAG_ENDED:
                        image.setVisibility(View.VISIBLE);
                        Log.d(TAG, "image1 ACTION_DRAG_ENDED");
                        break;
                    // 拖拽进某个控件后，退出
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(TAG, "image1 ACTION_DRAG_EXITED");
                        break;
                    // 拖拽进某个控件后，保持
                    case DragEvent.ACTION_DRAG_LOCATION:
                       // Log.d(TAG, "image1 v getX:"+v.getX());
                        //Log.d(TAG, "image1 v getPivotX :"+v.getPivotX());
                       // Log.d(TAG, "image1 ACTION_DRAG_LOCATION");
                        break;
                    // 推拽进入某个控件
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(TAG, "image1 ACTION_DRAG_ENTERED");
                        break;
                    // 推拽进入某个控件，后在该控件内，释放。即把推拽控件放入另一个控件
                    case DragEvent.ACTION_DROP:
                        Log.d(TAG, "image1 ACTION_DROP");
                        break;
                }
                return true;
            }
        });
        image2.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d(TAG, "image2 ACTION_DRAG_STARTED");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        image2.setBackgroundColor(Color.TRANSPARENT);
                        Log.d(TAG, "image2 ACTION_DRAG_ENDED");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        image2.setBackgroundColor(Color.TRANSPARENT);
                        Log.d(TAG, "image2 ACTION_DRAG_EXITED");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        image2.setBackgroundColor(Color.BLUE);
                        Log.d(TAG, "image2 ACTION_DRAG_ENTERED");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(TAG, "image2 ACTION_DRAG_LOCATION");
                    case DragEvent.ACTION_DROP:
                        Log.d(TAG, "image2 ACTION_DROP");
                }
                return true;
            }
        });
    }

}
