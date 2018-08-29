package com.myzk.bigdata.zkdist;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

public class DistributedServer {
	private static final String CONNECT_STRING = "192.168.247.20:2181,192.168.247.21:2181,192.168.247.22:2181";
	private static final int CONNECTTIMEOUT = 2000;
	private static final String rootPath ="/servers";
	
	private ZooKeeper zk = null;
	
	CountDownLatch latch = new CountDownLatch(1);
	
	/**
	 * 创建到客户端连接
	 * @throws Exception 
	 */
	public void getConnect() throws Exception {
		zk = new ZooKeeper(CONNECT_STRING,CONNECTTIMEOUT,new Watcher() {
			
			public void process(WatchedEvent event) {
				if(event.getState()==KeeperState.SyncConnected) {
					latch.countDown();
	  			}
			}
		});
		latch.await();
	}
	
	/**
	 * 向zk集群注册服务器信息
	 * @param hostname
	 * @throws Exception
	 */
	public void registerServer(String hostname) throws Exception {
		Stat exists = zk.exists(rootPath, false);
		if(exists==null) {
			zk.create(rootPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		String path = zk.create(rootPath+"/server",hostname.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println(hostname + "is online..." +path);
	}
	
	/**
	 * 业务功能
	 * @param hostname
	 * @throws InterruptedException
	 */
	public void handleBussiness(String hostname) throws InterruptedException {
		System.out.println(hostname+ "start working......");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception{
		DistributedServer server = new DistributedServer();
		//获取zk连接
		server.getConnect();
		//利用zk连接注册服务器信息（主机名）
		server.registerServer(args[0]);
		//启动业务功能
		server.handleBussiness(args[0]);
		
	}
	
}
















