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
package com.zju.ccnt.or.net.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.exception.NestableRuntimeException;

import com.zju.ccnt.or.common.glossary.UnsignedLong;
import com.zju.ccnt.or.common.glossary.column.BitColumn;
import com.zju.ccnt.or.common.glossary.column.StringColumn;
import com.zju.ccnt.or.common.util.CodecUtils;
import com.zju.ccnt.or.common.util.XSerializer;
import com.zju.ccnt.or.net.ExceedLimitException;
import com.zju.ccnt.or.net.XInputStream;

/**
 * 
 * @author Xianglong Yao
 */
public class XInputStreamImpl extends InputStream implements XInputStream {
	//
	private int head = 0;
	private int tail = 0;
	private int readCount = 0;
	private int readLimit = 0;
	private byte[] buffer;
	private final InputStream is;

	/**
	 * 
	 */
	public XInputStreamImpl(InputStream is) {
		this(is, 128 * 1024);
	}

	public XInputStreamImpl(InputStream is, int size) {
		this.is = is;
		if (size < 0) {
			return;
		}
		this.buffer = new byte[size];
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
		this.is.close();
	}

	@Override
	public void setReadLimit(final int limit) throws IOException {
		this.readCount = 0;
		this.readLimit = limit;
	}

	@Override
	public int available() throws IOException {
		if (this.readLimit > 0) {
			return this.readLimit - this.readCount;
		} else {
			return this.tail - this.head + this.is.available();
		}
	}

	@Override
	public boolean hasMore() throws IOException {
		if (this.head < this.tail)
			return true;
		return this.available() > 0;
	}

	@Override
	public long skip(final long n) throws IOException {
		if (this.readLimit > 0 && (this.readCount + n) > this.readLimit) {
			this.readCount += doSkip(this.readLimit - this.readCount);
			throw new ExceedLimitException();
		} else {
			this.readCount += doSkip(n);
			return n; // always skip the number of bytes specified by parameter
						// "n"
		}
	}

	@Override
	public int read() throws IOException {
		if (this.readLimit > 0 && (this.readCount + 1) > this.readLimit) {
			throw new ExceedLimitException();
		} else {
			if (this.head >= this.tail)
				doFill();
			final int r = this.buffer[this.head++] & 0xFF;
			++this.readCount;
			return r;
		}
	}

	@Override
	public int read(final byte b[], final int off, final int len)
			throws IOException {
		if (this.readLimit > 0 && (this.readCount + len) > this.readLimit) {
			this.readCount += doRead(b, off, this.readLimit - this.readCount);
			throw new ExceedLimitException();
		} else {
			this.readCount += doRead(b, off, len);
			return len; // always read the number of bytes specified by
						// parameter "len"
		}
	}

	/**
	 * 
	 */
	private void doFill() throws IOException {
		this.head = 0;
		this.tail = this.is.read(this.buffer, 0, this.buffer.length);
		if (this.tail < 0)
			throw new EOFException();
	}

	private long doSkip(final long n) throws IOException {
		long total = n;
		while (total > 0) {
			final int availabale = this.tail - this.head;
			if (availabale >= total) {
				this.head += total;
				break;
			} else {
				total -= availabale;
				doFill();
			}
		}
		return n;
	}

	private int doRead(final byte[] b, final int off, final int len)
			throws IOException {
		int total = len;
		int index = off;
		while (total > 0) {
			final int available = this.tail - this.head;
			if (available >= total) {
				System.arraycopy(this.buffer, this.head, b, index, total);
				this.head += total;
				break;
			} else {
				System.arraycopy(this.buffer, this.head, b, index, available);
				index += available;
				total -= available;
				doFill();
			}
		}
		return len;
	}
}
