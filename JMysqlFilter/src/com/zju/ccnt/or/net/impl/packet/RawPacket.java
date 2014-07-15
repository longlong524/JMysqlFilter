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
package com.zju.ccnt.or.net.impl.packet;

import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author Xianglong Yao
 */
public class RawPacket extends AbstractPacket {
	
	//
	private byte packetBytes[];
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("length", length)
		.append("sequence", sequence)
		.append("packetBody",packetBytes).toString();
	}
	

	@Override
	public byte[] getBytes() throws IOException {
		// TODO Auto-generated method stub
		return packetBytes;
	}
	
	@Override
	public void setBytes(byte[] bytes) throws IOException {
		// TODO Auto-generated method stub
		this.packetBytes=bytes;
	}


	@Override
	public int getPacketMarker() {
		if(length>0){
			return packetBytes[4];
		}
		return -1;
	}

}
