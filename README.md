
#学习zookeeper的基础知识、zookeepr的使用场景

####1. 调用zookeeper服务端，总共有三种方法，分别是
1. java的原生Api 
2. 使用zkclient
3. 使用框架curator

####2. zookeeper的原理
1. zookeeper集群中包含三个角色，分别为leader、follower、observer
2. observer是为了扩展zookeeper集群，不参与leader的选举。这样，observer在扩展zookeeper集群的同时，不会因为选举
zookeeper时，带来的性能开销。
3. leader节点就是zookeeper中的主节点，客户端读操作一定且只请求到leader节点来进行操作，读请求会根据zookeeper内部的
算法来分配到集群中指定的节点。
   leader是zookeeper集群的核心，主要功能有两点：1.事务请求的唯一调度者和处理者，保证集群事务处理的顺序性。2.集群内部各个节点的调度者.
4. follower是zookeeper集群中的从节点，当主节点leader挂掉后，参与投票选举主节点。
   follower主要作用有三点：1.处理客户端非事务请求，以及转发事务请求给leader服务器 2.参与事务请求提议（proposal）的投票（客户端的一个事务请求，需要半数服务器投票通过以后才能通知leader commit； leader会发起一个提案，要求follower投票）3.参与leader的选举

5. zookeeper最小的数据单元是一个节点，即/node ，节点可以保存数据，同时节点下也可以挂载子节点。类似操作系统的
文件系统。zookeeper中的节点有四种类型：1.持久化无序节点 2.持久化有序节点 3.临时有序节点 4. 临时无序节点
 zookeeper中随着客户端断开连接，临时节点自动删除。
6. zookeeper中的watcher,就是客户端可以为服务端任意节点注册个watcher事件，当服务端节点发生watcher事件的时候，则服务端会通知客户端
通知后，watcher事件失效。

7. zookeeper可以控制节点的访问权限，即acl。这样可以保证zookeeper节点访问的安全性。很容易想到，节点的操作必须设置权限控制，就像mysql数据库也要设置权限。



####3.zookeeper的使用场景
1.数据的发布和订阅(配置中心)












2.dubbo利用zookeeper实现负载均衡
3.master选举(kafka、hadoop/hbase)
4.命名服务
5.分布式锁
6.分布式队列
7.id生成器
