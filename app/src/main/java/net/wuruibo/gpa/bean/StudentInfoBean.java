package net.wuruibo.gpa.bean;

import java.io.Serializable;

/**
 * Created by Robert on 2015/9/21.
 */
public class StudentInfoBean implements Serializable {

    private String studentId;
    private String studentName;
    private String studentClass;

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getStudentClass() {
        return studentClass;
    }
    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

}
