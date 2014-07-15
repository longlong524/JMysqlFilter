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
package com.zju.ccnt.or.binlog.impl.variable.status;

import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.zju.ccnt.or.common.util.MySQLConstants;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class QMasterDataWrittenCode extends AbstractStatusVariable {
	//
	public static final int TYPE = MySQLConstants.Q_MASTER_DATA_WRITTEN_CODE;

	//
	private final int value;

	/**
	 * 
	 */
	public QMasterDataWrittenCode(int value) {
		super(TYPE);
		this.value = value;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("value", value).toString();
	}

	/**
	 * 
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 
	 */
	public static QMasterDataWrittenCode valueOf(XInputStream tis)
			throws IOException {
		return new QMasterDataWrittenCode(tis.readInt(4));
	}
}
