package com.zju.ccnt.or.net.com.handler.impl;

import com.zju.ccnt.or.common.util.MySQLUtils;
import com.zju.ccnt.or.net.com.handler.AbstractPacketHandler;
import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;
import com.zju.ccnt.or.net.impl.packet.EOFPacket;
import com.zju.ccnt.or.net.impl.packet.ErrorPacket;
import com.zju.ccnt.or.net.impl.packet.OKPacket;

public class StmtExecutePacketHandler extends AbstractPacketHandler {

	@Override
	public void handlePacket(AbstractPacket p, TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		AbstractPacket response = mysqlTransport.readPacket();
		clientTransport.writePacket(response);
		clientTransport.flush();
		if (response.getPacketMarker() == ErrorPacket.PACKET_MARKER
				|| response.getPacketMarker() == OKPacket.PACKET_MARKER) {
			return;
		}

		// ResultSet
		if (response.getLength() == 0) {
			return;
		}
		byte[] all = new byte[response.getLength()];
		for (int i = 0; i < response.getLength(); i++) {
			all[i] = response.getBytes()[i + 4];
		}
		int count = MySQLUtils.getLengthEncodedInteger(all);
		if (count == 0) {
			return;
		}
		// column_count * Protocol::ColumnDefinition packets
		for (int i = 0; i < count; i++) {
			clientTransport.writePacket(mysqlTransport.readPacket());
		}
		// EOF_Packet
		clientTransport.writePacket(mysqlTransport.readPacket());

		do {
			response = mysqlTransport.readPacket();
			clientTransport.writePacket(response);
		} while ((response.getPacketMarker() != EOFPacket.PACKET_MARKER || response
				.getLength() >= 9)
				&& response.getPacketMarker() != ErrorPacket.PACKET_MARKER);
		clientTransport.flush();
	}

}
