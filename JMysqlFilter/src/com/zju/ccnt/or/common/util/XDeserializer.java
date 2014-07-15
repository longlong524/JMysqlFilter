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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.exception.NestableRuntimeException;

import com.zju.ccnt.or.common.glossary.UnsignedLong;
import com.zju.ccnt.or.common.glossary.column.BitColumn;
import com.zju.ccnt.or.common.glossary.column.StringColumn;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class XDeserializer implements XInputStream {
	//

	//
	private final InputStream is;

	/**
	 * 
	 */
	public XDeserializer(byte[] data) {
		this.is = new ByteArrayInputStream(data);
	}

	public XDeserializer(byte[] data, int offset, int size) {
		this.is = new ByteArrayInputStream(data, offset, size);
	}

	/**
	 * 
	 */
	@Override
	public int readInt(final int length) throws IOException {
		int r = 0;
		for (int i = 0; i < length; ++i) {
			final int v = this.read();
			r |= (v << (i << 3));
		}
		return r;
	}

	@Override
	public long readLong(final int length) throws IOException {
		long r = 0;
		for (int i = 0; i < length; ++i) {
			final long v = this.read();
			r |= (v << (i << 3));
		}
		return r;
	}

	@Override
	public byte[] readBytes(final int length) throws IOException {
		byte[] r = new byte[length];
		this.read(r, 0, length);
		return r;
	}

	@Override
	public UnsignedLong readUnsignedLong() throws IOException {
		final int v = this.read();
		if (v < 251)
			return UnsignedLong.valueOf(v);
		else if (v == 251)
			return null;
		else if (v == 252)
			return UnsignedLong.valueOf(readInt(2));
		else if (v == 253)
			return UnsignedLong.valueOf(readInt(3));
		else if (v == 254)
			return UnsignedLong.valueOf(readLong(8));
		else
			throw new NestableRuntimeException(
					"assertion failed, should NOT reach here");
	}

	@Override
	public StringColumn readLengthCodedString() throws IOException {
		final UnsignedLong length = readUnsignedLong();
		return length == null ? null : readFixedLengthString(length.intValue());
	}

	@Override
	public StringColumn readNullTerminatedString() throws IOException {
		final XSerializer s = new XSerializer(128); // 128 should be OK for most
													// schema names
		while (true) {
			final int v = this.read();
			if (v == 0)
				break;
			s.writeInt(v, 1);
		}
		return StringColumn.valueOf(s.toByteArray());
	}

	@Override
	public StringColumn readFixedLengthString(final int length)
			throws IOException {
		return StringColumn.valueOf(readBytes(length));
	}

	@Override
	public BitColumn readBit(final int length, boolean isBigEndian)
			throws IOException {
		final byte[] value = readBytes((length + 7) >> 3);
		return isBigEndian ? BitColumn.valueOf(length, value) : BitColumn
				.valueOf(length, CodecUtils.toBigEndian(value));
	}

	/**
	 * 
	 */
	@Override
	public void close() throws IOException {

	}

	@Override
	public void setReadLimit(final int limit) throws IOException {

		/*
		 * this.readCount = 0; this.readLimit = limit;
		 */
	}

	@Override
	public int available() throws IOException {
		return is.available();
		/*
		 * if(this.readLimit > 0) { return this.readLimit - this.readCount; }
		 * else { return this.tail - this.head ; }
		 */
	}

	@Override
	public boolean hasMore() throws IOException {
		return this.available() > 0;
	}

	@Override
	public long skip(final long n) throws IOException {
		return is.skip(n);
	}

	public int read() throws IOException {
		return is.read();
	}

	public int read(final byte b[], final int off, final int len)
			throws IOException {
		return doRead(b, off, len);

	}

	private int doRead(final byte[] b, final int off, final int len)
			throws IOException {
		int total = len;
		int index = off;
		if (total > 0) {
			final int available = this.available() - index;
			if (available >= total) {
				return is.read(b, index, total);
			} else {
				return is.read(b, index, available);
			}
		}
		return len;
	}
}
