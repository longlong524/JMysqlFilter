package com.zju.ccnt.or.common.util;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.zju.ccnt.or.net.impl.packet.AbstractPacket;


public class ByteLinkedBlockingDeque{
	
	public static long waitTime=0;
	public static long writeTime=0;
	/**
	 * the real deque;
	 */
	private  LinkedBlockingDeque<AbstractPacket> deque;
	
	/**
	 * the size of elements,bytes
	 */
	private volatile   int size;
	/**
	 * the capacity of deque
	 */
	private int capacity;
	/**
	 * the lock
	 */
	final ReentrantLock lock = new ReentrantLock();


    /** Condition for waiting puts */
    private final Condition notFull = lock.newCondition();
	
	public ByteLinkedBlockingDeque(int capacity,int size){
		this.capacity=capacity;
		this.deque=new LinkedBlockingDeque<AbstractPacket>(size);
	}
	/**
	 * get size
	 * @return
	 */
	public int size(){
		final ReentrantLock lock = this.lock;
        lock.lock();
        try{
        	return size;
        }finally{
        	lock.unlock();
        }
	}
	/**
	 * get size
	 * @return
	 */
	public int capacity(){
		final ReentrantLock lock = this.lock;
        lock.lock();
        try{
        	return capacity;
        }finally{
        	lock.unlock();
        }
	}
	/**
	 * clear the list
	 */
	public void clear(){
		final ReentrantLock lock = this.lock;
        lock.lock();
        try{
        	size=0;
        	this.deque.clear();
        	this.notFull.signal();
        }finally{
        	lock.unlock();
        }
	}
	/**
	 * add to list tail
	 * @param p
	 * @return
	 * @throws InterruptedException
	 */
	public void offer(AbstractPacket p) {
		final ReentrantLock lock = this.lock;
        lock.lock();
        try{
        	while(size>capacity||deque.remainingCapacity()==0){
    			try {
					notFull.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
        	//long n2=System.currentTimeMillis();
        	//waitTime+=(n2-n1);
			while(!deque.offer(p));		
			size+=p.getLength()+4;
/*			long n3=System.currentTimeMillis();
			writeTime+=(n3-n2);
    		if(this.deque.size()%1000==0){
				//System.err.println("Write:"+this.deque.size()+" :deque"+this);
    		}*/
			return;
        }finally{
        	lock.unlock();
        }
	}
	/**
	 * get the list
	 * @param list
	 */
	public void drainTo(List<AbstractPacket> list){
		final ReentrantLock lock = this.lock;
        lock.lock();
        try{
        	deque.drainTo(list);
        	size=0;
        	this.notFull.signal();
        }finally{
        	lock.unlock();
        }
	}

	public LinkedBlockingDeque<AbstractPacket> getDeque() {
		return deque;
	}
	public void setDeque(LinkedBlockingDeque<AbstractPacket> deque) {
		this.deque = deque;
	}
}
