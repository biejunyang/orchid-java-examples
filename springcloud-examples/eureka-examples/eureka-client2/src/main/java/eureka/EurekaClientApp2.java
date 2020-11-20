package eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class EurekaClientApp2 {


    @Autowired
    private RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApp2.class, args);
    }


    @GetMapping("hello")
    public String hello(String name){
        return restTemplate.getForObject("http://eureka-client/hello", String.class);
    }



    @Bean // 自动扫描
    @LoadBalanced //这个注解的意思是在启动时先加载注册中心的域名列表
    public RestTemplate restTemplate() //这个方法用来发http请求
    {
        RestTemplate restTemplate=new RestTemplate();
        return restTemplate;
    }
}
