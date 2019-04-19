package com.ccnu.java.api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * created by 董乐强 on 2019/4/19
 * 主要是用来演示，使用java原生api来操作zookeeper
 *
 *
 * 1.关于客户端像zookeeper服务端注册事件的问题，创建节点和删除节点，服务端不触发的事件，客户端收不到，或者说服务端根本就没有触发事件
 * 但是修改节点会触发事件，删除和创建子节点服务端会触发事件，并发送给客户端。
 *
 *
 */
public class ZookeeperApi implements Watcher {


    private final static String CONNECTSTRING="182.254.196.174:2181";
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private static ZooKeeper zookeeper;
    private static Stat stat=new Stat();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        //5000代表连接超时，时间。客户端连接服务端，如果超过5秒还连不上，就报连接超时。
        zookeeper=new ZooKeeper(CONNECTSTRING, 5000, new ZookeeperApi());
        countDownLatch.await(); //countDownLathch是个计数器，jdk5.0以后并发包中的
        //设置权限,ZooDefs.Perms.ALL 拥有所有权限，前提是只能改ip连接到服务端
        ACL acl=new ACL(ZooDefs.Perms.ALL,new Id("ip","182.254.196.174"));
        List<ACL> acls=new ArrayList<ACL>();
        acls.add(acl);
        //---------------操作服务端zookeeper的功能主要有，创建节点、删除节点，修改节点的数据，获取节点的数据
        //1.创建节点，创建持久化节点，CreateMode.PERSISTENT  acls代表，判断上传的节点时候有权限
        //zookeeper.create("/authTest","111".getBytes(),acls,CreateMode.PERSISTENT);
        //创建持久化节点，ZooDefs.Ids.OPEN_ACL_UNSAFE，给予节点赋予的权限，CreateMode.PERSISTENT创建持久化节点，节点总共有四种类型
        //zookeeper.create("/authTest1","111".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        //2.获取节点的数据
        //zookeeper.getData("/authTest1",true,new Stat()); //获取节点的数据，为节点注册一个Watch事件.
        //System.out.println(zookeeper.getState());

        //创建节点
        String result=zookeeper.create("/nodedlq","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zookeeper.getData("/nodedlq",true,stat); //增加一个
        System.out.println("创建成功："+result);

        //修改数据
        zookeeper.setData("/node1","123".getBytes(),-1); //传送version为-1，修改数据不需要关注版本号
        Thread.sleep(2000);
        //修改数据
        zookeeper.setData("/node1","234".getBytes(),-1);
        Thread.sleep(2000);

        //删除节点
        zookeeper.delete("/node1",-1);
        Thread.sleep(2000);
        //---------------------------------------------------------------------------------------------------



        //----------------------------------操作服务端zookeeper的子节点的功能：创建子节点，删除子节点，修改子节点，获取子节点数据。
        //创建节点和子节点
        String path="/node11";

        zookeeper.create(path,"123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        TimeUnit.SECONDS.sleep(1);

        Stat stat=zookeeper.exists(path+"/node1",true);
        if(stat==null){//表示节点不存在
            zookeeper.create(path+"/node1","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            TimeUnit.SECONDS.sleep(1);
        }
        //修改子节点的数据
        zookeeper.setData(path+"/node1","123".getBytes(),-1);
        TimeUnit.SECONDS.sleep(1);

        //获取指定节点下的子节点
       List<String> childrens=zookeeper.getChildren("/1",true);
        System.out.println(childrens);
        //============================================================================================================

    }


   //客户端给zk服务端注册个事件，服务端触发事件后会调用该回调方法
   public void process(WatchedEvent watchedEvent) {
  //如果当前的连接状态是连接成功的，那么通过计数器去控制

       //这个watch事件SyncConnected是默认注册的,必须连接上，zookeeper服务端才可以响应客户端事件
       //表示zookeeper连接到服务端是成功的
        if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
            //第一次建立连接，会走该逻辑
            if(Event.EventType.None==watchedEvent.getType()&&null==watchedEvent.getPath()){
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }else if(watchedEvent.getType()== Event.EventType.NodeDataChanged){
                try {
                    System.out.println("数据变更触发路径："+watchedEvent.getPath()+"->改变后的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));//继续注册事件
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged){//子节点的数据变化会触发
                try {
                    System.out.println("子节点数据变更路径："+watchedEvent.getPath()+"->节点的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(watchedEvent.getType()== Event.EventType.NodeCreated){//创建子节点的时候会触发
                try {
                    System.out.println("节点创建路径："+watchedEvent.getPath()+"->节点的值："+
                            zookeeper.getData(watchedEvent.getPath(),true,stat));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(watchedEvent.getType()== Event.EventType.NodeDeleted){//子节点删除会触发
                System.out.println("节点删除路径："+watchedEvent.getPath());
            }
            System.out.println(watchedEvent.getType());
        }
    }
}
