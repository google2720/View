package com.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.view.card.MusicCard;
import com.view.card.NavCard;
import com.view.card.TimeCard;
import com.view.card.TrafficCard;
import com.view.card.WeChatCard;
import com.view.card.WeatherCard;

public class MainActivity extends Activity {
    private LayoutInflater inflater;
    private PageView mPageView;
    private android.widget.Button btnone;
    private MoveView moveView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_viewgroup);

        SlideGroup slideGroup = (SlideGroup) findViewById(R.id.sl);
        SlideGroupHelper slideGroupHelper = new SlideGroupHelper(slideGroup);


        // View view = View.inflate(this, R.layout.test, null);
        // myviewpager.addView(view, 1);


        slideGroup.addView(new MusicCard(this));
        slideGroup.addView(new NavCard(this));
        slideGroup.addView(new TimeCard(this));
        slideGroup.addView(new TrafficCard(this));
        slideGroup.addView(new WeatherCard(this));
        slideGroup.addView(new WeChatCard(this));
        slideGroup.addView(new NavCard(this));
        slideGroup.addView(new TimeCard(this));
        slideGroup.addView(new TrafficCard(this));

        slideGroup.addView(new WeatherCard(this));
        slideGroup.addView(new WeChatCard(this));
        slideGroup.addView(new NavCard(this));
        slideGroup.addView(new TimeCard(this));
        slideGroup.addView(new TrafficCard(this));
        slideGroup.addView(new WeatherCard(this));
        slideGroup.addView(new WeChatCard(this));
        slideGroup.addView(new WeChatCard(this));






//        setContentView(R.layout.activity_main_ii);
//        this.moveView = (MoveView) findViewById(R.id.moveView);
//        this.btnone = (Button) findViewById(R.id.btn_one);
//
//        this.btnone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                moveView.setBackgroundColor(Color.RED);
//            }
//        });


//        TranslateAnimation anim = new TranslateAnimation(0, 100, 0, 0);
//        anim.setDuration(300);
//        anim.start();
//        anim.setFillAfter(true);
//        btnone.setAnimation(anim);


//        ObjectAnimator anim = ObjectAnimator.ofFloat(btnone, "translationX", 0, 100);
//        anim.setDuration(300);
//        anim.start();


//        setContentView(R.layout.activity_main);
//        inflater = LayoutInflater.from(this);
//        mPageView = (PageView) findViewById(R.id.pageview);
//        //增加几个页面
//        LinearLayout layout = new LinearLayout(this);
//        layout.setBackgroundColor(Color.BLUE);
//        mPageView.addPage(layout);
//
//        LinearLayout layout2 = new LinearLayout(this);
//        layout2.setBackgroundColor(Color.YELLOW);
//        mPageView.addPage(layout2);
//
//        LinearLayout layout3 = new LinearLayout(this);
//        layout3.setBackgroundColor(Color.BLACK);
//        mPageView.addPage(layout3);
//
//        LinearLayout layout4 = new LinearLayout(this);
//        layout4.setBackgroundColor(Color.DKGRAY);
//        mPageView.addPage(layout4);
//
//        LinearLayout layout5 = new LinearLayout(this);
//        layout5.setBackgroundColor(Color.GREEN);
//        mPageView.addPage(layout5);
//
//        LinearLayout layout6 = new LinearLayout(this);
//        layout6.setBackgroundColor(Color.YELLOW);
//        mPageView.addPage(layout6);
//
//        //这里就是个普通的xml布局文件
//        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.activity_page, null);
//        mPageView.addPage(view);
//
//        //删除一个页面
////      mPageView.removePage(1);

        Person person = new Person();
        person.setSex(100);

        Person2 person2 = new Person2();
        person2.setSex(SEX.WOMAN);

    }
}

class Person {
    public static final int MALE = 0;
    public static final int FEMALE = 1;

    private int sex;

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSexDes() {
        if(sex == 0) {
            return "男";
        }else if(sex == 1){
            return "女";
        }else {
            throw new IllegalArgumentException("什么鬼性别？");
        }
    }
}

class Person2 {
    public static final int MALE = 0;
    public static final int FEMALE = 1;

    private SEX sex;

    public void setSex(SEX sex) {
        this.sex = sex;
    }

    public String getSexDes() {
        if(sex == SEX.MAN) {
            return "男";
        }else if(sex == SEX.WOMAN){
            return "女";
        }else {
            throw new IllegalArgumentException("什么鬼性别？");
        }
    }
}




