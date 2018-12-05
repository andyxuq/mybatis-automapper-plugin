package example.ibatis.dao;

import example.ibatis.dao.model.StudentDetail;
import example.ibatis.dao.model.StudentSubject;
import example.ibatis.dao.mysql.dao.StudentDoMapper;
import example.ibatis.dao.mysql.dao.StudentSubjectDoMapper;
import example.ibatis.dao.mysql.dao.UserDoMapper;
import example.ibatis.dao.mysql.extend.ExStudentMapper;
import example.ibatis.dao.mysql.model.StudentDo;
import example.ibatis.dao.mysql.model.StudentSubjectDo;
import example.ibatis.dao.mysql.model.UserDo;
import example.ibatis.dao.mysql.model.UserDoExample;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * User: andyxu
 * Date: 2018/11/28
 * Time: 11:05
 */
@EnableAutoConfiguration
public class DaoTest extends SpringBootBaseTest {

    private static Logger logger = LoggerFactory.getLogger(DaoTest.class);

    @Autowired
    private ExStudentMapper exStudentMapper;

    @Autowired
    private UserDoMapper userDoMapper;

    @Autowired
    private StudentDoMapper studentDoMapper;

    @Autowired
    private StudentSubjectDoMapper studentSubjectDoMapper;

    private String[] nameArray = new String[]{"lily", "eva"};

    private int subjectSize = 5;

    @Before
    public void setUp() {
        for (String userName : nameArray) {
            UserDoExample example = new UserDoExample();
            example.createCriteria().andUserNameEqualTo(userName);
            List<UserDo> userList = userDoMapper.selectByExample(example);
            if (!userList.isEmpty()) {
                return;
            }

            UserDo userDo = new UserDo();
            userDo.setUserName(userName);
            userDo.setUserAge(10);
            userDoMapper.insertSelective(userDo);

            StudentDo studentDo = new StudentDo();
            studentDo.setUserId(userDo.getId());
            studentDo.setClassName(userDo.getId() + ":" + getRandomString(10));
            studentDoMapper.insertSelective(studentDo);

            for (int i = 0; i < subjectSize; i++) { //add 5 records
                Map<String, Object> param = new HashMap<>();
                param.put("student_id", studentDo.getId());
                param.put("subject_name", studentDo.getId() + ":name:" + getRandomString(10));
                param.put("subject_teacher", studentDo.getId() + ":teacher:" + getRandomString(10));

                exStudentMapper.insertStudentSubject(param);
            }
        }
    }

    @Test
    public void testAutoMapperOne() {
        String userName = nameArray[0];
        StudentDetail studentDetail = exStudentMapper.getAutoMapperOne(userName);
        assertNotNull(studentDetail);
        assertEquals(userName, studentDetail.getUser().getUserName());
        assertEquals(subjectSize, studentDetail.getSubjectList().size());
    }

    @Test
    public void testAutoMapperAll() {
        List<StudentDetail> detailList = exStudentMapper.getAutoMapperAll();
        assertNotNull(detailList);
        assertEquals(nameArray.length, detailList.size());
    }

    @Test
    public void testOriginMultipleResultMap() {
        Object result = exStudentMapper.getMultipleResultMap();

        assertNotNull(result);
        assertTrue(result instanceof List);

        List list = (List)result;
        assertEquals(nameArray.length, list.size());

        for (Object o : list) {
            assertTrue(o instanceof List);
            List subList = (List)o;
            assertEquals(nameArray.length, subList.size());
        }
    }

    @Test
    public void testGetAutoMapperMultipleResultMap() {
        List<Object> result = exStudentMapper.getAutoMapperMultipleResultMap();
        assertNotNull(result);
        assertTrue(result instanceof List);

        List list = (List)result;
        assertEquals(nameArray.length, list.size());

        for (Object o : list) {
            assertTrue(o instanceof List);
            List subList = (List)o;
            assertEquals(nameArray.length, subList.size());
        }
    }

    @Test
    public void testOriginAutoMapping() {
        Object result = exStudentMapper.getOriginAutoMapping(nameArray[0]);

        assertNotNull(result);
        assertTrue(result instanceof List);

        List list = (List)result;
        assertEquals(subjectSize, list.size());
    }

    @Test
    public void testGetAutoMapperWithXmlSql() {
        List<StudentDetail> detailList = exStudentMapper.getAutoMapperWithXmlSql();

        assertNotNull(detailList);
        assertEquals(nameArray.length, detailList.size());

        for (StudentDetail studentDetail : detailList) {
            assertNotNull(studentDetail.getUser());
            assertEquals(subjectSize, studentDetail.getSubjectList().size());
        }
    }

    @Test
    public void testGetAllSubject() {
        List<StudentSubjectDo> subjectList = exStudentMapper.getAllSubject();

        assertNotNull(subjectList);
        for (StudentSubjectDo subjectDo : subjectList) {
            assertTrue(subjectDo.getSubjectName().contains(":"));
            assertTrue(subjectDo.getSubjectTeacher().contains(":"));
        }
    }

    @Test
    public void testGetAutoMapperAllSubject() {
        List<StudentSubject> subjectList = exStudentMapper.getAutoMapperAllSubject();

        assertNotNull(subjectList);
        for (StudentSubject subjectDo : subjectList) {
            assertTrue(subjectDo.getSubjectName().contains(":"));
            assertTrue(subjectDo.getSubjectTeacher().contains(":"));
        }
    }

    private String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
