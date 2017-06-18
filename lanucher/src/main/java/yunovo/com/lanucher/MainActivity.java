package yunovo.com.lanucher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import yunovo.com.lanucher.card.MusicCard;
import yunovo.com.lanucher.card.MyViewPage;
import yunovo.com.lanucher.card.NavCard;
import yunovo.com.lanucher.card.TimeCard;
import yunovo.com.lanucher.card.TrafficCard;
import yunovo.com.lanucher.card.WeChatCard;
import yunovo.com.lanucher.card.WeatherCard;

public class MainActivity extends AppCompatActivity {

    private MyViewPage myviewpager;

    private final int imageIds[] = { R.drawable.a1, R.drawable.a2,
            R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6 };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        myviewpager = (MyViewPage) findViewById(R.id.myviewpager);


//        for (int i = 0; i < imageIds.length; i++)
//        {
//            ImageView imageView = new ImageView(this);
//            imageView.setBackgroundResource(imageIds[i]);
//            myviewpager.addView(imageView);
//        }

        myviewpager.addView(new MusicCard(this));

       // View view = View.inflate(this, R.layout.test, null);
       // myviewpager.addView(view, 1);

        myviewpager.addView(new NavCard(this));
        myviewpager.addView(new TimeCard(this));
        myviewpager.addView(new TrafficCard(this));
        myviewpager.addView(new WeatherCard(this));
        myviewpager.addView(new WeChatCard(this));
        myviewpager.addView(new NavCard(this));
        myviewpager.addView(new TimeCard(this));
        myviewpager.addView(new TrafficCard(this));
        myviewpager.addView(new WeatherCard(this));
        myviewpager.addView(new WeChatCard(this));

        myviewpager.addView(new NavCard(this));
        myviewpager.addView(new TimeCard(this));
        myviewpager.addView(new TrafficCard(this));
        myviewpager.addView(new WeatherCard(this));
        myviewpager.addView(new WeChatCard(this));

        myviewpager.addView(new NavCard(this));
        myviewpager.addView(new TimeCard(this));
        myviewpager.addView(new TrafficCard(this));
        myviewpager.addView(new WeatherCard(this));
        myviewpager.addView(new WeChatCard(this));



    }
}
