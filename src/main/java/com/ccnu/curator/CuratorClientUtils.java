package com.ccnu.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * created by 董乐强 on 2019/4/30
 * curator开发包操作zookeeper的工具类
 * https://www.jianshu.com/p/6ef36f8d5802
 */
public class CuratorClientUtils {


    private static CuratorFramework curatorFramework;
    private final static String CONNECTSTRING="182.254.196.174:2181";


    public static CuratorFramework getInstance(){
        //遵从fluex风格，参数1：连接地址 。 参数2：session超时时间  参数3：连接超时时间
        //new ExponentialBackoffRetry表示重试机制，
        // 就是客户端与服务端断开连接后，客户端需要重试连接，例如： new ExponentialBackoffRetry(1000,3)
        // 表示为每隔1秒重试一次，重试三次不在继续尝试连接服务端

        curatorFramework= CuratorFrameworkFactory.
                newClient(CONNECTSTRING,5000,5000,
                        new ExponentialBackoffRetry(1000,3));
        curatorFramework.start();
        return curatorFramework;
    }

    //使用该中方法，加入认证信息。
    public static CuratorFramework getInstance1(){

        curatorFramework = CuratorFrameworkFactory.builder().authorization("digest","dlq:dlq".getBytes())
                .connectString("").sessionTimeoutMs(5000).connectionTimeoutMs(5000).
                        retryPolicy(new ExponentialBackoffRetry(1000,3)).build();
        curatorFramework.start();
        return curatorFramework;

    }


}
