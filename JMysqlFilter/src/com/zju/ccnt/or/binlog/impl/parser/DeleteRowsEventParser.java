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
package com.zju.ccnt.or.binlog.impl.parser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.zju.ccnt.or.binlog.BinlogEventV4Header;
import com.zju.ccnt.or.binlog.BinlogParserContext;
import com.zju.ccnt.or.binlog.impl.event.AbstractBinlogEventV4;
import com.zju.ccnt.or.binlog.impl.event.DeleteRowsEvent;
import com.zju.ccnt.or.binlog.impl.event.TableMapEvent;
import com.zju.ccnt.or.common.glossary.Row;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class DeleteRowsEventParser extends AbstractRowEventParser {

	/**
	 * 
	 */
	public DeleteRowsEventParser() {
		super(DeleteRowsEvent.EVENT_TYPE);
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	@Override
	public AbstractBinlogEventV4 parse(XInputStream is, 
			BinlogEventV4Header header, BinlogParserContext context)
	throws Exception {
		//
		final long tableId = is.readLong(6);
		final TableMapEvent tme = context.getTableMapEvent(tableId);
		
		//
		final DeleteRowsEvent event = new DeleteRowsEvent(header);
		event.setTableId(tableId);
		event.setReserved(is.readInt(2));
		event.setColumnCount(is.readUnsignedLong()); 
		event.setUsedColumns(is.readBit(event.getColumnCount().intValue(), true));
		event.setRows(parseRows(is, tme, event));
		return event;
	}
	
	/**
	 * 
	 */
	protected List<Row> parseRows(XInputStream is, TableMapEvent tme, DeleteRowsEvent dre)
	throws IOException {
		final List<Row> r = new LinkedList<Row>();
		while(is.available() > 0) {
			r.add(parseRow(is, tme, dre.getUsedColumns()));
		}
		return r;
	}
}
