package easyoa.filetest;

import easyoa.filemanager.FileManagerApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by claire on 2019-07-11 - 10:53
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FileManagerApplication.class)
@ActiveProfiles("dev")
public class ApplicationTest {
}
