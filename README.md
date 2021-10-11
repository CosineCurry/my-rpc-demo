# my-rpc-demo
自己实现的RPC框架 <br>
学习于：https://blog.csdn.net/qq_40856284/category_10138756.html
## 基础版本
RPC，远程过程调用，消费者调用服务端的方法就像调用本地方法一样简单。那如何去实现呢？首先，消费者和服务端有一个公共接口，能够达成共识，服务端
知道消费者想要调用哪个方法。再者，需要网络传输，基础版本是用原生socket去做的。BIO的网络模型是阻塞的，阻塞在于ServerSocket#accept和
Socket#read。<br>
具体来说，公共接口的实现类只有服务端有，消费者如果想调用其中的方法就必须通过网络传输。基础版本的消费者通过动态代理动态地生成公共接口的代理类，
在代理类中能够拿到这个代理类的**实现的接口名**，**调用的方法名**，**方法参数**，**方法参数类型**（方法可能重载，所以需要参数类型指定唯一的
方法），并将这些参数包装成一个RpcRequest对象。在invoke()方法中调用RpcClient的sendRequest方法。而sendRequest方法中利用socket配合
java自带的传输格式，将这个对象传到服务端。<br>
基础版本的服务端先是手动注册单个服务，并监听一个端口，与消费者进行socket连接，获取消费者传来的RpcRequest，通过反射获取服务对象，并且调用消
费者想要调用的方法，最后返回结果。至此一个RPC过程就结束了。
## 服务端注册多个服务
基础版本的劣势在于只能注册一个服务实例，这个版本通过引入服务注册表对此进行了改善。启动服务的过程变成两步：先注册服务（可注册多个），再启动
（start()方法监听一个端口）。服务注册表的逻辑利用接口进行解耦。
## Netty传输和通用序列化接口
将传统的BIO方式传输换成效率更高的NIO方式，使用较为简单的Netty进行实现。还实现了一个通用的序列化接口，为多种序列化支持做准备，并且自定义了
传输的协议。传输的包首部加上魔数（4字节）、包类型（请求或响应，4字节）、序列化类型（4字节）、数据长度（4字节）。如果魔数不相同，拒绝收包。
定义了数据长度是为了防止粘包。
## Kryo序列化
这个基于 JSON 的序列化器有一个毛病，就是在某个类的属性反序列化时，如果属性声明为 Object 的，就会造成反序列化出错，通常会把 Object 属
性直接反序列化成 String 类型，就需要其他参数辅助序列化。并且，JSON 序列化器是基于字符串（JSON 串）的，占用空间较大且速度较慢。<br>
Kryo 是一个快速高效的 Java 对象序列化框架，主要特点是高性能、高效和易用。最重要的两个特点，一是基于字节的序列化，对空间利用率较高，
在网络传输时可以减小体积；二是序列化时记录属性对象的类型信息，这样在反序列化时就不会出现之前的问题了。<br>
Kryo可能存在线程安全的问题，我们使用ThreadLocal避免之。
```java
private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        // 支持循环引用
        kryo.setReferences(true);
        // 关闭注册行为
        kryo.setRegistrationRequired(false);
        return kryo;
    });
```