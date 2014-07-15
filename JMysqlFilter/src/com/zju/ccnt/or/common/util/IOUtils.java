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

import java.net.Socket;

import com.zju.ccnt.or.net.XInputStream;
import com.zju.ccnt.or.net.XOutputStream;

/**
 * 
 * @author Xianglong Yao
 */
public final class IOUtils {
	
	/**
	 * 
	 */
	public static void closeQuietly(Socket socket) {
		try {
			socket.close();
		} catch(Exception e) {
			// NOP
		}
	}
	
	public static void closeQuietly(XInputStream is) {
		try {
			is.close();
		} catch(Exception e) {
			// NOP
		}
	}
	
	public static void closeQuietly(XOutputStream os) {
		try {
			os.close();
		} catch(Exception e) {
			// NOP
		}
	}
}
