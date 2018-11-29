package example.ibatis.dao.mysql.extend;

import example.ibatis.dao.model.StudentDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * User: andyxu
 * Date: 2018/11/20
 * Time: 11:35
 */
public interface ExStudentMapper {

    @Select("select s.id, s.create_time, s.modified_time, s.class_name," +
            " u.id as user_id, u.user_name, u.user_age, " +
            " ss.id as subject_id, ss.subject_name, ss.subject_teacher " +
            " from ie_student s, ie_user u, ie_student_subject ss " +
            " where s.user_id = u.id and s.id = ss.student_id " +
            " and u.user_name = #{userName}")
    StudentDetail getAutoMapperOne(@Param("userName") String userName);

    @Select("select s.id, s.create_time, s.modified_time, s.class_name," +
            " u.id as user_id, u.user_name, u.user_age, " +
            " ss.id as subject_id, ss.subject_name, ss.subject_teacher " +
            " from ie_student s, ie_user u, ie_student_subject ss " +
            " where s.user_id = u.id and s.id = ss.student_id ")
    List<StudentDetail> getAutoMapperAll();

    List<Object> getMultipleResultMap();

    List<StudentDetail> getOriginAutoMapping(@Param("userName") String userName);

    List<StudentDetail> getAutoMapperWithXmlSql();
}
