/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zju.ccnt.or.net.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.zju.ccnt.or.common.util.ByteLinkedBlockingDeque;
import com.zju.ccnt.or.common.util.MySQLConstants;
import com.zju.ccnt.or.net.TransportOutputStream;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;

/**
 * 
 * @author Xianglong Yao
 */
public class TransportOutputStreamImpl extends XOutputStreamImpl implements TransportOutputStream,Runnable {

	private AtomicBoolean isrun=new AtomicBoolean(false);
	
	private ByteLinkedBlockingDeque deque;

	private byte[] buffer;
	
	private IOException e;
	
	private long writeSocketInterval;
	
	private int maxSize;
	
	/**
	 * 
	 */
	public TransportOutputStreamImpl(OutputStream out,
			int bytecapacity,int size,long writeSocketInterval) {
		super(out);
		deque=new ByteLinkedBlockingDeque(bytecapacity,size);
		buffer=new byte[bytecapacity];
		e=null;
		maxSize=size;
		this.writeSocketInterval=writeSocketInterval;
		new Thread(this).start();
	}
	
	@Override
	public void writePacket(AbstractPacket p) throws Exception{
		if(e!=null){
			throw e;
		}
		this.deque.offer(p);
	}
	@Override
	public void close(){
		this.isrun.compareAndSet(true, false);
		try {
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		isrun.set(true);
		List<AbstractPacket> list=new ArrayList<AbstractPacket>(maxSize);
		//int kong=0;
		while(isrun.get()&&e==null){
			try {
				Thread.sleep(writeSocketInterval);
			} catch (InterruptedException e) {
			}
			//kong++;
			if(this.deque.size()==0){
/*				if(kong%20==0){
					System.err.println("kong:"+this.deque.size()+" :deque"+this.deque);
				}*/
				continue;
			}
			
			//kong=0;
			//System.err.println("before drain:"+list.size());

			//long n1=System.currentTimeMillis();
			this.deque.drainTo(list);
			//long n2=System.currentTimeMillis();
			//System.err.println("after drain:"+list.size()+" time:"+(n2-n1));
			int count=0;
			int i=0;
			try {
				while(i<list.size()
						&&count+list.get(i).getBytes().length<=buffer.length){
					System.arraycopy(list.get(i).getBytes(), 0, buffer, count, list.get(i).getBytes().length);
					count+=list.get(i).getBytes().length;
					i++;
				}
				this.writeBytes(buffer, 0, count);
				for(int j=i;j<list.size();j++){
					this.writePacket2(list.get(j));;
				}
				list.clear();
				//long n3=System.currentTimeMillis();
				//System.err.println("after writebytes:"+list.size()+" time:"+(n3-n2));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.e=e;
				this.isrun.set(false);
			}
		}
	}
	
	/**
	 * 
	 */
	public void writePacket2(AbstractPacket packet) throws IOException {
		//
		int len = packet.getLength();
		if(len < MySQLConstants.MAX_PACKET_LENGTH) { // Single packet
			//long n3=System.currentTimeMillis();
			writeBytes(packet.getBytes());
			//long n4=System.currentTimeMillis();
			//bodytime+=n4-n3;
			return;
		}
		
		// If the length of the packet is greater than the value of MAX_PACKET_LENGTH,
		// which is defined to be power(2, 24) �C 1 in sql/net_serv.cc, the packet gets 
		// split into smaller packets with bodies of MAX_PACKET_LENGTH plus the last 
		// packet with a body that is shorter than MAX_PACKET_LENGTH. 
		int offset = 0;
		int sequence = packet.getSequence();
		byte[] bytes=packet.getBytes();
		for(; offset + MySQLConstants.MAX_PACKET_LENGTH <= len; offset += MySQLConstants.MAX_PACKET_LENGTH) {
			writeInt(MySQLConstants.MAX_PACKET_LENGTH, 3);
			writeInt(sequence++, 1);
			writeBytes(bytes, offset+4, MySQLConstants.MAX_PACKET_LENGTH);
		}
		
		// The last short packet will always be present even if it must have a zero-length body.
		// It serves as an indicator that there are no more packet parts left in the stream for this large packet.
		writeInt(len - offset, 3);
		writeInt(sequence++, 1);
		writeBytes(bytes, offset+4, len - offset);
	}
	
	public ByteLinkedBlockingDeque getDeque() {
		return deque;
	}

	public void setDeque(ByteLinkedBlockingDeque deque) {
		this.deque = deque;
	}
}
