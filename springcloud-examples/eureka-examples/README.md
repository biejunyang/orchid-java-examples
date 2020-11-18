# 
> 此 demo 主要演示了 Spring Cloud 架构中Eureka组件的集成使用。

## 1、Eureka组件介绍

Eureka是一个服务发现组件，解决了传统接口地址硬编码引起的问题。主要作用是能够动态发现服务实例的接口地址。

实现功能：

````
1、提供服务注册和注销API：微服务启动时调用eureka server的服务注册API，注册自己的网络信息；注销时调用eureka server的注销API注销自己注册信息。

2、提供服务发现API：服务消费者调用服务是会调用eureka server的服务发现API获取服务注册表信息（实际上eureka client会定时获取注册表缓存到本地）。

3、服务检查：当服务启动并注册后，会周期性（默认30s）的向eureka server发送心跳表示服务可用；当eureka server在一定时间内（默认90s）没有接收到服务的心跳是，会将该服务注销。
````


## 2、pom文件引入
Eureka Server端
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

```

Eureka Client端
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

```

## 3、Eureka Server配置
1、开启注解：
````
@EnableEurekaServer：eureka server开启注解
````

2、application.yml配置文件配置

````
eureka.instance.hostname=127.0.0.1
````
eureka服务器使用的域名

````
eureka.client.register-with-eureka=false
````
是否向server注册地址：eureka server默认本身也是一个eureka client;启动时会向本身注册服务地址。eureka server单机部署中，作为server不需要向自己注册地址,则选择false;集群部署中，需像其他节点注册本身的地址，则需设置为true

````
eureka.client.fetch-registry=false
````
是否抓取eureka服务器上的注册信息：单节点的组件是不需要同步其他eureka server的注册表，eureka server集群是要设置


````
eureka.instance.lease-expiration-duration-in-seconds=90
````
eureka server接收到客户端实例最后一个发出的心跳后，需要等待多长时间没有收到新的心跳后，能将该实例删除，默认是90秒。

注意，此时间的设置需要大于客户端发出心跳的时间间隔设置




## 4、Eureka Client配置
1、开启注解@EnableDiscoveryClient：
开启服务发现客户端组件的功能，会抓取eureka服务器服务注册信息，以及将本机服务注册到eureka服务器中。

@EnableEurekaClient包含了@EnableDiscoverClient的功能。Spring Cloud中服务发现组件有很多，组zookeeper, Consule等，@EnableDiscoverClient是一个抽象注解支持其他的服务发现组件，@EnableEurekaClient值针对eureka组件。



2、application.yml配置文件配置

````
# 注册本机服务的eureka server
eureka.client.register-with-eureka=true

# 抓取eureka server上的服务实例信息
eureka.client.fetch-registry=true


# 设置Eureka server地址，server集群环境时，可以设置多个地址，逗号分隔
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# 服务注册时将自己的ip地址注册到eureka注册中心，否则默认将本机的hostname注册到eureka server
eureka.instance.prefer-ip-address=true


````








## 4、配置的使用

### 4.1、@Value注解注入

```
    @Value("${privatekey1:a}")
    private String privatekey1;
```

### 4.2、@ConfigurationProperties注解注入
````java
@Data
@Component
@ConfigurationProperties(prefix = "")
public class TestConfigProperties {

    private String mytest;

    private String privatekey1;

    private String publickey1;
    
}
````


注意：@ConfigurationProperties注解注入的配置，当配置更新后默认不会自动更新。

如果需要在Apollo配置变化时自动更新注入的值，需要配合使用EnvironmentChangeEvent或RefreshScope。

实现:

````java

@ApolloConfigChangeListener(value = {ConfigConsts.NAMESPACE_APPLICATION, "TEST1.apollo", "application.yaml"},
      interestedKeyPrefixes = {"redis.cache."})
  public void onChange(ConfigChangeEvent changeEvent) {
    logger.info("before refresh {}", sampleRedisConfig.toString());
    refreshScope.refresh("sampleRedisConfig");
    logger.info("after refresh {}", sampleRedisConfig.toString());
  }
````


## 4、已有配置迁移到配置真中心

https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97#324-%E5%B7%B2%E6%9C%89%E9%85%8D%E7%BD%AE%E8%BF%81%E7%A7%BB

## 5、本地开发模式

https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97#%E4%BA%94%E6%9C%AC%E5%9C%B0%E5%BC%80%E5%8F%91%E6%A8%A1%E5%BC%8F


## 6、集群环境使用

https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97#1242-cluster%E9%9B%86%E7%BE%A4


## 7、Apollo公共组件配置及使用

https://github.com/ctripcorp/apollo/wiki/Apollo使用指南#二公共组件接入指南

## 8、Apollo配置灰度发布

https://github.com/ctripcorp/apollo/wiki/Apollo%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97#55-%E7%81%B0%E5%BA%A6%E5%8F%91%E5%B8%83

## 9、参考
https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97#3213-spring-boot%E9%9B%86%E6%88%90%E6%96%B9%E5%BC%8F%E6%8E%A8%E8%8D%90

