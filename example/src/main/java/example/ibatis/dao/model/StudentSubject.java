package example.ibatis.dao.model;

import andy.ibatis.plugin.annation.Column;
import example.ibatis.dao.handler.ExampleTypeHandler;
import example.ibatis.dao.mysql.model.StudentSubjectDo;

/**
 * User: andyxu
 * Date: 2018/12/5
 * Time: 11:34
 */
public class StudentSubject extends StudentSubjectDo {

    @Column(name = "subject_name", typeHandler = ExampleTypeHandler.class)
    private String subjectName;

    @Column(name = "subject_teacher", typeHandler = ExampleTypeHandler.class)
    private String subjectTeacher;

    @Override
    public String getSubjectName() {
        return subjectName;
    }

    @Override
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String getSubjectTeacher() {
        return subjectTeacher;
    }

    @Override
    public void setSubjectTeacher(String subjectTeacher) {
        this.subjectTeacher = subjectTeacher;
    }
}
