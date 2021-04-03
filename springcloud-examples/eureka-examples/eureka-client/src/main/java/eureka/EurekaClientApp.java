package eureka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class EurekaClientApp {


    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApp.class, args);
    }


    @GetMapping("hello")
    public String hello(String name){
        return "<h1>hello,"+name+"</h1>";
    }

    @Value("${eureka.instance.metadata-map.zone:''}")
    private String zone;


    @GetMapping("welcome")
    public String welcome(){
        return "<h2>welcome, "+zone+"</h2>";
    }

}
