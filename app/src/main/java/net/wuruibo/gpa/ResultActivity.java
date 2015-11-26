package net.wuruibo.gpa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.wuruibo.gpa.bean.ScoreBean;
import net.wuruibo.gpa.bean.StudentInfoBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robert on 2015/9/21.
 */
public class ResultActivity extends Activity
{
    private ArrayList<HashMap<String,String>> listItem;
    private String termResult;
    private StudentInfoBean studentResult;
    private List<ScoreBean> scoreListResult;
    private double avaGradePoint;

    private TextView totalGradePoint,totalCredit,averageGradePoint;
    private RelativeLayout result_back,user_info;

    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        termResult = getIntent().getStringExtra("term");
        studentResult = (StudentInfoBean)getIntent().getSerializableExtra("student");
        scoreListResult =  (List<ScoreBean>) getIntent().getSerializableExtra("score");

        findView();
        init();
        calculate();
        setView();

    }

    private void findView()
    {
        totalGradePoint = (TextView) findViewById(R.id.gradePoint);
        totalCredit = (TextView) findViewById(R.id.totalCredit);
        averageGradePoint = (TextView) findViewById(R.id.averageGradePoint);
        result_back = (RelativeLayout)findViewById(R.id.result_back);
        user_info = (RelativeLayout)findViewById(R.id.user_info);
    }

    private void setView()
    {
        result_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent informationIntent = new Intent(ResultActivity.this, UserActivity.class);
                informationIntent.putExtra("stuID", studentResult.getStudentId());
                informationIntent.putExtra("stuName", studentResult.getStudentName());
                informationIntent.putExtra("stuClass", studentResult.getStudentClass());
                informationIntent.putExtra("stuTerm", termResult);

                Bundle bundle = new Bundle();
                bundle.putDouble("jd", avaGradePoint);
                informationIntent.putExtras(bundle);
                startActivity(informationIntent);
            }
        });

    }

    private void calculate()
    {
        double gradePoint=0,credit=0;

        for(int i=0;i<scoreListResult.size();i++)
        {
            if(scoreListResult.get(i).getCredit().trim().equals("0.125")||
                    scoreListResult.get(i).getCredit().trim().equals("0.5")||
                    scoreListResult.get(i).getCredit().trim().equals("1")||
                    scoreListResult.get(i).getCredit().trim().equals("2")||
                    scoreListResult.get(i).getCredit().trim().equals("2.5")||
                    scoreListResult.get(i).getCredit().trim().equals("3")||
                    scoreListResult.get(i).getCredit().trim().equals("4")||
                    scoreListResult.get(i).getCredit().trim().equals("5")||
                    scoreListResult.get(i).getCredit().trim().equals("6")||
                    scoreListResult.get(i).getCredit().trim().equals("7")||
                    scoreListResult.get(i).getCredit().trim().equals("8"))
            {
                credit+=Double.parseDouble(scoreListResult.get(i).getCredit());
            }
            else
            {
                System.out.println("学分为其他字符");
                scoreListResult.get(i).setCredit("0");
            }

        }
        for(int i=0;i<scoreListResult.size();i++)
        {
            if(scoreListResult.get(i).getScore().equals("优秀"))
            {
                scoreListResult.get(i).setScore("95");
                gradePoint+=((Double.parseDouble(scoreListResult.get(i).getScore())-50)/10)*Double.parseDouble(scoreListResult.get(i).getCredit());
            }

            else if(scoreListResult.get(i).getScore().equals("良好"))
            {
                scoreListResult.get(i).setScore("85");
                gradePoint+=((Double.parseDouble(scoreListResult.get(i).getScore())-50)/10)*Double.parseDouble(scoreListResult.get(i).getCredit());
            }
            else if(scoreListResult.get(i).getScore().equals("中等"))
            {
                scoreListResult.get(i).setScore("75");
                gradePoint+=((Double.parseDouble(scoreListResult.get(i).getScore())-50)/10)*Double.parseDouble(scoreListResult.get(i).getCredit());
            }
            else if(scoreListResult.get(i).getScore().equals("及格"))
            {
                scoreListResult.get(i).setScore("60");
                gradePoint+=((Double.parseDouble(scoreListResult.get(i).getScore())-50)/10)*Double.parseDouble(scoreListResult.get(i).getCredit());
            }
            else
            {
                try
                {
                    gradePoint+=((Double.parseDouble(scoreListResult.get(i).getScore())-50)/10)*Double.parseDouble(scoreListResult.get(i).getCredit());
                }
                catch(Exception e)
                {
                    gradePoint += 0;
                    e.printStackTrace();
                }

            }
        }
        avaGradePoint = gradePoint / credit;
        DecimalFormat df=new DecimalFormat(".##");//使double保留两位小数
        totalGradePoint.setText(df.format(gradePoint));
        totalCredit.setText(Double.toString(credit));
        averageGradePoint.setText(df.format(avaGradePoint));
    }

    private void init()
    {
        ListView resultListView = (ListView) findViewById(R.id.lv);
        listItem = loadData();
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
                R.layout.list_item, new String[]{"course","credit","score"},
                new int[]{R.id.course,R.id.credit,R.id.score});
        resultListView.setAdapter(listItemAdapter);
    }

    private ArrayList<HashMap<String, String>> loadData()
    {
        listItem = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<scoreListResult.size();i++)
        {
            HashMap<String,String> map = new HashMap<String,String>();
            String course = scoreListResult.get(i).getCourse();
            String credit = scoreListResult.get(i).getCredit();
            String score = scoreListResult.get(i).getScore();
            map.put("course", course);
            map.put("credit", credit);
            map.put("score", score);
            listItem.add(map);
        }
        return listItem;
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
