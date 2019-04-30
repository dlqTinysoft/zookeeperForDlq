package com.ccnu.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * created by 董乐强 on 2019/4/30
 *
 * 学习zkclient工具包来操作zookeeper
 *
 *
 *
 */
public class ZkClientDemo {

    private final static String CONNECTSTRING="182.254.196.174:2181";

    private static ZkClient  getInstance(){
        //建立连接
        return new ZkClient(CONNECTSTRING,10000);
    }

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient=getInstance();
        //zkclient 提供递归创建父节点的功能
       /* zkClient.createPersistent("/zkclient/zkclient1/zkclient1-1/zkclient1-1-1",true);
        System.out.println("success");*/

        //删除节点
//        zkClient.deleteRecursive("/zkclient");
        // zkClient.createPersistent("/node/node1",true);
        //这样创建节点会失败的，zkClient里面提供了递归创建节点的重载方法。
        //zkClient.createPersistent("/nodeT/t1","nodet");
        //获取子节点
        List<String> list=zkClient.getChildren("/node");

        System.out.println(list);
        //watcher
        zkClient.subscribeDataChanges("/node", new IZkDataListener() {

            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("节点名称："+s+"->节点修改后的值"+o);
            }


            public void handleDataDeleted(String s) throws Exception {

            }
        });
        zkClient.writeData("/node","node");
        TimeUnit.SECONDS.sleep(2);

        //只监听到删除节点的变化
        zkClient.subscribeChildChanges("/node", new IZkChildListener() {

            public void handleChildChange(String s, List<String> list) throws Exception {
                System.out.println("监听事件进来了吗");
                System.out.println(s);
                          for(String value :list){
                              System.out.println(value);
                          }
            }
        });
        zkClient.writeData("/node/node1","dlq");

        zkClient.writeData("/node/node1","dlqData");

        zkClient.delete("/node/node1");

        TimeUnit.SECONDS.sleep(2);

    }
}
