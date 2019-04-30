package com.ccnu.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by 董乐强 on 2019/4/30
 *
 * 学习CuratorApi的一些操作
 */
public class CuratorApiDemo {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        System.out.println("连接成功....");
        //新建节点
        try {
           String data = curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath("/curator/curator1","curator".getBytes());
            System.out.println(data);
        } catch (Exception e) {
            System.out.println("节点已经创建");
        }
/*
        //删除节点  默认情况下，version为-1
        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/curator");*/
        Stat stat = new Stat();
       //查询节点
        byte[] datas = curatorFramework.getData().storingStatIn(stat).forPath("/curator/curator1");

        System.out.println(new String(datas));

        System.out.println(stat);

        //更新节点
        Stat stat1 = curatorFramework.setData().forPath("/curator/curator1","dlq111".getBytes());
        System.out.println(stat1);
        /**
         * 异步操作
         */
        //设置线程池
        ExecutorService service= Executors.newFixedThreadPool(1);
        final CountDownLatch countDownLatch=new CountDownLatch(1);
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).
                    inBackground(new BackgroundCallback() {
                        public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                            System.out.println(Thread.currentThread().getName()+"->resultCode:"+curatorEvent.getResultCode()+"->"
                            +curatorEvent.getType());
                            countDownLatch.countDown();
                        }
                    },service).forPath("/dlq","123".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.await();
        service.shutdown();

        //事务操作
        try {
            Collection<CuratorTransactionResult> resultCollections=curatorFramework.inTransaction().create().forPath("/dlqtrans","111".getBytes()).and().
                    setData().forPath("/curator","111".getBytes()).and().commit();
            for (CuratorTransactionResult result:resultCollections){
                System.out.println(result.getForPath()+"->"+result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }





}
