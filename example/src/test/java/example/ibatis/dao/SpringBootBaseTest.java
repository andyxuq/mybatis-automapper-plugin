package example.ibatis.dao;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: andyxu
 * Date: 2018/11/28
 * Time: 11:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {DaoConfiguration.class})
@Transactional
public class SpringBootBaseTest {
}
