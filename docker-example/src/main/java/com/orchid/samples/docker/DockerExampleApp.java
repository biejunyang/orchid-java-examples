package com.orchid.samples.docker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class DockerExampleApp {
    public static void main(String[] args){
        SpringApplication.run(DockerExampleApp.class, args);
    }


    @GetMapping("/hello")
    public String hello(String name){
        return "<h1>hello,"+name+"</h1>";
    }

    @GetMapping("/welcome")
    public String welcome(String name){
        return "<h1>welcome,"+name+"</h1>";
    }

    @GetMapping("/docker-example/world")
    public String world(String name){
        return "<h1>world,"+name+"</h1>";
    }

    @GetMapping("/bye")
    public String bye(String name){
        return "<h1>bye,"+name+"</h1>";
    }
}
