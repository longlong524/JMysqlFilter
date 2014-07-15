package com.zju.ccnt.or.net.com.handler.impl;

import com.zju.ccnt.or.net.com.handler.AbstractPacketHandler;
import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;
import com.zju.ccnt.or.net.impl.packet.EOFPacket;
import com.zju.ccnt.or.net.impl.packet.ErrorPacket;

public class ChangeUserPacketHandler extends AbstractPacketHandler {

	@Override
	public void handlePacket(AbstractPacket p, TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		AbstractPacket response = mysqlTransport.readPacket();
		clientTransport.writePacket(response);
		if (response.getPacketMarker() == ErrorPacket.PACKET_MARKER) {
			return;
		}
		AbstractPacket tmp = null;
		do {
			tmp = clientTransport.readPacket();
			mysqlTransport.writePacket(tmp);
			tmp = mysqlTransport.readPacket();
			clientTransport.writePacket(tmp);
		} while (tmp.getPacketMarker() != ErrorPacket.PACKET_MARKER
				&& (tmp.getPacketMarker() != EOFPacket.PACKET_MARKER || tmp
						.getLength() >= 9));
	}

}
