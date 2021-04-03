# 
> 此 demo 主要演示了 Spring Cloud 架构中Eureka组件的集成使用。

## 1、Eureka组件介绍

Eureka是一个服务发现组件，解决了传统接口地址硬编码引起的问题。主要作用是能够动态发现服务实例的接口地址。

Eureka Server实现功能：

````
1、提供服务注册和注销API：微服务启动时调用eureka server的服务注册API，注册自己的网络信息；注销时调用eureka server的注销API注销自己注册信息。

2、提供服务发现API：服务消费者调用服务是会调用eureka server的服务发现API获取服务注册表信息（实际上eureka client会定时获取注册表缓存到本地）。

3、服务检查：当服务启动并注册后，会周期性（默认30s）的向eureka server发送心跳表示服务可用；当eureka server在一定时间内（默认90s）没有接收到服务的心跳是，会将该服务注销。

4、Server集群中，节点之间相互同步
````


Eureka Client实现功能：

````
1、服务注册/注销：启动时调取注册/注销API注册本身服务实例信息。

2、心跳机制：周期性发送心跳信息给server端

3、健康自检：检查自身的健康状态，并将改状态绑定到心跳信息中。

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
### 3.1、开启注解：
````
@EnableEurekaServer：eureka server开启注解
````

### 3.2、application.yml配置文件配置

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

````
eureka.server.eviction-interval-timer-in-ms=60000
````
eureka server会定期检查客户端实例是否有效，并清理无效节点的时间,默认60*1000毫秒,即60秒。



## 4、Eureka Client配置
### 4.1、开启注解@EnableDiscoveryClient：
开启服务发现客户端组件的功能，会抓取eureka服务器服务注册信息，以及将本机服务注册到eureka服务器中。

@EnableEurekaClient包含了@EnableDiscoverClient的功能。Spring Cloud中服务发现组件有很多，组zookeeper, Consule等，@EnableDiscoverClient是一个抽象注解支持其他的服务发现组件，@EnableEurekaClient值针对eureka组件。


### 4.2、application.yml配置文件配置

````
spring.application.name=eureka-client

server.port=8080

# 注册本机服务的eureka server
eureka.client.register-with-eureka=true

# 抓取eureka server上的服务实例信息
eureka.client.fetch-registry=true

# eureka客户端间隔多久去抓取一次eureka server上的注册信息，默认30
eureka.client.registry-fetch-interval-seconds=30

# 设置Eureka server地址，server集群环境时，可以设置多个地址，逗号分隔
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# 服务注册时将自己的ip地址注册到eureka注册中心，否则默认将本机的hostname注册到eureka server
eureka.instance.prefer-ip-address=true

# 使用Ip地址注册时，直接指定注册的ip地址，否则程序会动态获取本机ip
eureka.instance.ip-address=127.0.0.1

# 服务注册时的唯一标识,控制台界面上默认显示的名称
eureka.instance.instance-id=${eureka.instance.ip-address}.${spring.application.name}.${server.port}

# eureka客户端需要多长时间发送心跳给eureka服务器，表明他仍然或者，默认30秒
eureka.instance.lease-renewal-interval-in-seconds=30

````

注意：Eureka Server本身也默认可以作为一个Eureka Client实例，在eureka集群部署中，将做一个eureka client实例向其他server节点注册本身。

所以eureka客户端的配置也适用于eureka服务端


## 5、eureka server端服务检查
eureka客户端实例注册到eureka服务端上面后，服务端需要定期检查客户端服务实例的状态

默认情况下客户端会每个30秒发送心跳给服务器，告知服务端该实例仍然是可用的，使用配置```eureka.instance.lease-renewal-interval-in-seconds=30```可以修改间隔时间

eureka服务器也会定期检查客户端实例的状态，当发现某个实例距离上一次接收到心跳后的一定时间内没有在收到该节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除。

eureka服务端定期检查客户端节点的时间间隔设置默认为60秒，通过```eureka.server.eviction-interval-timer-in-ms=60000```配置设置，单位毫秒

eureka服务器在接受到实力的最后一次发出的心跳后，需要等待多长时间可以将此实力作为无效节点(默认90秒)，可以通过配置```eureka.instance.lease-expiration-duration-in-seconds=90```修改时间。

## 6、eureka server自我保护模式
默认情况下（90s）内eureka server 没有收到注册服务发送的心跳时，会注销该实例。但是有种情况是，在网络故障的情况下，无法与eureka server通信导致不能发送心跳，此时服务本身是可用的，这种情况则不能注销实例。

默认情况下Eureka的”自我保护模式“可以避免这种问题，当在短时间内，丢失过多客户端时（可能是王网络故障），节点则进入自我保护模式，会保护注册表中的信息，不会注册服务。当网络故障消除后，则退出自我保护模式。

关闭自我保护模式：````eureka.server.enable-self-preservation=false````

## 7、eureka client实例健康自检
默认情况下，eureka客户端每隔30秒会发送一次心跳给eureka服务器，告知eurek服务器，它任仍然是存去的。

但是实际情况中，eureka表面上可以正常发送心跳给eureka服务器，但实际上是不可用的，比如服务示例需要访问的数据库无法访问，或者需要访问的第三方服务失效，这些情况下服务实例应不能被其他调用者或者客户端获取到，所以需要告知eureka服务器自身的状态。

eureka实例通过实现健康自检，来更正本身的可用状态，并通过发送心跳告知服务器其本身的状态。
```
eureka实例的健康自检分两步：
    1、整合actuator功能木块，实现健康检查器HealthIndicator，根据检查结果设置应用的状态
    2、实现健康检查处理器，eureka的功能，将actuator检查结果和eureka实例状态进行关联设置。
```


## 8、Eureka API使用
### 8.1、eureka服务端事件监听

```java
@Component
public class EurekaStateChangeListener {

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) {
        //服务下线事件
        log.info("服务:{}|{}挂了",event.getAppName(),event.getServerId());
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        //服务注册事件
        log.info("服务:{}|{}注册成功了",event.getInstanceInfo().getAppName(),event.getInstanceInfo().getIPAddr());
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        //服务续约事件
        log.info("心跳检测:{}|{}",event.getInstanceInfo().getAppName(),event.getInstanceInfo().getIPAddr());
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
       //注册中心启动事件
        log.info("EurekaRegistryAvailableEvent");
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        //Server启动
        log.info("EurekaServerStartedEvent");
    }
}

```

### 8.2 eureka服务器上注册服务实例查询
1、通Spring cloud的DiscoveryClient类实例(直接注入)

2、通过Eureka的EurekaClient类实例获取
````java
    @GetMapping("/router")
    public void router(){
        List<ServiceInstance> ins=getServiceInstances();
        for(ServiceInstance service: ins){
            EurekaDiscoveryClient.EurekaServiceInstance esi=(EurekaDiscoveryClient.EurekaServiceInstance)service;
            InstanceInfo info=esi.getInstanceInfo();
            System.out.println(info.getAppName()+"----"+info.getInstanceId()+"---"+info.getStatus());
        }
    }
    
    private List<ServiceInstance> getServiceInstances(){
        List<String> serviceIds=discoveryClient.getServices();
        List<ServiceInstance> result=new ArrayList<>();
        for(String sid: serviceIds){
            result.addAll(discoveryClient.getInstances(sid));
        }
        return result;
    }
````

## 9、eureka server集群使用
Eureka Server可以通过运行多个实例，并且互相注册的方式实现高可用部署,并且eureka实例之间会彼此增量同步注册信息，保持节点数据一致。

当有服务注册时，两个Eureka-eserver是对等的，它们都存有相同的信息，这就是通过服务器的冗余来增加可靠性，当有一台服 务器宕机了，服务并不会终止，因为另一台服务存有相同的数据。


### 9.1、eureka server配置

使用默认配置true；需要向其他节点注册本机信息，以及抓取其他节点的注册信息
````
eureka.client.register-with-eureka=true；
eureka.client.fetchRegistry=ture;
````

指定其他节点的eureka server的地址信息，多个通过都好分割
```
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/
```

### 9.2、eureka客户端配置
向所有的eureka server节点注册服务器信息，
```
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/
```

### 9.3、eureka server运行多个实例，实例之间进行相互注册，相互复制注册表信息，高可用部署时；需注意：
多个实例的服务名必须一致，保证是同一个服务的多个实例;spring.application.name属性一致,这样实例之间才能相互复制，形成副本。（实际上是eureka.instance.appname配置参数，该参数不指定是会默认去spring.application.name的值）

多个实例注册时的域名不能相同；eureka.instance.hostname参数设置；所以一台机器上部署多个实例时，域名不能相同

相互复制生成的副本节点会在eureka的控制台界面DS Replicas中显示



### 9.4、集群中Eureka Server节点的同步机制
集群中的server节点相互注册，相互复制注册表信息，形成副本后会通过同步机制保证节点之间注册表信息的一致性。

当其中一个节点收到注册信息，或者客户端实例发送过来的心跳信息后，会将该信息同步到集群中的其他节点。

### 9.5、Eureka Client实例注册到集群中
eureka客户端实例注册时，默认会注册到```eureka.client.service-url.defaultZone```配置中的**第一个**节点的地址；以及后续向该server节点周期性发送心跳信息。
该server节点会通过集群的同步机制，将收到的注册信息，还有心跳信息复制到其他节点。

默认会按照顺序选择```eureka.client.service-url.defaultZone```配置中的**_第一个可用节点_**发送心跳，按照顺序选择第一个可用的节点。


## 10、Eureka集群分区设置

### 10.1、背景
用户量比较大或者用户地理位置分布范围很广的项目，一般都会有多个机房。这个时候如果上线springCloud服务的话，我们希望一个机房内的服务优先调用同一个机房内的服务
，当同一个机房的服务不可用的时候，再去调用其它机房的服务，以达到减少延时的作用。

### 10.2、作用
服务调用时优先获取相同机房的服务实例进行调用，已减少服务调用延时。只有当相同机房的服务不可用时，才能调用其他分区中的机房中的服务。


### 10.3、Eureka分区概念
eureka中提供了region和zone两个概念是实现分区：

(1)region：可以理解为地理区域上的分区，如：亚洲地区，华中地区，北京等一个地理上的区域划分

(2)zone：可以理解为分区中的机房，北京分区中有两个机房：zone1和zone2


### 10.4、分区配置
(1)eureka client配置：
```
# 指定eureka server节点分区beijing
eureka.client.region=beijing
# 指定分区中的机房zone1,zone2
eureka.client.availability-zones.beijing=zone1,zone2

# 指定连个机房的eureka server地址
eureka.client.service-url.zone1=http://localhost:8761/eureka/
eureka.client.service-url.zone2=http://localhost:8762/eureka/

# 是否优先选择相同分区，相同机房下的server节点
eureka.client.prefer-same-zone-eureka=true

# 指定本身实例是在哪个机房中
eureka.instance.metadata-map.zone=zone1
```

(2)eureka server配置：
```
# 注册本机服务的eureka server
eureka.client.register-with-eureka=true

# 抓取eureka server上的服务实例信息
eureka.client.fetch-registry=true

# 指定eureka server节点分区beijing
eureka.client.region=beijing
# 指定分区中的机房zone1,zone2
eureka.client.availability-zones.beijing=zone1,zone2

# 指定连个机房的eureka server地址
eureka.client.service-url.zone1=http://localhost:8761/eureka/
eureka.client.service-url.zone2=http://localhost:8762/eureka/
```
eureka server配置和server高可用集群部署时一直，这是细分了region和zone;而且之前默认的集群部署中，是在默认region下的同一个机房defaultZone中。

同样是server节点之间相互注册，相互同步注册表信息，心跳信息。

(3)eureka client注册中心选择：

a、```eureka.client.prefer-same-zone-eureka=true```时，先通过region取availability-zones内的第一个zone，然后通过这个zone取service-url下的list，并向list内的第一个注册中心进行注册和维持心跳，不会再向list内的其它的注册中心注册和维持心跳。只有在第一个注册失败的情况下，才会依次向其它的注册中心注册，总共重试3次，如果3个service-url都没有注册成功，则注册失败。每隔一个心跳时间，会再次尝试。

b、```eureka.client.prefer-same-zone-eureka=true```时，直接取service-url下的 list取第一个注册中心来注册，并和其维持心跳检测。不会再向list内的其它的注册中心注册和维持心跳。只有在第一个注册失败的情况下，才会依次向其它的注册中心注册，总共重试3次，如果3个service-url都没有注册成功，则注册失败。每隔一个心跳时间，会再次尝试。

所以说，为了保证服务注册到同一个zone的注册中心，一定要注意availability-zones的顺序，必须把同一zone写在前面


(4)相同机房的服务信息获取

a、首先服务注册时，先指定己属于哪个机房(Zone)，通过```eureka.instance.metadata-map.zone=zone1```配置

b、调用服务是选择相同机房zone下的服务调用：首先获取注册中心上的所有服务实例列表，然后在根据```eureka.instance.metadata-map.zone=zone1```配置，筛选出和自己相同zone下的服务调用

所以，为了保证服务注册到同一个zone的注册中心，一定要注意availability-zones的顺序，必须把```eureka.instance.metadata-map.zone=zone1```自己所在zone写在前面


## 11、Eureka client Ip地址注册
客户端注册时默认使用的是域名注册

```eureka.instance.prefer-ip-address = true```
来使用ip地址注册，注册时spring会默认自动获取机器的第一个非回环IP地址。

```eureka.instance.ip-address=127.0.0.1```
直接指定ip地址注册，会覆盖```eureka.instance.prefer-ip-address = true```的配置效果

```spring.cloud.client.ip-address```配置就是默认spring会自动获取机器的第一个非回环IP地址配置


## 12、参考
https://blog.csdn.net/Michaelwubo/article/details/81449191
http://note.youdao.com/s/4bQE12ox
