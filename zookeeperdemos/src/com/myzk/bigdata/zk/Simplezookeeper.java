package com.myzk.bigdata.zk;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.junit.Before;
import org.junit.Test;

public class Simplezookeeper {
	private static final String connectString ="192.168.247.20:2181,192.168.247.21:2181,192.168.247.22:2181";
	private static final int sessionTimeout = 2000;
	CountDownLatch latch = new CountDownLatch(1);
	ZooKeeper zookclient = null;
	
	@Before
	public void init() throws Exception {
		zookclient  = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			
			public void process(WatchedEvent event) {
				if(latch.getCount()>0 && event.getState()==KeeperState.SyncConnected) {
					System.out.println("countdown");
					latch.countDown();
				}
				
				// 收到事件通知后的回调函数（应该是我们自己的事件处理逻辑）
				System.out.println(event.getType()+"------"+event.getPath());
				System.out.println(event.getState());
			}
		});
		latch.await();
	}
	
	/**
	 * 数据的增删改查
	 * 
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	
	/**
	 * 创建一个数据节点到zk中
	 */
	@Test
	public void testCreate() throws KeeperException, InterruptedException {
		//参数1、创建的节点  /参数2、节点的数据 /参数3、节点的权限 /节点的类型
		String nodeCreated = zookclient.create("/eclipse", "hellozk".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zookclient.close();
	}
	
	/**
	 * 判断节点是否存在
	 * @throws Exception 
	 */
	@Test
	public void testExist() throws Exception {
		Stat stat = zookclient.exists("/eclipse", false);
		System.out.println(stat == null? "not exist":stat);
	}
	
	/**
	 * 获取子节点
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	@Test
	public void testgetChilren() throws KeeperException, InterruptedException {
		List<String> children = zookclient.getChildren("/eclipse", true);
		for(String child: children) {
			System.out.println(child);
		}
		Thread.sleep(Long.MAX_VALUE);
	}
	
	/**
	 * 获取子节点数据
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	@Test
	public void testgetDate() throws KeeperException, InterruptedException {
		
		byte[] data = zookclient.getData("/eclipse", true, null);
		System.out.println(new String(data));
		Thread.sleep(Long.MAX_VALUE);
		
	}
	
	/**
	 * 删除节点
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	@Test
	public void testdelete() throws InterruptedException, KeeperException {
		zookclient.delete("/eclipse/app1", -1);
	}
	
	@Test
	public void testset() throws KeeperException, InterruptedException {
		byte[] databefore = zookclient.getData("/eclipse", false, null);
		System.out.println("修改后的节点内容为 ："+new String(databefore));
		zookclient.setData("/eclipse", "123".getBytes(), -1);
		byte[] data = zookclient.getData("/eclipse", false, null);
		System.out.println("修改后的节点内容为 ："+new String(data));
	}

}
