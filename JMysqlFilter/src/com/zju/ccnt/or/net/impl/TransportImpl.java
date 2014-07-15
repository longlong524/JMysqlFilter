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
import java.net.Socket;

import com.zju.ccnt.or.common.util.IOUtils;
import com.zju.ccnt.or.net.TransportInputStream;
import com.zju.ccnt.or.net.TransportOutputStream;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;

/**
 * The trasportion to connect to mysql
 * @author yaoxianglong
 * @version 1.0
 */
public class TransportImpl extends AbstractTransport {	
	public static long writeTime=0;
	/**
	 * the socket
	 */
	protected Socket socket;
	/**
	 * the inputstream
	 */
	protected TransportInputStream is;
	/**
	 * the outputstream
	 */
	protected TransportOutputStream os;

	
	public TransportImpl(Socket socket,int readBufferSize,
			int writebuffersize,int writeSize
			,long writeSocketInterval) throws IOException{
		this.socket=socket;
		this.socket.setSendBufferSize(1024*16);
		this.is = new TransportInputStreamImpl(this.socket.getInputStream(),
				readBufferSize);
		this.os = new TransportOutputStreamImpl(this.socket.getOutputStream(),
				writebuffersize,writeSize,writeSocketInterval);
	}



	@Override
	public void disconnect() throws Exception {
		IOUtils.closeQuietly(this.is);
		IOUtils.closeQuietly(this.os);
		IOUtils.closeQuietly(this.socket);
	}
	


	public TransportInputStream getInputStream() {
		return this.is;
	}

	public TransportOutputStream getOutputStream() {
		return this.os;
	}


	@Override
	public AbstractPacket readPacket() throws IOException {
		return this.is.readPacket();
	}
	@Override
	public void writePacket(AbstractPacket packet) throws Exception {
		this.os.writePacket(packet);
	}



	@Override
	public void flush() throws IOException {
		this.os.flush();
	}


}
