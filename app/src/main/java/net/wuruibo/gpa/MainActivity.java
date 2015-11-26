package net.wuruibo.gpa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import net.wuruibo.gpa.bean.ScoreBean;
import net.wuruibo.gpa.bean.StudentInfoBean;
import net.wuruibo.gpa.utils.Constant;
import net.wuruibo.gpa.utils.HtmlParse;
import net.wuruibo.gpa.utils.HttpUtil;
import net.wuruibo.gpa.utils.NetworkState;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends Activity
{
    private ProgressDialog pd;// 进度条对话框
    private int count = 0;
    private String isLogin;
    private int yearFlag = 0;
    private int termFlag = 0;
    private String finalTerm;
    private String[] year = Constant.year;
    private String[] term = Constant.term;
    private String queryViewState;// 查询时要截取的viewstate
    private String strUserId;
    private String strPassword;
    private HttpUtil myHttpUtil;
    private HtmlParse queryHtmlParse, viewStateHtmlParse;
    private StudentInfoBean studentBean;
    private List<ScoreBean> scoreList;

    private EditText userId;
    private EditText password;
    private Button calculate,about;

    public MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        setContentView(R.layout.activity_main);

        if (!NetworkState.isNetworkAvailable(this, true)) {
            return;
        }

        userId = (EditText) findViewById(R.id.userId);
        password = (EditText) this.findViewById(R.id.password);
        calculate = (Button) this.findViewById(R.id.calculate);
        about = (Button) findViewById(R.id.about);

        //年份Spinner
        Spinner spinner_year = (Spinner) findViewById(R.id.spinner_year);
        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adapter_year);

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int flag, long arg3) {
                yearFlag = flag;
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        //学期Spinner
        Spinner spinner_term = (Spinner) findViewById(R.id.spinner_term);
        ArrayAdapter<String> adapter_term = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, term);
        adapter_term.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_term.setAdapter(adapter_term);

        spinner_term.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int flag, long arg3) {
                termFlag = flag;
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        setView();
    }

    private void setView()
    {
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                strUserId = userId.getText().toString();
                strPassword = password.getText().toString();
                if (strUserId.equals("") || strPassword.equals(""))
                {
                    new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("绩点宝提示")
                            .setMessage("请先输入学号或密码=^_^=")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
                else
                {
                    // 进行数据的获取、计算和传递到result中
                    count = 0;
                    // 创建ProgressDialog对象
                    pd = new ProgressDialog(MainActivity.this);
                    // 设置进度条风格，风格为长形
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    // 设置ProgressDialog标题
                    pd.setTitle("绩点宝提示");
                    // 设置ProgressDialog 提示信息
                    pd.setMessage("正在拼命加载中......");
                    // 设置ProgressDialog 进度条进度
                    pd.setProgress(100);
                    // 设置ProgressDialog 的进度条是否不明确
                    pd.setIndeterminate(false);
                    // 设置ProgressDialog 是否可以按退回按键取消
                    pd.setCancelable(false);
                    // 让ProgressDialog显示
                    pd.show();

                    new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                Message msg;
                                Bundle b1 = new Bundle();// 存放数据

                                while (count <= 100)
                                {
                                    // 由线程来控制进度
                                    pd.setProgress(count++);

                                    if (count == 33)
                                    {
                                        String term_Of_year = termResult(term[termFlag]);
                                        finalTerm = year[yearFlag] + term_Of_year;
                                        myHttpUtil = new HttpUtil(strUserId, strPassword, finalTerm);
                                        isLogin = myHttpUtil.login();

                                        // LoginFail表示学号或密码错误
                                        if (isLogin.equals("LoginFail")) {
                                            pd.cancel();
                                            b1.putString("flg", "1");
                                            msg = new Message();
                                            msg.setData(b1);
                                            myHandler.sendMessage(msg);
                                        }
                                        // SystemError表示原创系统出现故障
                                        if (isLogin.equals("SystemError")) {
                                            pd.cancel();
                                            b1.putString("flg", "2");
                                            msg = new Message();
                                            msg.setData(b1);
                                            myHandler.sendMessage(msg);
                                        }
                                    }

                                    if (count == 66) {
                                        viewStateHtmlParse = new HtmlParse(myHttpUtil.getViewStateHtml());
                                        queryViewState = viewStateHtmlParse.getQueryViewState();
                                    }

                                    if (count == 100 && isLogin.equals("LoginSuccess"))
                                    {
                                        if (term[termFlag].equals("全年"))
                                        {
                                            queryHtmlParse = new HtmlParse(myHttpUtil.queryYear(queryViewState));
                                        }
                                        else
                                        {
                                            queryHtmlParse = new HtmlParse(myHttpUtil.query(queryViewState));
                                        }

                                        studentBean = queryHtmlParse.getStudentInfo();
                                        scoreList = queryHtmlParse.getScore();

                                        // 判断查询的学年是否有成绩
                                        if (scoreList.size() == 0)
                                        {
                                            b1.putString("flg", "3");
                                            msg = new Message();
                                            msg.setData(b1);
                                            myHandler.sendMessage(msg);
                                        }

                                    }

                                    Thread.sleep(100);
                                }

                                pd.cancel();

                                if (scoreList != null && scoreList.size() != 0)
                                {
                                    Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                                    resultIntent.putExtra("term", finalTerm);
                                    resultIntent.putExtra("student", studentBean);
                                    resultIntent.putExtra("score", (Serializable) scoreList);
                                    startActivity(resultIntent);
                                }

                            }
                            catch (Exception e)
                            {
                                pd.cancel();
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    public String termResult(String term)
    {
        if(term.equals("第一学期"))
        {
            term="(1)";
        }
        if(term.equals("第二学期"))
        {
            term="(2)";
        }
        if(term.equals("全年"))
        {
            term="";
        }
        return term;
    }

    @Override
    public void onBackPressed() {
        // 弹出退出对话框
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setMessage("您确定要退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // 退出程序
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        dialog.show();

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private static class MyHandler extends Handler {
        WeakReference<MainActivity> activity;

        MyHandler(MainActivity activity) {

            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = this.activity.get();
            if (activity != null){
                activity.doHandler(msg);
            }

        }
    }
    protected void doHandler(Message msg) {
        Bundle b2 = msg.getData();
        String flg = b2.getString("flg");

        if (flg != null) {
            if (flg.equals("1")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("绩点宝提示").setMessage("学号或密码错误，请重新输入！")
                        .setPositiveButton("确定", null).show();
                scoreList = null;
                return;
            }

            if (flg.equals("2")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("绩点宝提示").setMessage("原创系统出故障了，请稍后重试！")
                        .setPositiveButton("确定", null).show();
                scoreList = null;
            }

            if (flg.equals("3")) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("绩点宝提示").setMessage("该学年目前没有你的成绩！")
                        .setPositiveButton("确定", null).show();
            }
        }
    }
}
