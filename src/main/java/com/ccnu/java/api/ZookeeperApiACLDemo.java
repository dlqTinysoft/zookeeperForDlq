package com.ccnu.java.api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * created by 董乐强 on 2019/4/29
 *
 * 学习zookeeper节点的权限控制，即ACL
 * 该示例演示zookeeper的节点权限控制，ACL
 *
 * https://blog.csdn.net/en_joker/article/details/78771936
 * https://blog.csdn.net/gangsijay888/article/details/82426475
 * https://blog.51cto.com/zero01/2108483
 * https://blog.csdn.net/u010156024/article/details/50151029
 */
public class ZookeeperApiACLDemo implements Watcher {

    private final static String CONNECTSTRING="182.254.196.174:2181";
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private static CountDownLatch countDownLatch2=new CountDownLatch(1);

    private static ZooKeeper zookeeper;
    //封装节点的状态信息
    private static Stat stat=new Stat();
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException, NoSuchAlgorithmException {
        //客户端向服务端发起创建连接。 1.连接地址 2.连接超时时间 3.注册watcher来监听连接状态
        zookeeper=new ZooKeeper(CONNECTSTRING, 5000, new ZookeeperApiACLDemo());
        countDownLatch.await();

        //自定义权限， 创建节点时，需要权限user:password 即root:root。其中digest为提供的验证方式。
        //赋予权限必须 使用DigestAuthentication进行加密处理。
        //权限认证，包括两步：1.认证 2.赋予权限

        //自定义权限认证。 第一个参数：要赋予的权限  第二个参数认证方式。使用用户名密码模式进行认证，用户名和密码必须加密处理。
        /**
         * 赋予节点权限：ZooDefs.Perms.* 赋予节点的权限总共有五种，分别为
         *         int READ = 1; 可以读取节点数据的权限，以及当前节点的子节点列表
         *         int WRITE = 2; 可以向节点写数据的权限
         *         int CREATE = 4; 创建子节点的权限
         *         int DELETE = 8; 删除子节点的权限
         *         int ADMIN = 16;  可以为节点设置权限
         *         //包括上面五种权限
         *         int ALL = 31;
         *
         * 节点认证方式： 有四种认证方式
         *         world：默认方式，相当于全世界都能访问。
         *         auth：不使用任何id，表示任何经过身份验证的用户。
         *         digest：即用户名:密码这种方式认证，这也是业务系统中最常用的,
         *                使用username:password字符串生成MD5哈希，然后将其用作ACL的ID标识。
         *                例如： new Id("digest", DigestAuthenticationProvider.generateDigest("dlq:dlq"))
         *         ip：使用Ip地址认证。例如：new Id("ip","192.168.1.134") //ip地址表示客户端的ip
         *         四种方式中，digest方式最常用
         */
        ACL acl=new ACL(
                ZooDefs.Perms.ALL,
                new Id("digest", DigestAuthenticationProvider.generateDigest("dlq:dlq")));
        //自定义权限，使用ip进行验证，1.创建权限  2.使用ip模式来进行验证，ip地址为客户端ip
        //ACL acl2=new ACL(ZooDefs.Perms.CREATE, new Id("ip","192.168.1.134"));

        //添加权限
        List<ACL> acls=new ArrayList<ACL>();
        acls.add(acl);

        //创建节点的时候，给节点添加权限认证。当客户端，操作该节点时，必须认证通过并有相应操作权限才可以访问。
        //String dlq = zookeeper.create("/testdlq4","dlq".getBytes(),acls,CreateMode.PERSISTENT);

        Thread.sleep(1000);
        //使用用户名和密码，进行认证。
        zookeeper.addAuthInfo("digest","dlq:dlq".getBytes());
        //System.out.println(dlq);
         List<ACL> testAcls = zookeeper.getACL("/testdlq4",stat);

         //认证通过后，且有获取节点数据的权限，才可以获取节点的数据
         byte []  data = zookeeper.getData("/testdlq4",true,stat);

         System.out.println(new String(data));
         for(ACL tAcl :testAcls){
             System.out.println(tAcl.getPerms()+"---"+tAcl.getId());
         }
        /*//acls.add(acl2);
        zookeeper.addAuthInfo("digest","root:root".getBytes());
        //创建节点的时候，添加权限
        zookeeper.create("/auth8/auth8-1","auth8".getBytes(),acls, CreateMode.PERSISTENT);
*/
       /* //添加验证信息
        zookeeper.addAuthInfo("digest","root:root".getBytes());
        byte [] data = zookeeper.getData("/auth8",true,stat);

        System.out.println(new String(data));*/

      /*
        zookeeper.addAuthInfo("digest","root:root".getBytes());
        zookeeper.create("/auth1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zookeeper.create("/auth1/auth1-1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);


        ZooKeeper zooKeeper1=new ZooKeeper(CONNECTSTRING, 5000, new ZookeeperApiACLDemo());
        countDownLatch.await();
        zooKeeper1.delete("/auth1",-1);*/

        // acl (create /delete /admin /read/write)
        //权限模式： ip/Digest（username:password）/world/super

    }
    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
            if(Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }
        }

    }
}
