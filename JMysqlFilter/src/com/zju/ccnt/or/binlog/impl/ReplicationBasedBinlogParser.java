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
package com.zju.ccnt.or.binlog.impl;

import java.util.List;

import com.zju.ccnt.or.binlog.BinlogEventParser;
import com.zju.ccnt.or.binlog.LogEventType;
import com.zju.ccnt.or.binlog.impl.event.AbstractBinlogEventV4;
import com.zju.ccnt.or.binlog.impl.event.BinlogEventV4HeaderImpl;
import com.zju.ccnt.or.common.util.XDeserializer;
import com.zju.ccnt.or.net.ExceedLimitException;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;

/**
 * 
 * @author Xianglong Yao
 */
public class ReplicationBasedBinlogParser extends AbstractBinlogParser {

	private Context context;
	private byte[] data = new byte[1000 * 1000];

	public Context getContext() {
		return context;
	}

	/**
	 * 
	 */
	public ReplicationBasedBinlogParser() {
		context = new Context();
	}

	/**
	 * 
	 */
	@Override
	public AbstractBinlogEventV4 doParse(List<AbstractPacket> list)
			throws Exception {
		int len = 0;
		for (int i = 0; i < list.size(); i++) {
			len += list.get(i).getLength();
		}
		if (data == null || data.length < len) {
			data = new byte[len];
		}
		int pos = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLength() > 0) {
				System.arraycopy(list.get(i).getBytes(), 5, data, pos, list
						.get(i).getLength() - 1);
				pos += list.get(i).getLength() - 1;
			}
		}
		XDeserializer is = new XDeserializer(data, 0, pos);
		try {
			// Parse the event header
			final BinlogEventV4HeaderImpl header = new BinlogEventV4HeaderImpl();
			header.setTimestamp(is.readLong(4) * 1000L);
			header.setEventType(LogEventType.get(is.readInt(1)));
			header.setServerId(is.readLong(4));
			header.setEventLength(is.readInt(4));
			header.setNextPosition(is.readLong(4));
			header.setFlags(is.readInt(2));
			header.setTimestampOfReceipt(System.currentTimeMillis());

			// Parse the event body
			BinlogEventParser parser = getEventParser(header.getEventType());
			if (parser == null)
				parser = this.defaultParser;

			AbstractBinlogEventV4 event = parser.parse(is, header, context);
			context.accept(event);
			// Ensure the packet boundary
			/*
			 * if(is.available() != 0) { throw new
			 * NestableRuntimeException("assertion failed, available: " +
			 * is.available() + ", event type: " + header.getEventType()); }
			 */
			return event;
		} catch (ExceedLimitException e) {
			is.setReadLimit(0);
		}
		return null;
	}

}
