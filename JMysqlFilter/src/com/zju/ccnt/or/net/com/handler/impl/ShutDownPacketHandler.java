package com.zju.ccnt.or.net.com.handler.impl;

import com.zju.ccnt.or.net.com.handler.AbstractPacketHandler;
import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;

public class ShutDownPacketHandler extends AbstractPacketHandler {

	@Override
	public void handlePacket(AbstractPacket p, TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		super.handlePacket(p, clientTransport, mysqlTransport);
	}

}
