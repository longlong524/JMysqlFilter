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
package com.zju.ccnt.or.binlog.impl.variable.user;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.zju.ccnt.or.common.util.MySQLConstants;

/**
 * 
 * @author Xianglong Yao
 */
public class UserVariableDecimal extends AbstractUserVariable {
	//
	public static final int TYPE = MySQLConstants.DECIMAL_RESULT;
	
	//
	private final byte[] value;

	/**
	 * 
	 */
	public UserVariableDecimal(byte[] value) { 
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
	@Override
	public byte[] getValue() {
		return this.value;
	}
}