package com.ocean.netty.dubborpc.publicinterface;

//这个是接口  是服务提供方 和 服务消费方 都需要
public interface HelloService {
    String hello(String mes);
}
