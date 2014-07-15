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
package com.zju.ccnt.or.binlog;

/**
 * v4 format description event (size ≥ 91 bytes; the size is 76 + the number of
 * event types): +=====================================+ | event | timestamp 0 :
 * 4 | | header +----------------------------+ | | type_code 4 : 1 | =
 * FORMAT_DESCRIPTION_EVENT = 15 | +----------------------------+ | | server_id
 * 5 : 4 | | +----------------------------+ | | event_length 9 : 4 | >= 91 |
 * +----------------------------+ | | next_position 13 : 4 | |
 * +----------------------------+ | | flags 17 : 2 |
 * +=====================================+ | event | binlog_version 19 : 2 | = 4
 * | data +----------------------------+ | | server_version 21 : 50 | |
 * +----------------------------+ | | create_timestamp 71 : 4 | |
 * +----------------------------+ | | header_length 75 : 1 | |
 * +----------------------------+ | | post-header 76 : n | = array of n bytes,
 * one byte per event | | lengths for all | type that the server knows about | |
 * event types | +=====================================+ In all binary log
 * versions, the event data for the descriptor event begins with a common set of
 * fields
 * 
 * Binlog event V4 since mysql 5.0 and up
 * 
 * @author yaoxianglong
 * @version 1.0 2014-07-09
 */
public interface BinlogEventV4 {

	BinlogEventV4Header getHeader();
}
