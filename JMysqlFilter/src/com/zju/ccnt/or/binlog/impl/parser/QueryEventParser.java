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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zju.ccnt.or.binlog.BinlogEventV4Header;
import com.zju.ccnt.or.binlog.BinlogParserContext;
import com.zju.ccnt.or.binlog.StatusVariable;
import com.zju.ccnt.or.binlog.impl.event.AbstractBinlogEventV4;
import com.zju.ccnt.or.binlog.impl.event.QueryEvent;
import com.zju.ccnt.or.binlog.impl.variable.status.QAutoIncrement;
import com.zju.ccnt.or.binlog.impl.variable.status.QCatalogCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QCatalogNzCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QCharsetCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QCharsetDatabaseCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QFlags2Code;
import com.zju.ccnt.or.binlog.impl.variable.status.QInvoker;
import com.zju.ccnt.or.binlog.impl.variable.status.QLcTimeNamesCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QMasterDataWrittenCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QMicroseconds;
import com.zju.ccnt.or.binlog.impl.variable.status.QSQLModeCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QTableMapForUpdateCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QTimeZoneCode;
import com.zju.ccnt.or.binlog.impl.variable.status.QUpdatedDBNames;
import com.zju.ccnt.or.common.util.XDeserializer;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class QueryEventParser extends AbstractBinlogEventParser {
	//
	private static final Logger LOGGER = LoggerFactory
			.getLogger(QueryEventParser.class);

	/**
	 * 
	 */
	public QueryEventParser() {
		super(QueryEvent.EVENT_TYPE);
	}

	/**
	 * @throws Exception
	 * 
	 */
	@Override
	public AbstractBinlogEventV4 parse(XInputStream is,
			BinlogEventV4Header header, BinlogParserContext context)
			throws Exception {
		final QueryEvent event = new QueryEvent(header);
		event.setThreadId(is.readLong(4));
		event.setElapsedTime(is.readLong(4));
		event.setDatabaseNameLength(is.readInt(1));
		event.setErrorCode(is.readInt(2));
		event.setStatusVariablesLength(is.readInt(2));
		event.setStatusVariables(parseStatusVariables(is.readBytes(event
				.getStatusVariablesLength())));
		event.setDatabaseName(is.readNullTerminatedString());
		event.setSql(is.readFixedLengthString(is.available()));
		return event;
	}

	/**
	 * 
	 */
	protected List<StatusVariable> parseStatusVariables(byte[] data)
			throws IOException {
		final List<StatusVariable> r = new ArrayList<StatusVariable>();
		final XDeserializer d = new XDeserializer(data);
		boolean abort = false;
		while (!abort && d.available() > 0) {
			final int type = d.readInt(1);
			switch (type) {
			case QAutoIncrement.TYPE:
				r.add(QAutoIncrement.valueOf(d));
				break;
			case QCatalogCode.TYPE:
				r.add(QCatalogCode.valueOf(d));
				break;
			case QCatalogNzCode.TYPE:
				r.add(QCatalogNzCode.valueOf(d));
				break;
			case QCharsetCode.TYPE:
				r.add(QCharsetCode.valueOf(d));
				break;
			case QCharsetDatabaseCode.TYPE:
				r.add(QCharsetDatabaseCode.valueOf(d));
				break;
			case QFlags2Code.TYPE:
				r.add(QFlags2Code.valueOf(d));
				break;
			case QLcTimeNamesCode.TYPE:
				r.add(QLcTimeNamesCode.valueOf(d));
				break;
			case QSQLModeCode.TYPE:
				r.add(QSQLModeCode.valueOf(d));
				break;
			case QTableMapForUpdateCode.TYPE:
				r.add(QTableMapForUpdateCode.valueOf(d));
				break;
			case QTimeZoneCode.TYPE:
				r.add(QTimeZoneCode.valueOf(d));
				break;
			case QMasterDataWrittenCode.TYPE:
				r.add(QMasterDataWrittenCode.valueOf(d));
				break;
			case QInvoker.TYPE:
				r.add(QInvoker.valueOf(d));
				break;
			case QUpdatedDBNames.TYPE:
				r.add(QUpdatedDBNames.valueOf(d));
				break;
			case QMicroseconds.TYPE:
				r.add(QMicroseconds.valueOf(d));
				break;
			default:
				LOGGER.warn("unknown status variable type: " + type);
				abort = true;
				break;
			}
		}
		return r;
	}
}
