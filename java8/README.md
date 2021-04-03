# java8
> 此 demo 主要演示了 Java 8新特性的使用

## 1、lambda表达式
lambda表达式实际一个匿名内部类对象，可以作为一个方法的参数。并且匿名内部类实现的接口中有且只有一个抽象接口(函数式接口)。

所以lambda表达式也可称为一个闭包，或函数对象，是Java中面向函数式编程的思想。

即、Java中Lambda 表达式是对象，他们必须依附于一类特别的对象类型——函数式接口(functional interface)、如

```java
//匿名内部类实现
 new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("内部类写法");
    }
}).start();

```


```java
//lambda表达式
 new Thread(() - > System.out.println("lambda写法")).start();

```

### 1.1、lambda表达式语法
(arg1, arg2...) -> { body }

(type1 arg1, type2 arg2...) -> { body }

lambada特性
````
可选类型声明：不需要声明参数类型，编译器可以统一识别参数值。
可选的参数圆括号：一个参数无需定义圆括号，但多个参数需要定义圆括号。
可选的大括号：如果主体包含了一个语句，就不需要使用大括号。
可选的返回关键字：如果主体只有一个表达式返回值则编译器会自动返回值，大括号需要指定明表达式返回了一个数值。
````

实例：
```java
    //入参为空
    TestDemo no_param = () -> "hi, no param";
    TestDemo no_param2 = () -> { return "hi, no param"; };
    System.out.println(no_param.hi());

    //单个参数
    TestDemo2 param = name -> name;
    TestDemo2 param2 = name -> { return name;};
    System.out.println(param.hei("hei, grils"));

    //多个参数
    TestDemo3 multiple = (String hello, String name) -> hello + " " + name;
    //一条返回语句，可以省略大括号和return
    TestDemo3 multiple2 = (hello, name) -> hello + name;
    //多条处理语句，需要大括号和return
    TestDemo3 multiple3 = (hello, name) -> {
        System.out.println("进入内部");
        return hello + name;
    };
    System.out.println(multiple.greet("hello", "lambda"));
```

### 1.2、lambda方法引用
引用对象的实例方法，或者类的静态访问来创建lambda表达式，如：

#### 1.2.1、对象实例方法引用
语法：objectName::instanceMethod

示例：
````
Consumer<String> sc = System.out::println;
//等效
Consumer<String> sc2 = (x) -> System.out.println(x);
sc.accept("618, 狂欢happy");
````



#### 1.2.2、类静态方法引用
语法：ClassName::staticMethod

示例：
````
//ClassName::staticMethod  类的静态方法：把表达式的参数值作为staticMethod方法的参数
Function<Integer, String> sf = String::valueOf;
//等效
Function<Integer, String> sf2 = (x) -> String.valueOf(x);
String apply1 = sf.apply(61888);
````



#### 1.2.3、类实例方法引用
语法：ClassName::instanceMethod

示例：
````
//ClassName::instanceMethod  类的实例方法：把表达式的第一个参数当成instanceMethod的调用者，其他参数作为该方法的参数
BiPredicate<String, String> sbp = String::equals;
//等效
BiPredicate<String, String> sbp2 = (x, y) -> x.equals(y);
boolean test = sbp.test("a", "A");
````

注意：
此种表达式调用中，将lambda的第一个参数当做方法的调用者，其他的参数作为方法的参数。开发中尽量少些此类写法，减少后续维护成本。


### 1.3、lambda构造引用
引用类的构造方法来创建lambda表达式，如：

```java
   Supplier<User> us = User::new;
        //等效
    Supplier<User> us2 = () -> new User();
    //获取对象
    User user = us.get();
    
    
     //一个参数,参数类型不同则会编译出错
    Function<Integer, User> uf = id -> new User(id);
    //或加括号
    Function<Integer, User> uf2 = (id) -> new User(id);
    //等效
    Function<Integer, User> uf3 = (Integer id) -> new User(id);
    User apply = uf.apply(61888);

    //两个参数
    BiFunction<Integer, String, User> ubf = (id, name) -> new User(id, name);
    User 狂欢happy = ubf.apply(618, "狂欢happy");
    
```


## 2、函数式接口
函数式接口”是指仅仅只包含一个抽象方法的接口，每个lambda表达式对对应一个函数式接口，会被匹配到对应接口的抽象方法上。

函数式接口的默认方法不算抽象方法，所以你也可以给你的函数式接口添加默认方法。

JDK 1.8 API包含了很多内建的函数式接口、能够很方便的构建lambda表达式，如：


### Predicate<T>接口
该接口接收一个类型的入参(T类型)，并返回一个boolean类型值、可以用来实现判断逻辑。并且该接口包含多种默认方法来将Predicate组合成其他复杂的逻辑（比如：与，或，非）如：

```java
    Predicate<String> predicate = (s) -> s.length() > 0;
    
    predicate.test("foo");              // true
    predicate.negate().test("foo");     // false
    
    Predicate<Boolean> nonNull = Objects::nonNull;
    Predicate<Boolean> isNull = Objects::isNull;
    
    Predicate<String> isEmpty = String::isEmpty;
    Predicate<String> isNotEmpty = isEmpty.negate();
```


### Function<T, R>接口
该接口接收一个入参(T类型)，并返回一个结果(R类型)。并附带了一些可以和其他函数组合的默认方法（compose, andThen）：如：

```java
    Function<String, Integer> toInteger = Integer::valueOf;
    Function<String, String> backToString = toInteger.andThen(String::valueOf);
    
    backToString.apply("123");     // "123"
```


### Consumer<T>  接口
该接口接收一个入参(T类型)，没有返回结果。

```java
    Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
    greeter.accept(new Person("Luke", "Skywalker"));
```


### Supplier<T> 接口
该接口没有入参，返回一个结果(T类型)。

```java
    Supplier<Person> personSupplier = Person::new;
    personSupplier.get();   // new Person
```



### Comparator<T> 接口
Comparator 是老Java中的经典接口， Java 8在此之上添加了多种默认方法：

```java
    Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
    
    Person p1 = new Person("John", "Doe");
    Person p2 = new Person("Alice", "Wonderland");
    
    comparator.compare(p1, p2);             // > 0
    comparator.reversed().compare(p1, p2);  // < 0/ new Person
```



## 8、参考
https://www.ibm.com/developerworks/cn/java/j-lo-jdk8newfeature/
https://www.jianshu.com/p/0bf8fe0f153b
https://www.cnblogs.com/xichji/p/11570387.html


