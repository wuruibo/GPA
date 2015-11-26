package net.wuruibo.gpa.utils;

import net.wuruibo.gpa.bean.ScoreBean;
import net.wuruibo.gpa.bean.StudentInfoBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 2015/9/21.
 */
public class HtmlParse {

    private Document doc;

    public HtmlParse(String htmlStr){
        doc = Jsoup.parse(htmlStr);
    }

    public StudentInfoBean getStudentInfo(){

        StudentInfoBean std = new StudentInfoBean();
        Element stdId = doc.select("span#lblXh").first();
        Element stdName = doc.select("span#lblXm").first();
        Element stdClass = doc.select("span#lblBjmc").first();

        std.setStudentId(stdId.text());
        std.setStudentName(stdName.text());
        std.setStudentClass(stdClass.text());
        return std;
    }

    public String getQueryViewState(){
        String viewState=null;
        Elements input = doc.getElementsByTag("input");
        for(int i=0;i<input.size();i++){
            Element ele_viewState = input.get(i);
            if(ele_viewState.attr("name").equals("__VIEWSTATE")){
                viewState = ele_viewState.val();
                break;
            }
        }
        return viewState;
    }

    public List<ScoreBean> getScore()
    {

        List<ScoreBean> scoreList = new ArrayList<ScoreBean>();

        Element	 table = doc.select("table#DataGrid1").first();
        Elements trs = table.select("tr");
        for(int i=1;i<trs.size();i++)
        {
            ScoreBean scorebean = new ScoreBean();
            Elements td = trs.get(i).select("td");
            Element eleCourse = td.get(1);
            Element eleScore = td.get(3);
            Element eleCredit = td.get(5);
            scorebean.setCourse(eleCourse.text());
            scorebean.setCredit(eleCredit.text());
            scorebean.setScore(eleScore.text());
            scoreList.add(scorebean);
        }
        return scoreList;
    }
}
