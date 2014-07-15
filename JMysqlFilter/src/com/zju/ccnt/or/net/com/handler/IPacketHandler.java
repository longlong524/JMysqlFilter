package com.zju.ccnt.or.net.com.handler;

import java.io.IOException;

import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;

public interface IPacketHandler {
	public void handlePacket(AbstractPacket p, TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws IOException, Exception;
}
