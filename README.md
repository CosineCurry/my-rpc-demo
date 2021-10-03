# my-rpc-demo
自己实现的RPC框架
学习于：https://blog.csdn.net/qq_40856284/category_10138756.html
## 基础版本
基础版本服务端可以注册一个服务，监听一个端口，是利用java序列化+socket网络连接去做的。是BIO的网络模型，即一个连接过来会新建一个工作线程去处理。
