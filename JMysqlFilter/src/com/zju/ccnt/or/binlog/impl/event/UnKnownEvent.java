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
import com.zju.ccnt.or.common.util.MySQLConstants;

/**
 * 
 * @author Xianglong Yao
 */
public final class UnKnownEvent extends AbstractBinlogEventV4 {
	//
	public static final int EVENT_TYPE = MySQLConstants.UNKNOWN_EVENT;

	/**
	 * 
	 */
	public UnKnownEvent() {
	}

	public UnKnownEvent(BinlogEventV4Header header) {
		this.header = header;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("Unknown event").toString();
	}

}
