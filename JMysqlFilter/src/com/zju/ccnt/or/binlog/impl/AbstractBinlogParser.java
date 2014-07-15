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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zju.ccnt.or.binlog.BinlogEventFilter;
import com.zju.ccnt.or.binlog.BinlogEventParser;
import com.zju.ccnt.or.binlog.BinlogEventV4;
import com.zju.ccnt.or.binlog.BinlogParser;
import com.zju.ccnt.or.binlog.BinlogParserContext;
import com.zju.ccnt.or.binlog.LogEventType;
import com.zju.ccnt.or.binlog.impl.event.RotateEvent;
import com.zju.ccnt.or.binlog.impl.event.TableMapEvent;
import com.zju.ccnt.or.binlog.impl.parser.NopEventParser;

/**
 * 
 * @author Xianglong Yao
 */
public abstract class AbstractBinlogParser implements BinlogParser {

	protected boolean clearTableMapEventsOnRotate = true;
	protected final BinlogEventParser defaultParser = new NopEventParser();
	protected final BinlogEventParser[] parsers = new BinlogEventParser[128];

	/**
	 * 
	 */
	public AbstractBinlogParser() {
	}

	public boolean isClearTableMapEventsOnRotate() {
		return clearTableMapEventsOnRotate;
	}

	public void setClearTableMapEventsOnRotate(
			boolean clearTableMapEventsOnRotate) {
		this.clearTableMapEventsOnRotate = clearTableMapEventsOnRotate;
	}

	/**
	 * 
	 */
	public void clearEventParsers() {
		for (int i = 0; i < this.parsers.length; i++) {
			this.parsers[i] = null;
		}
	}

	public BinlogEventParser getEventParser(LogEventType type) {
		return this.parsers[type.ordinal()];
	}

	public BinlogEventParser unregistgerEventParser(int type) {
		return this.parsers[type] = null;
	}

	public void registgerEventParser(BinlogEventParser parser) {
		this.parsers[parser.getEventType()] = parser;
	}

	public void setEventParsers(List<BinlogEventParser> parsers) {
		clearEventParsers();
		if (parsers != null) {
			for (BinlogEventParser parser : parsers) {
				registgerEventParser(parser);
			}
		}
	}

	protected class Context implements BinlogParserContext, BinlogEventFilter {

		private Map<Long, TableMapEvent> tableMapEvents = new HashMap<Long, TableMapEvent>();

		/**
		 * 
		 */
		public Context() {
		}

		@Override
		public final BinlogEventFilter getEventListener() {
			return this;
		}

		@Override
		public final TableMapEvent getTableMapEvent(long tableId) {
			return this.tableMapEvents.get(tableId);
		}

		/**
		 * 
		 */
		@Override
		public boolean accept(BinlogEventV4 event) throws Exception {
			//
			if (event == null) {
				return true;
			}
			//
			if (event instanceof TableMapEvent) {
				final TableMapEvent tme = (TableMapEvent) event;
				this.tableMapEvents.put(tme.getTableId(), tme);
			} else if (event instanceof RotateEvent) {
				// final RotateEvent re = (RotateEvent)event;
				if (isClearTableMapEventsOnRotate())
					this.tableMapEvents.clear();
			}
			return true;
		}

		public Map<Long, TableMapEvent> getTableMapEvents() {
			return tableMapEvents;
		}
	}
}
