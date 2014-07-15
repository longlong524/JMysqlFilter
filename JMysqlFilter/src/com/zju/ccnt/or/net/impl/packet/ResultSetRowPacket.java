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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.zju.ccnt.or.common.glossary.column.StringColumn;
import com.zju.ccnt.or.common.util.XDeserializer;
import com.zju.ccnt.or.common.util.XSerializer;

/**
 * 
 * @author Xianglong Yao
 */
public class ResultSetRowPacket extends RawPacket {
		
	//
	private List<StringColumn> columns;
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("columns", columns).toString();
	}
	
	/**
	 * 
	 */
	public byte[] getPacketBody() {
		final XSerializer s = new XSerializer(1024);
		for(StringColumn column : this.columns) {
			s.writeLengthCodedString(column);
		}
		return s.toByteArray();
	}
	
	/**
	 * 
	 */
	public List<StringColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<StringColumn> columns) {
		this.columns = columns;
	}
	
	/**
	 * 
	 */
	public static ResultSetRowPacket valueOf(AbstractPacket packet) throws IOException {
		final XDeserializer d = new XDeserializer(packet.getBytes(),4,packet.getBytes().length-4);
		final ResultSetRowPacket r = new ResultSetRowPacket();
		r.length = packet.getLength();
		r.sequence = packet.getSequence();
		r.setColumns(new LinkedList<StringColumn>());
		while(d.available() > 0) {
			r.getColumns().add(d.readLengthCodedString());
		}
		return r;
	}
}
