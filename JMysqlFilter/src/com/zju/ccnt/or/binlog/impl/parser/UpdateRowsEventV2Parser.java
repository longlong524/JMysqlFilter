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
import com.zju.ccnt.or.binlog.impl.event.TableMapEvent;
import com.zju.ccnt.or.binlog.impl.event.UpdateRowsEventV2;
import com.zju.ccnt.or.common.glossary.Pair;
import com.zju.ccnt.or.common.glossary.Row;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class UpdateRowsEventV2Parser extends AbstractRowEventParser {

	/**
	 * 
	 */
	public UpdateRowsEventV2Parser() {
		super(UpdateRowsEventV2.EVENT_TYPE);
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
		final UpdateRowsEventV2 event = new UpdateRowsEventV2(header);
		event.setTableId(tableId);
		event.setReserved(is.readInt(2));
		event.setExtraInfoLength(is.readInt(2));
		if (event.getExtraInfoLength() > 2)
			event.setExtraInfo(is.readBytes(event.getExtraInfoLength() - 2));
		event.setColumnCount(is.readUnsignedLong());
		event.setUsedColumnsBefore(is.readBit(
				event.getColumnCount().intValue(), true));
		event.setUsedColumnsAfter(is.readBit(event.getColumnCount().intValue(),
				true));
		event.setRows(parseRows(is, tme, event));
		return event;
	}

	/**
	 * 
	 */
	protected List<Pair<Row>> parseRows(XInputStream is, TableMapEvent tme,
			UpdateRowsEventV2 ure) throws IOException {
		final List<Pair<Row>> r = new LinkedList<Pair<Row>>();
		while (is.available() > 0) {
			final Row before = parseRow(is, tme, ure.getUsedColumnsBefore());
			final Row after = parseRow(is, tme, ure.getUsedColumnsAfter());
			r.add(new Pair<Row>(before, after));
		}
		return r;
	}
}
