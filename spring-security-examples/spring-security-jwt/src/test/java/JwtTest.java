import com.nimbusds.jose.JOSEException;
import com.orchid.examples.security.JwtSecurityApp;
import com.orchid.web.auth.JwtTokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= JwtSecurityApp.class)
public class JwtTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Test
    public void test() throws JOSEException {
        jwtTokenUtil.createToken("zhangsan");
    }
}
