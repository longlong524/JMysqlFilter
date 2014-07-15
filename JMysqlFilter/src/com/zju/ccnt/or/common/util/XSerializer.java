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
package com.zju.ccnt.or.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang.exception.NestableRuntimeException;

import com.zju.ccnt.or.common.glossary.UnsignedLong;
import com.zju.ccnt.or.common.glossary.column.StringColumn;
import com.zju.ccnt.or.net.XOutputStream;
import com.zju.ccnt.or.net.impl.XOutputStreamImpl;

/**
 * 
 * @author Xianglong Yao
 */
public final class XSerializer implements XOutputStream {
	//
	private final XOutputStream tos;
	private final ByteArrayOutputStream bos;

	/**
	 * 
	 */
	public XSerializer() {
		this.bos = new ByteArrayOutputStream();
		this.tos = new XOutputStreamImpl(this.bos);
	}

	public XSerializer(int size) {
		this.bos = new ByteArrayOutputStream(size);
		this.tos = new XOutputStreamImpl(this.bos);
	}

	/**
	 * 
	 */
	public byte[] toByteArray() {
		flush();
		return this.bos.toByteArray();
	}

	/**
	 * 
	 */
	@Override
	public void flush() {
		try {
			this.tos.flush();
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			this.tos.close();
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeBytes(byte[] value) {
		try {
			this.tos.writeBytes(value);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeBytes(int value, int length) {
		try {
			this.tos.writeBytes(value, length);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeBytes(byte[] value, int offset, int length) {
		try {
			this.tos.writeBytes(value, offset, length);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeInt(int value, int length) {
		try {
			this.tos.writeInt(value, length);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeLong(long value, int length) {
		try {
			this.tos.writeLong(value, length);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeUnsignedLong(UnsignedLong value) {
		try {
			this.tos.writeUnsignedLong(value);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeLengthCodedString(StringColumn value) {
		try {
			this.tos.writeLengthCodedString(value);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeFixedLengthString(StringColumn value) {
		try {
			this.tos.writeFixedLengthString(value);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}

	@Override
	public void writeNullTerminatedString(StringColumn value) {
		try {
			this.tos.writeNullTerminatedString(value);
		} catch (IOException e) {
			throw new NestableRuntimeException(e);
		}
	}
}
