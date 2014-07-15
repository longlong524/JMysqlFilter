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
import java.io.InputStream;

import com.zju.ccnt.or.net.TransportInputStream;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;
import com.zju.ccnt.or.net.impl.packet.RawPacket;

/**
 * 
 * @author Xianglong Yao
 */
public class TransportInputStreamImpl extends XInputStreamImpl implements TransportInputStream {

	/**
	 * 
	 */
	public TransportInputStreamImpl(InputStream is) {
		super(is);
	}
	
	public TransportInputStreamImpl(InputStream is, int size) {
		super(is, size);
	}

	/**
	 * 
	 */
	@Override
	public AbstractPacket readPacket() throws IOException {
		//
		final RawPacket r = new RawPacket();
		int len=readInt(3);
		int sequence=readInt(1);
		r.setLength(len);
		r.setSequence(sequence);
		
		int total = 0;
		final byte[] body = new byte[len+4];
		for(int i=0;i<3;i++){
			body[i]=(byte) (0x00ff&(len>>>(i<<3)));
		}
		body[3]=(byte) (0x00ff&sequence);
		while(total < len) {
			total += this.read(body, total+4, len - total);
		}
		r.setBytes(body);
		return r;
	}
}
