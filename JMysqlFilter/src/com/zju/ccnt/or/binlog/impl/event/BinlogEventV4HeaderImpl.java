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
package com.zju.ccnt.or.binlog.impl.event;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.zju.ccnt.or.binlog.BinlogEventV4Header;
import com.zju.ccnt.or.binlog.LogEventType;

/**
 * 
 * @author Xianglong Yao
 */
public final class BinlogEventV4HeaderImpl implements BinlogEventV4Header {
	//
	private long timestamp;
	private LogEventType eventType;
	private long serverId;
	private long eventLength;
	private long nextPosition;
	private int flags;
	private long timestampOfReceipt;
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("timestamp", timestamp)
		.append("eventType", eventType)
		.append("serverId", serverId)
		.append("eventLength", eventLength)
		.append("nextPosition", nextPosition)
		.append("flags", flags)
		.append("timestampOfReceipt", timestampOfReceipt).toString();
	}
	
	/**
	 * 
	 */
	@Override
	public int getHeaderLength() {
		return 19;
	}
	
	@Override
	public long getPosition() {
		return this.nextPosition - this.eventLength;
	}
	
	/**
	 * 
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	

	
	@Override
	public long getServerId() {
		return serverId;
	}
	
	public void setServerId(long serverId) {
		this.serverId = serverId;
	}
	
	@Override
	public long getEventLength() {
		return eventLength;
	}
	
	public void setEventLength(long eventLength) {
		this.eventLength = eventLength;
	}
	
	@Override
	public long getNextPosition() {
		return nextPosition;
	}
	
	public void setNextPosition(long nextPosition) {
		this.nextPosition = nextPosition;
	}
	
	@Override
	public int getFlags() {
		return flags;
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	@Override
	public long getTimestampOfReceipt() {
		return timestampOfReceipt;
	}

	public void setTimestampOfReceipt(long timestampOfReceipt) {
		this.timestampOfReceipt = timestampOfReceipt;
	}

	@Override
	public LogEventType getEventType() {
		// TODO Auto-generated method stub
		return eventType;
	}
	
	public void setEventType(LogEventType let) {
		// TODO Auto-generated method stub
		this.eventType=let;
	}
}
