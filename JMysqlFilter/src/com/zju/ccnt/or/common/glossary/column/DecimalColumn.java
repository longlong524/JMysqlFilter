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

import java.math.BigDecimal;

import com.zju.ccnt.or.common.glossary.Column;

/**
 * 
 * @author Xianglong Yao
 */
public final class DecimalColumn implements Column {
	//
	private static final long serialVersionUID = -3798378473095594835L;
	
	//
	private final BigDecimal value;
	private final int precision;
	private final int scale;
	
	/**
	 * 
	 */
	private DecimalColumn(BigDecimal value, int precision, int scale) {
		this.value = value;
		this.scale = scale;
		this.precision = precision;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	/**
	 * 
	 */
	@Override
	public BigDecimal getValue() {
		return this.value;
	}
	
	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}
	
	/**
	 * 
	 */
	public static final DecimalColumn valueOf(BigDecimal value, int precision, int scale) {
		if(precision < scale) throw new IllegalArgumentException("invalid precision: " + precision + ", scale: " + scale);
		return new DecimalColumn(value, precision, scale);
	}
}
