package springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableWebSecurity
public class SpringSecurityExampleApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityExampleApp.class, args);
    }



    @GetMapping("/hello")
    public String hello(String name){
        return "<h1>hello,"+name+"</h1>";
    }

    @GetMapping("/welcome")
    public String welcome(String name){
        return "<h1>welcome,"+name+"</h1>";
    }

    @GetMapping("/bye")
    public String bye(String name){
        return "<h1>bye,"+name+"</h1>";
    }
}
