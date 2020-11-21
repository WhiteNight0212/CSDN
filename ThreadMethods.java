package com.Thread;

import java.util.Arrays;

public class ThreadMethods  {
	static int index = 0;
	public static void main(String[] args) throws InterruptedException {
		 BreadCon breadCon = new BreadCon();
		 Thread p1 = new Thread(new ProducerRunnable(breadCon), "生产者1号");
		 Thread c1 = new Thread(new ConsumerRunnable(breadCon), "消费者1号");
		 Thread p2 = new Thread(new ProducerRunnable(breadCon), "生产者2号");
		 Thread c2 = new Thread(new ConsumerRunnable(breadCon), "消费者2号");
		 p1.start(); c1.start();
		 p2.start(); c2.start();
	}
}
class BreadProduct {
	private int id ;
	private String ProductName;
	
	public BreadProduct() {
		super();
	}
	public BreadProduct(String productName) {
		super();
		this.ProductName = productName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	@Override
	public String toString() {
		return "BreadProduct [id=" + id + ", ProductName=" + ProductName + "]";
	}
}
class BreadCon { //仓库
	private BreadProduct[] breads = new BreadProduct[20];
	private int index = 0;
	private int breadId = 0;
	
	public synchronized void input(BreadProduct bread) throws InterruptedException {
		while(index > (breads.length-1))
			this.wait();
		bread.setId(++breadId);
		breads[index++] = bread;
		System.out.println(Thread.currentThread().getName() + "生产了"+bread.getId()+"号面包");
		this.notifyAll();
	}
	public synchronized void output() throws InterruptedException {
		while(index < 1)
			this.wait();
		--index;
		System.out.println(Thread.currentThread().getName() + "消费了"
				+ breads[index].getId() + "号面包，该面包生产者："
				+ breads[index].getProductName());
		breads[index] = null;
		this.notifyAll();
	}
}
class ProducerRunnable implements Runnable{
	private BreadCon breadCon;
	
	public ProducerRunnable(BreadCon breadCon) {
		super();
		this.breadCon = breadCon;
	}
	public void run() {
		try {
			for (int i = 0; i < 30; i++)
				breadCon.input(new BreadProduct(Thread.currentThread().getName()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class ConsumerRunnable implements Runnable{
	private BreadCon breadCon;
	
	public ConsumerRunnable(BreadCon breadCon) {
		super();
		this.breadCon = breadCon;
	}
	public void run() {
		try {
			for (int i = 0; i < 30; i++)
				breadCon.output();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	
class BankCard {//银行卡类
	private double money = 0;
	private boolean flag = false;
	//true:有钱，能取，不能存
	//false：没钱,不能取，能存
	public synchronized void addMoney() throws InterruptedException {
		if(flag) 	// this——>main（）中的 BankCard card 对象，保证了锁的唯一型
			this.wait();//有钱，不能再存了
		money += 1000; // 存钱
		System.out.println(Thread.currentThread().getName()+"存了1000， 余额："+money);
		flag = true; // 修改标记，可以取钱了，不能再存钱
		this.notify(); //随机唤醒等待队列里的   一个   线程
	}
	public synchronized void subMoney() throws InterruptedException {
		if(!flag) 	// flag = false 时进入等待队列
			this.wait();//没钱，不能再取了
		money -= 1000; // 取钱
		System.out.println(Thread.currentThread().getName()+"取了1000， 余额："+money);
		flag = false; // 修改标记，可以存钱了，不能再取钱
		this.notify(); //随机唤醒等待队列里的   一个   线程
	}
}
class SubMoneyRunnable implements Runnable{
	private BankCard card;
	public SubMoneyRunnable(BankCard card) {
		this.card = card;
	}
	public void run() {
		while(true)
			try {
				card.subMoney();
//				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
class AddMoneyRunnable implements Runnable{
	private BankCard card;
	public AddMoneyRunnable(BankCard card) {
		this.card = card;
	}
	public void run() {
		while(true) {
			try {
				card.addMoney();
//				Thread.sleep(100);//将两个run里的休眠注销，才容易看到这次的问题 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
class MyLock {
	static Object a = "a"; static Object b = "b";
	static Object c = "c"; static Object d = "d";
}
class BoyRunnable implements Runnable{
	public void run() {
		synchronized (MyLock.a) {
			System.out.println("男孩拿到a筷子");
			synchronized (MyLock.b) {
				System.out.println("男孩拿到了b筷子");
				System.out.println("男孩可以吃饭了");
			}
		}
	}
}
class GirlRunnable implements Runnable{
	public void run() {
		synchronized (MyLock.b) {
			System.out.println("女孩拿到b筷子了");
			synchronized (MyLock.a) {
				System.out.println("女孩拿到了a筷子");
				System.out.println("女孩可以吃饭了");
			}
		}
	}
}
class BoysRunnable implements Runnable{//多情boys
	public void run() {
		synchronized (MyLock.a) {
			System.out.println("boys:拿到a锁，想继续拿b锁");
			synchronized(MyLock.b) {
				System.out.println("boys:拿到a锁和b锁");
				synchronized(MyLock.c) {
					System.out.println("boys:拿到a锁、b锁和c锁");
				}
			}
		}
	}
}
class OtherBoyRunnable implements Runnable{
	public void run() {
		System.out.println("OtherBoy:我什么锁都没拿到，我想要拿到a锁");
		synchronized (MyLock.a) {
			System.out.println("OtherBoy:我拿到a锁了。。。。。");
		}
	}
}
class SynchronizedMethod implements Runnable{
	private int ticket = 200;
	private Object ticketSocket= new Object();
	@Override
	public void run() { synMethod(); }
	//同步方法
	public synchronized void synMethod() {
		//锁是this，this是执行同步方法的对象——>main()里的TicketRunnable tickets
		while(true) {
			if(ticket <= 0)//访问共享资源
				break;
			System.out.println(Thread.currentThread().getName()
					+ "卖了第"+ticket +"张票");//访问共享资源
			ticket--;//修改共享资源
		}
	}
	//如果写成静态同步方法，如
	//public static synchronized void synMethod(){}
	//那么锁是类本身，————>SynchronizedMethod.class
}
class TicketRunnable implements Runnable{
	private int ticket = 200;
	private Object ticketSocket= new Object();
	@Override
	public void run() {
		synchronized (ticketSocket) {
			while(true) {
				if(ticket <= 0)//访问共享资源
					break;
				//ticketSocket是该同步代码块的锁，保证锁唯一就行，Object对象
				System.out.println(Thread.currentThread().getName()
						+ "卖了第"+ticket +"张票");//访问共享资源
				ticket--;//修改共享资源
			} } }
}

class DaemonThread extends Thread{
	public DaemonThread() {
		super();
	}
	public void run() {
		while(true)
			System.out.println("Daemon：我是守护线程，即后台线程 ,等所有前台线程结束后自动结束   "
					+Thread.currentThread().isDaemon());
	}
}
class PriorityThread implements Runnable{ 
	public PriorityThread() {
		super();
	}
	public PriorityThread(int p) {
		super();
		Thread.currentThread().setPriority(p);
	}
	@Override
	public void run()  {
		for (int i = 0; i < 50; i++) {
			System.out.print (Thread.currentThread().getName()+" Priority: "  + i);
		}
		System.out.println("\r\n"+Thread.currentThread().getPriority()+ " " +Thread.currentThread().getName()+" Priority: 我打印完了---------------------------");
	}
}

class JoinThread implements Runnable{
	@Override
	public void run() {
		int i =0 ;
		System.out.println("Join：插个队，等我全部打印完你再执行。");
		while(i<10) {
			System.out.println("Join：我这是第"+(++i)+"次运行-----------");
		} 
		System.out.println("Join：我全部打印完了，还你CPU。");
	}
}
class YieldThread implements Runnable{
	public void run() {
		int i =0 ;
		while(i<20) {
			++i; 
			yy();
			System.out.println("Yield：我这是第"+(++i)+"次运行");
		} 
	}
	void yy() {
		System.out.println("Yield：我放弃了，这次不打印，没心情");
		Thread.yield(); 
	}
}
class PojoThread implements Runnable{
	public PojoThread() {
		super();
	}
	public PojoThread(String name) {
		super();
		Thread.currentThread().setName(name);
	}
	public void run() {
		int i =0 ;
		while(i<10) {
			System.out.println("Pojo： 这是第"+(++i)+"次运行");
			//			System.out.println("Pojo：我就是不让join，不能惯他，这是第"+(++i)+"次运行");
		} 
	}
}
class SleepThread implements Runnable{
	public void run() {
		int i =0 ;
		while(true) {
			System.out.println("我这是第"+(++i)+"次运行，休息一秒");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
}