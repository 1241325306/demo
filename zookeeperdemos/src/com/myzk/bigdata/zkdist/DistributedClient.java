package com.myzk.bigdata.zkdist;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DistributedClient {
	private static final String CONNECT_STRING = "192.168.247.20:2181,192.168.247.21:2181,192.168.247.22:2181";
	private static final int CONNECTTIMEOUT = 2000;
	private static final String rootPath ="/servers";
	//注意：加volatile的意义何在？ 
	private volatile List<String> serverList;
	private ZooKeeper zk =null;
	
	/**
	 * 创建到zk的客户端连接
	 * @throws Exception
	 */
	public void getConnect() throws Exception {
		zk = new ZooKeeper(CONNECT_STRING, CONNECTTIMEOUT, new Watcher() {
			
			public void process(WatchedEvent event) {
				//收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
					try {
						//重新更新服务器列表，并且注册了监听
						getServerList();
						
					}catch(Exception e) {
						
					}
			}
			
		});
	}
	
	/**
	 * 获取服务器信息列表
	 * @throws Exception
	 */
	private void getServerList() throws Exception {
		//获取服务器子节点信息，并且对父节点进行监听
		List<String> children = zk.getChildren(rootPath, true);
		
		//先创建一个局部list来存储服务器信息
		List<String> servers = new ArrayList<String>();
		for(String child:children) {
			byte[] data = zk.getData(rootPath, false, null);
			servers.add(new String(data));
		}
		
		//把servers赋值给成员变量serverList，已提供给各业务线程使用
		serverList =servers;
		
	
		//打开服务器列表
		System.out.println(serverList);

	}
	
	/**
	 * 业务功能
	 * @throws InterruptedException
	 */
	public void handleBussiness() throws InterruptedException {
		System.out.println("client start working......");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception {
		DistributedClient client = new DistributedClient();
		
		//获取zk连接
		client.getConnect();
		
		//获取servers的子节点信息（并监听）,从中获取服务器信息列表
		client.getServerList();
		
		//业务线程启动
		client.handleBussiness();
		
	
	}
}
