package com.cosine.rpc.netty.server;

import com.cosine.rpc.RpcServer;
import com.cosine.rpc.codec.CommonDecoder;
import com.cosine.rpc.codec.CommonEncoder;
import com.cosine.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 利用Netty(NIO)实现的服务端
 * @Author: cosine
 * @Date: 2021/10/6 2:39 下午
 * @Version: 1.0.0
 */
public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Override
    public void start(int port) {
        /**
         * NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器。bossGroup用来接收进来的连接，
         * workerGroup用来处理已经被接收的连接,一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
         * 如何知道多少个线程已经被使用，如何映射到已经创建的 Channel上都需要依赖于 EventLoopGroup
         * 的实现，并且可以通过构造函数来配置他们的关系。
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap 是一个启动 NIO 服务的辅助启动类。
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 新的 Channel 如何接收进来的连接。
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // option() 是提供给NioServerSocketChannel 用来接收进来的连接。
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // childOption() 是提供给由父管道 ServerChannel 接收到的连接
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 这里的事件处理类经常会被用来处理一个最近的已经接收的 Channel。
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 等待连接关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生：",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
