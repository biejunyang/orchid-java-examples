# 
> 此 demo 主要演示了 Spring Boot、Apollo配置中心的的集成使用。

## 1、Apollo配置中心介绍

Apollo（阿波罗）是携程框架部门研发的分布式配置中心，能够集中化管理应用不同环境、不同集群的配置，配置修改后能够实时推送到应用端，并且具备规范的权限、流程治理等特性，适用于微服务配置管理场景。

````
https://github.com/ctripcorp/apollo/wiki/Apollo%E9%85%8D%E7%BD%AE%E4%B8%AD%E5%BF%83%E4%BB%8B%E7%BB%8D
````


## 2、pom.xml已入库Apollo客户端依赖
```xml
    <dependency>
        <groupId>com.ctrip.framework.apollo</groupId>
        <artifactId>apollo-client</artifactId>
        <version>1.1.0</version>
    </dependency>

```


## 3、引入Apollo配置
````
# 应用身份信息，获取配置中心对应项目的配置信息
app.id=apollo-config-example 

# 指定获取那个环境的配置信息，设置环境对应的meta server地址，实际上就是config service的地址
apollo.meta= http://10.225.12.115:1111

# 是否开启引入Apollo配置中心配置；为true则开启，并默认引入application命名空间下的配置
apollo.bootstrap.enabled=true


# 引入
apollo.bootstrap.namespaces=application,testprivatenamespace,C15AG.publicnamespace
````

**注意：引入配置中心配置后，本地配置将会被覆盖。**


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

