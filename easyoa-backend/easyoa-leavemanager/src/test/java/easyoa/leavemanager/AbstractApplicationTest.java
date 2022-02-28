package easyoa.leavemanager;

import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by claire on 2019-06-24 - 09:00
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LeaveManagerApplication.class)
@ActiveProfiles("test")
@Slf4j
public class AbstractApplicationTest {

}
