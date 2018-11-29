package example.ibatis.dao.model;

import andy.ibatis.plugin.annation.Column;
import andy.ibatis.plugin.annation.Many;
import andy.ibatis.plugin.annation.One;
import example.ibatis.dao.mysql.model.StudentDo;
import example.ibatis.dao.mysql.model.StudentSubjectDo;
import example.ibatis.dao.mysql.model.UserDo;
import org.apache.ibatis.type.JdbcType;

import java.util.*;


/**
 * User: andyxu
 * Date: 2018/11/20
 * Time: 11:28
 */
public class StudentDetail extends StudentDo {

    @Column(name = "id", jdbcType = JdbcType.INTEGER, isId = true)
    private Integer id;

    @One(idColumn = "user_id")
    private UserDo user;

    @Many(idColumn = "subject_id")
    private List<StudentSubjectDo> subjectList;

    public UserDo getUser() {
        return user;
    }

    public void setUser(UserDo user) {
        this.user = user;
    }

    public List<StudentSubjectDo> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<StudentSubjectDo> subjectList) {
        this.subjectList = subjectList;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

}
