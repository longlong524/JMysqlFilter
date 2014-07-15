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
package com.zju.ccnt.or.common.glossary.column;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.zju.ccnt.or.common.glossary.Column;

/**
 * 
 * @author xianglong yao
 */
public class CommonColumn implements Column {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5949238473370590805L;
	//
	private final byte[] value;
	
	private final int ordinal;
	
	private final int type;

	public int getType() {
		return type;
	}

	/**
	 * 
	 */
	private CommonColumn(byte[] value,int ordinal,int type) {
		this.value = value;
		this.ordinal=ordinal;
		this.type=type;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
		.append("value", value).append("ordinal",ordinal).append("type",type).toString();
	}
	
	/**
	 * 
	 */
	@Override
	public byte[] getValue() {
		return value;
	}
	
	/**
	 * 
	 */
	public static final CommonColumn valueOf(byte[] value,int ordinal,int type) {
		return new CommonColumn(value,ordinal,type);
	}

	public int getOrdinal() {
		return ordinal;
	}
}
