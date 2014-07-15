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
package com.zju.ccnt.or;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.exception.NestableRuntimeException;

import com.zju.ccnt.or.binlog.BinlogEventFilter;
import com.zju.ccnt.or.binlog.BinlogParser;
import com.zju.ccnt.or.binlog.impl.ReplicationBasedBinlogParser;
import com.zju.ccnt.or.binlog.impl.event.AbstractBinlogEventV4;
import com.zju.ccnt.or.binlog.impl.parser.DeleteRowsEventParser;
import com.zju.ccnt.or.binlog.impl.parser.DeleteRowsEventV2Parser;
import com.zju.ccnt.or.binlog.impl.parser.FormatDescriptionEventParser;
import com.zju.ccnt.or.binlog.impl.parser.IncidentEventParser;
import com.zju.ccnt.or.binlog.impl.parser.IntvarEventParser;
import com.zju.ccnt.or.binlog.impl.parser.QueryEventParser;
import com.zju.ccnt.or.binlog.impl.parser.RandEventParser;
import com.zju.ccnt.or.binlog.impl.parser.RotateEventParser;
import com.zju.ccnt.or.binlog.impl.parser.StopEventParser;
import com.zju.ccnt.or.binlog.impl.parser.TableMapEventParser;
import com.zju.ccnt.or.binlog.impl.parser.UpdateRowsEventParser;
import com.zju.ccnt.or.binlog.impl.parser.UpdateRowsEventV2Parser;
import com.zju.ccnt.or.binlog.impl.parser.UserVarEventParser;
import com.zju.ccnt.or.binlog.impl.parser.WriteRowsEventParser;
import com.zju.ccnt.or.binlog.impl.parser.WriteRowsEventV2Parser;
import com.zju.ccnt.or.binlog.impl.parser.XidEventParser;
import com.zju.ccnt.or.common.util.MySQLConstants;
import com.zju.ccnt.or.net.TransportException;
import com.zju.ccnt.or.net.com.handler.IPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.BinlogDumpGTIDPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.BinlogDumpPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ChangeUserPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ConnectOutPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ConnectPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.CreateDBPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.DaemonPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.DebugPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.DelayedInsertPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.DropDBPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.FieldListPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.InitDBPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.PingPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ProcessInfoPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ProcessKillPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.QueryPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.QuitPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.RefreshPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.RegisterSlavePacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ResetConnectionPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.SetOptionPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.ShutDownPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.SleepPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StatisticsPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StmtClosePacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StmtExecutePacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StmtFetchPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StmtPreparePacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StmtResetPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.StmtSendLongDataPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.TableDumpPacketHandler;
import com.zju.ccnt.or.net.com.handler.impl.TimePacketHandler;
import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;
import com.zju.ccnt.or.net.impl.packet.EOFPacket;
import com.zju.ccnt.or.net.impl.packet.ErrorPacket;
import com.zju.ccnt.or.net.impl.packet.OKPacket;

/**
 * Get the mysql binary log event
 * 
 * @author yaoxianglong
 * @version 1.0
 */
public class OpenReplicator implements Runnable {
	/**
	 * this port for client to connected to this filter
	 */
	private int clientport = 4402;
	/**
	 * the info for connect to mysql
	 */
	private String host;
	private int port = 3306;

	/**
	 * the custom buffer size,bytes
	 */
	protected int readBufferSize = 8 * 1024 * 1024;
	private int writeBufferSize = 8 * 1024 * 1024;
	/**
	 * the binlog event's parser
	 */
	protected BinlogParser binlogParser;
	/**
	 * custom event listener
	 */
	protected BinlogEventFilter binlogEventListener;
	/**
	 * Whether is running
	 */
	protected final AtomicBoolean running = new AtomicBoolean(false);
	/**
	 * how to handle every different packet from client
	 */
	private IPacketHandler[] packetHandlers = new IPacketHandler[MySQLConstants.COM_SIZE];
	/**
	 * the time interval to write socket
	 */
	private long writeSocketInterval = 20;
	/**
	 * all the socket connection
	 */
	private TransportImpl clientTransport = null;
	private TransportImpl mysqlTransport = null;
	/**
	 * the write limit
	 */
	private int writeSize = 2000;
	/**
	 * the server socket
	 */
	private ServerSocket ss = null;

	/**
	 * 
	 * @param mysqlhost
	 *            :mysql's port
	 */
	public OpenReplicator(String mysqlhost) {
		this.host = mysqlhost;
	}

	public int getWriteSize() {
		return writeSize;
	}

	public void setWriteSize(int writeSize) {
		this.writeSize = writeSize;
	}

	/**
	 * mysql's host and port
	 * 
	 * @param mysqlhost
	 * @param mysqlport
	 */
	public OpenReplicator(String mysqlhost, int mysqlport) {
		this.host = mysqlhost;
		this.port = mysqlport;
	}

	public int getWriteBufferSize() {
		return writeBufferSize;
	}

	public void setWriteBufferSize(int writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}

	public long getWriteSocketInterval() {
		return writeSocketInterval;
	}

	public void setWriteSocketInterval(long writeSocketInterval) {
		this.writeSocketInterval = writeSocketInterval;
	}

	private void initPacketHandlers() {
		packetHandlers[MySQLConstants.COM_SLEEP] = new SleepPacketHandler();
		packetHandlers[MySQLConstants.COM_QUIT] = new QuitPacketHandler();
		packetHandlers[MySQLConstants.COM_INIT_DB] = new InitDBPacketHandler();
		packetHandlers[MySQLConstants.COM_QUERY] = new QueryPacketHandler();
		packetHandlers[MySQLConstants.COM_FIELD_LIST] = new FieldListPacketHandler();
		packetHandlers[MySQLConstants.COM_CREATE_DB] = new CreateDBPacketHandler();
		packetHandlers[MySQLConstants.COM_DROP_DB] = new DropDBPacketHandler();
		packetHandlers[MySQLConstants.COM_REFRESH] = new RefreshPacketHandler();
		packetHandlers[MySQLConstants.COM_SHUTDOWN] = new ShutDownPacketHandler();
		packetHandlers[MySQLConstants.COM_STATISTICS] = new StatisticsPacketHandler();
		packetHandlers[MySQLConstants.COM_PROCESS_INFO] = new ProcessInfoPacketHandler();
		packetHandlers[MySQLConstants.COM_CONNECT] = new ConnectPacketHandler();
		packetHandlers[MySQLConstants.COM_PROCESS_KILL] = new ProcessKillPacketHandler();
		packetHandlers[MySQLConstants.COM_DEBUG] = new DebugPacketHandler();
		packetHandlers[MySQLConstants.COM_PING] = new PingPacketHandler();
		packetHandlers[MySQLConstants.COM_TIME] = new TimePacketHandler();
		packetHandlers[MySQLConstants.COM_DELAYED_INSERT] = new DelayedInsertPacketHandler();
		packetHandlers[MySQLConstants.COM_CHANGE_USER] = new ChangeUserPacketHandler();
		packetHandlers[MySQLConstants.COM_BINLOG_DUMP] = new BinlogDumpPacketHandler();
		packetHandlers[MySQLConstants.COM_TABLE_DUMP] = new TableDumpPacketHandler();
		packetHandlers[MySQLConstants.COM_CONNECT_OUT] = new ConnectOutPacketHandler();
		packetHandlers[MySQLConstants.COM_REGISTER_SLAVE] = new RegisterSlavePacketHandler();
		packetHandlers[MySQLConstants.COM_STMT_PREPARE] = new StmtPreparePacketHandler();
		packetHandlers[MySQLConstants.COM_STMT_EXECUTE] = new StmtExecutePacketHandler();
		packetHandlers[MySQLConstants.COM_STMT_SEND_LONG_DATA] = new StmtSendLongDataPacketHandler();
		packetHandlers[MySQLConstants.COM_STMT_CLOSE] = new StmtClosePacketHandler();
		packetHandlers[MySQLConstants.COM_STMT_RESET] = new StmtResetPacketHandler();
		packetHandlers[MySQLConstants.COM_SET_OPTION] = new SetOptionPacketHandler();
		packetHandlers[MySQLConstants.COM_STMT_FETCH] = new StmtFetchPacketHandler();
		packetHandlers[MySQLConstants.COM_DAEMON] = new DaemonPacketHandler();
		packetHandlers[MySQLConstants.COM_BINLOG_DUMP_GTID] = new BinlogDumpGTIDPacketHandler();
		packetHandlers[MySQLConstants.COM_RESET_CONNECTION] = new ResetConnectionPacketHandler();
	}

	/**
	 * start the openreplicator
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		new Thread(this).start();
	}

	public void stop() {
		this.running.compareAndSet(true, false);
		if (this.mysqlTransport != null) {
			try {
				this.mysqlTransport.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (this.clientTransport != null) {
			try {
				this.clientTransport.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (this.ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * protected void dumpBinlog() throws Exception { // final
	 * ComBinlogDumpPacket command = new ComBinlogDumpPacket();
	 * command.setBinlogFlag(0); command.setServerId(this.serverId);
	 * command.setBinlogPosition(this.binlogPosition);
	 * command.setBinlogFileName(
	 * StringColumn.valueOf(this.binlogFileName.getBytes(this.encoding)));
	 * this.transport.getOutputStream().writePacket(command);
	 * this.transport.getOutputStream().flush();
	 * 
	 * // final Packet packet = this.transport.getInputStream().readPacket();
	 * if(packet.getPacketBody()[0] == ErrorPacket.PACKET_MARKER) { final
	 * ErrorPacket error = ErrorPacket.valueOf(packet); throw new
	 * TransportException(error); } }
	 */

	protected ReplicationBasedBinlogParser getDefaultBinlogParser(
			TransportImpl mysqlTransport) throws Exception {
		//
		final ReplicationBasedBinlogParser r = new ReplicationBasedBinlogParser();
		r.registgerEventParser(new StopEventParser());
		r.registgerEventParser(new RotateEventParser());
		r.registgerEventParser(new IntvarEventParser());
		r.registgerEventParser(new XidEventParser());
		r.registgerEventParser(new RandEventParser());
		r.registgerEventParser(new QueryEventParser());
		r.registgerEventParser(new UserVarEventParser());
		r.registgerEventParser(new IncidentEventParser());
		r.registgerEventParser(new TableMapEventParser());
		r.registgerEventParser(new WriteRowsEventParser());
		r.registgerEventParser(new UpdateRowsEventParser());
		r.registgerEventParser(new DeleteRowsEventParser());
		r.registgerEventParser(new WriteRowsEventV2Parser());
		r.registgerEventParser(new UpdateRowsEventV2Parser());
		r.registgerEventParser(new DeleteRowsEventV2Parser());
		r.registgerEventParser(new FormatDescriptionEventParser());

		return r;
	}

	@Override
	public void run() {
		initPacketHandlers();
		try {
			ss = new ServerSocket(clientport);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		this.running.set(true);
		while (this.running.get()) {
			long writeTime = 0;
			long n1, n2;
			try {
				clientTransport = new TransportImpl(ss.accept(),
						readBufferSize, writeBufferSize, writeSize,
						writeSocketInterval);
				mysqlTransport = new TransportImpl(new Socket(host, port),
						readBufferSize, 32 * 1024, 100, writeSocketInterval);
				// connect
				connect(clientTransport, mysqlTransport);
				// login
				login(clientTransport, mysqlTransport);
				// dump
				utilDumpBin(clientTransport, mysqlTransport);

				this.binlogParser = getDefaultBinlogParser(mysqlTransport);

				int count = 0;
				while (this.running.get()) {
					List<AbstractPacket> list = getEventPackets(mysqlTransport,
							clientTransport);
					count += list.size();
					AbstractBinlogEventV4 event = null;
					event = this.binlogParser.doParse(list);
					if (event != null && this.binlogEventListener != null) {
						boolean re = this.binlogEventListener.accept(event);
						if (!re) {
							for (AbstractPacket p : list) {
								// System.out.println(p.getSequence());
								byte[] newb = new byte[5];
								newb[0] = 1;
								newb[3] = (byte) p.getSequence();
								newb[4] = 0;
								p.setLength(1);
								p.setBytes(newb);
							}
						}
					}
					n1 = System.currentTimeMillis();
					for (AbstractPacket p : list) {
						clientTransport.writePacket(p);
					}
					n2 = System.currentTimeMillis();
					writeTime += (n2 - n1);

					if (count % 50000 == 0) {
						// System.err.println("body time:"+TransportOutputStreamImpl.bodytime);
						System.err.println("write time:" + writeTime);
						// System.err.println("wait time:"+ByteLinkedBlockingDeque.waitTime);
						// System.err.println("write time:"+ByteLinkedBlockingDeque.writeTime);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (clientTransport != null) {
					try {
						clientTransport.disconnect();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (mysqlTransport != null) {
					try {
						mysqlTransport.disconnect();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private List<AbstractPacket> getEventPackets(TransportImpl mysqlTransport,
			TransportImpl clientTransport) throws Exception {
		List<AbstractPacket> list = new LinkedList<AbstractPacket>();
		try {
			while (true) {
				AbstractPacket p = mysqlTransport.readPacket();
				list.add(p);
				int packetMarker = p.getPacketMarker();
				// Parse packet
				if (packetMarker != OKPacket.PACKET_MARKER) { // 0x00
					if ((byte) packetMarker == ErrorPacket.PACKET_MARKER) {
						throw new NestableRuntimeException("ErrorPacket:"
								+ p.toString());
					} else if ((byte) packetMarker == EOFPacket.PACKET_MARKER) {
						throw new NestableRuntimeException("EOFPacket:"
								+ p.toString());
					} else {
						throw new NestableRuntimeException(
								"assertion failed, invalid packet marker: "
										+ packetMarker);
					}
				}
				if (p.getLength() != 0x00ffffff) {
					break;
				}
			}
		} catch (Exception e) {
			for (AbstractPacket p : list) {
				clientTransport.writePacket(p);
			}
			throw e;
		}
		return list;
	}

	private void connect(TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		AbstractPacket packet = mysqlTransport.readPacket();
		clientTransport.writePacket(packet);
		if (packet.getPacketMarker() == ErrorPacket.PACKET_MARKER) {
			final ErrorPacket error = ErrorPacket.valueOf(packet);
			throw new TransportException(error);
		}
		System.err.println("connect to successfully!");
	}

	private void login(TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		// login
		final AbstractPacket request = clientTransport.readPacket();

		mysqlTransport.writePacket(request);

		final AbstractPacket response = mysqlTransport.readPacket();

		clientTransport.writePacket(response);

		if (response.getPacketMarker() == ErrorPacket.PACKET_MARKER) {
			final ErrorPacket error = ErrorPacket.valueOf(response);
			throw new TransportException(error);
		} else if (response.getPacketMarker() == OKPacket.PACKET_MARKER) {
			System.err.println("login successfully!");
		} else {
			throw new NestableRuntimeException(
					"assertion failed, invalid packet: " + response);
		}
	}

	private void utilDumpBin(TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		while (true) {
			AbstractPacket request = clientTransport.readPacket();
			mysqlTransport.writePacket(request);

			packetHandlers[request.getPacketMarker()].handlePacket(request,
					clientTransport, mysqlTransport);

			if (request.getPacketMarker() == MySQLConstants.COM_BINLOG_DUMP) {
				break;
			}
		}
		System.err.println("dump to mysql successfully!");
	}

	public boolean isRunning() {
		return this.running.get();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public BinlogParser getBinlogParser() {
		return binlogParser;
	}

	public void setBinlogParser(BinlogParser parser) {
		this.binlogParser = parser;
	}

	public BinlogEventFilter getBinlogEventListener() {
		return binlogEventListener;
	}

	public void setBinlogEventListener(BinlogEventFilter listener) {
		this.binlogEventListener = listener;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	public int getClientport() {
		return clientport;
	}

	public void setClientport(int clientport) {
		this.clientport = clientport;
	}

}
