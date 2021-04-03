1、Redis支持的数据结构有5中：字符串、列表、集合、散列、有序集合。底层能存储的数据字符串或者数值型。
底层Redis中存储的键，和值或者结合类型中的元素都只能是字符串或者数值型。
Spring boot Redis中在进行redis操作时，支持对象类型。Redis的key和value都可以是对象类型。
而redis底层却只支持字符串类型，所以spring boot 进行redis操作时需要使用序列化器对key和value进行序列化和反序列化。
默认是使用的是JdkSerializationRedisSerializer，jdk本身的序列化机制。

2、常用序列化器：
    JdkSerializationRedisSerializer：默认的序列化器，jdk中本身的序列化机制。只能序列化字符串、和数值型的数据，不能序列化其他对象类型。
    StringRedisSerializer：只能序列化字符串类型，通常可以作为redis key的序列化器
    JacksonJsonRedisSerializer：支持所有Java类型的序列化，能够将数据序列化成json字符串，存储到redis中
    OxmSerializer：支持所有Java类型的序列化，将数据序列化成xml格式的字符串。一般少用，json串更加轻量级。
    
    
3、spring-data-redis API管理：
   3.1、RedisTemplate：提供类一个高度封装的Redis操作类，能够对Redis的各种数据结构进行操作，并且提供了连接池的自动管理。
    
   3.2、针对Redis的5中数据结构，对api进行了分类，将同一类型操作封装为operation接口
        ValueOperations：操作单个数据进行操作，数据可以是简单类型，也可以是对象类型，最终都会序列化成字符串存储到redis的字符串结构中
        SetOperations：set类型数据操作
        ZSetOperations：zset类型数据操作
        HashOperations：针对map类型的数据操作
        ListOperations：针对list类型的数据操作
    
   3.3、提供了对key的“bound”(绑定)便捷化操作API，可以通过bound封装指定的key，然后进行一系列的操作而无须“显式”的再次指定Key，即
        BoundKeyOperations：
        BoundValueOperations
        BoundSetOperations
        BoundListOperations
        BoundSetOperations
        BoundHashOperations
        
   3.4、泛型机制
        RedisTemplate<K, V>：K为键的Java数据类型，V为值的Java数据类型
        
4、将事务操作封装，有容器控制。

5、关于key的设计
   5.1、key的存活时间：无论什么时候，只要有可能就利用key超时的优势。一个很好的例子就是储存一些诸如临时认证key之类的东西。当你去查找一个授权key时——以OAUTH为例——通常会得到一个超时时间。
这样在设置key的时候，设成同样的超时时间，Redis就会自动为你清除。

   5.4、关系型数据库的redis
        1: 把表名转换为key前缀 如, tag:
        2: 第2段放置用于区分区key的字段--对应mysql中的主键的列名,如userid
        3: 第3段放置主键值,如2,3,4...., a , b ,c
        4: 第4段,写要存储的列名
        例：user:userid:9:username
  
6、事务
    MULTI    开启事务
    EXEC    执行任务队列里所有命令，并结束事务
    DISCARD     放弃事务，清空任务队列，全部不执行，并UNWATCH
    WATCH key [key1]    MULTI执行之前，指定监控某key，如果key发生修改，放弃整个事务执行
    UNWATCH    手动取消监控
    redis进行事务操作为，会挂起其他客户端的请求，指的是redis 执行exec命令时
    ，不会再执行其他客户端的命令(单线程请求处理)、而不是说执行multi后就不会执行其他连接的命令

   RedisTemplate开启事务支持：
       //开启事务支持
        template.setEnableTransactionSupport(true);
     事务开启后，后续操作都需要使用事务、若是不需要则事务执行完毕后，关闭事务支持
     
    事务异常后可以选重试事务，或者终止执行
  
 管道：
 
 
7、Redis锁
   乐观锁：使用watch命令对键(可以多个)进行监视之后、直至执行exec命令的这段时间内，
   如果有其他客户端抢先对监视的键进行了替换、修改、删除等操作、当用户尝试执行exec命令时、
   事务将会失败，并返回一个错误(之后用户可以选择重试事务或者放弃)。
   通过使用watch、，multi/exec、unwatch/discard命令来确保自己正在使用的数据没有发生变化。
   
   注意：使用RedisTemplate实现乐观锁机制时，当监视的数据被修改了，执行exec命令时，程序并不会异常，
   可以根据执行exec返回的结果来判断事务是否执行成功
        使用watch命令来监视频繁访问的键时，可能会一起性能问题。
        
   分布式锁：
    锁的问题：
        获取锁、释放锁、获取锁等待超时、锁超时不是放、多个线程获取到了锁
        主要就是构建锁的key
    乐观锁在高并发情况下，会增加操作失败的次数，数据的唯一性保证了，但是降低了操作成功的次数(如果重试更会影响)。

8、计算信号量(Semaphore)
 
       
6、redis连接池
https://blog.csdn.net/asdasd3418/article/details/84639782?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task


7、redisson库
https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95



https://blog.csdn.net/u010623927/article/details/87918603