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

import com.zju.ccnt.or.common.glossary.column.StringColumn;
import com.zju.ccnt.or.common.util.XDeserializer;
import com.zju.ccnt.or.common.util.XSerializer;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class ErrorPacket extends RawPacket {

	//
	public static final byte PACKET_MARKER = (byte) 0xFF;

	//
	private int packetMarker;
	private int errorCode;
	private StringColumn slash;
	private StringColumn sqlState;
	private StringColumn errorMessage;

	/**
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("packetMarker", packetMarker)
				.append("errorCode", errorCode).append("slash", slash)
				.append("sqlState", sqlState)
				.append("errorMessage", errorMessage).toString();
	}

	/**
	 * 
	 */
	public byte[] getPacketBody() {
		final XSerializer s = new XSerializer(64);
		s.writeInt(this.packetMarker, 1);
		s.writeInt(this.errorCode, 2);
		s.writeFixedLengthString(this.slash);
		s.writeFixedLengthString(this.sqlState);
		s.writeFixedLengthString(this.errorMessage);
		return s.toByteArray();
	}

	/**
	 * 
	 */
	@Override
	public int getPacketMarker() {
		return packetMarker;
	}

	public void setPacketMarker(int fieldCount) {
		this.packetMarker = fieldCount;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public StringColumn getSlash() {
		return slash;
	}

	public void setSlash(StringColumn slash) {
		this.slash = slash;
	}

	public StringColumn getSqlState() {
		return sqlState;
	}

	public void setSqlState(StringColumn sqlState) {
		this.sqlState = sqlState;
	}

	public StringColumn getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(StringColumn errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 */
	public static ErrorPacket valueOf(AbstractPacket packet) throws IOException {
		final XDeserializer d = new XDeserializer(packet.getBytes(), 4,
				packet.getBytes().length - 4);
		final ErrorPacket r = new ErrorPacket();
		r.length = packet.getLength();
		r.sequence = packet.getSequence();
		r.packetMarker = d.readInt(1);
		r.errorCode = d.readInt(2);
		r.slash = d.readFixedLengthString(1);
		r.sqlState = d.readFixedLengthString(5);
		r.errorMessage = d.readFixedLengthString(d.available());
		return r;
	}

	public static ErrorPacket valueOf(int packetLength, int packetSequence,
			int packetMarker, XInputStream is) throws IOException {
		final ErrorPacket r = new ErrorPacket();
		r.length = packetLength;
		r.sequence = packetSequence;
		r.packetMarker = packetMarker;
		r.errorCode = is.readInt(2);
		r.slash = is.readFixedLengthString(1);
		r.sqlState = is.readFixedLengthString(5);
		r.errorMessage = is.readFixedLengthString(is.available());
		return r;
	}

}
