package net.wuruibo.gpa;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;

/**
 * Created by Robert on 2015/9/21.
 */
public class UserActivity extends Activity
{
    private TextView info_userId,info_userName,info_class,info_comment;
    private ImageView info_cartoon;
    private RelativeLayout user_back;

    private String strStudentID,strStudentStuName,strStudentStuClass,strStudentTerm;
    private double gradePoint;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle bundle =getIntent().getExtras();
        gradePoint = bundle.getDouble("jd");

        strStudentID = getIntent().getStringExtra("stuID");
        strStudentStuName = getIntent().getStringExtra("stuName");
        strStudentStuClass = getIntent().getStringExtra("stuClass");
        strStudentTerm = getIntent().getStringExtra("stuTerm");

        findView();
        setView();

    }

    private void findView()
    {
        info_userId = (TextView)findViewById(R.id.info_userId);
        info_userName = (TextView)findViewById(R.id.info_userName);
        info_class = (TextView)findViewById(R.id.info_class);
        info_comment = (TextView)findViewById(R.id.info_comment);
        info_cartoon = (ImageView)findViewById(R.id.info_cartoon);
        user_back = (RelativeLayout)findViewById(R.id.user_back);
    }

    private void setView()
    {
        DecimalFormat df=new DecimalFormat(".##");//使double保留两位小数
        info_userId.setText(strStudentID);
        info_userName.setText(strStudentStuName);
        info_class.setText(strStudentStuClass);

        if(gradePoint > 0 && gradePoint < 1)
        {
            info_comment.setText("您在"+strStudentTerm+"学年中平均绩点为"+df.format(gradePoint)+"，可称为\"麻辣小龙侠\"！");
            info_cartoon.setImageResource(R.drawable.malaxiaolongxia);
        }

        if(gradePoint >= 1 && gradePoint < 2)
        {
            info_comment.setText("您在"+strStudentTerm+"学年中绩点为"+df.format(gradePoint)+"，可称为\"铁胆火车侠\"！");
            info_cartoon.setImageResource(R.drawable.tiedanhuochexia);
        }

        if(gradePoint >= 2 && gradePoint < 3)
        {
            info_comment.setText("您在" + strStudentTerm+ "学年中绩点为" + df.format(gradePoint) + "，可称为\"逆袭侠\"！");
            info_cartoon.setImageResource(R.drawable.nixixia);
        }

        if(gradePoint >= 3 && gradePoint < 4)
        {
            info_comment.setText("您在" +strStudentTerm+ "学年中绩点为" + df.format(gradePoint) + "，可称为\"功夫侠\"！");
            info_cartoon.setImageResource(R.drawable.gongfuxia);
        }

        if(gradePoint >= 4 && gradePoint <= 5)
        {
            info_comment.setText("您在"+strStudentTerm+"学年中绩点为"+df.format(gradePoint)+"，果真\"绩点侠\"！");
            info_cartoon.setImageResource(R.drawable.jidianxia);
        }

        user_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
