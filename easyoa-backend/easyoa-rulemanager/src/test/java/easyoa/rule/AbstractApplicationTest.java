package easyoa.rule;

import easyoa.rulemanager.RuleManagerApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by claire on 2019-07-08 - 17:03
 **/
@SpringBootTest(classes = RuleManagerApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
public class AbstractApplicationTest {
}
