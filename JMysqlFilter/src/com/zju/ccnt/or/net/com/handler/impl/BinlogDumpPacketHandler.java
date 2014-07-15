package com.zju.ccnt.or.net.com.handler.impl;

import java.io.IOException;

import com.zju.ccnt.or.net.com.handler.AbstractPacketHandler;
import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;

public class BinlogDumpPacketHandler extends AbstractPacketHandler {

	@Override
	public void handlePacket(AbstractPacket p,TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws IOException {
		return;
	}

}
