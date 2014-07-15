package com.zju.ccnt.or.net.com.handler.impl;

import com.zju.ccnt.or.net.com.handler.AbstractPacketHandler;
import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;
import com.zju.ccnt.or.net.impl.packet.ErrorPacket;

public class StmtPreparePacketHandler extends AbstractPacketHandler {

	@Override
	public void handlePacket(AbstractPacket p,TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		AbstractPacket response=mysqlTransport.readPacket();
		clientTransport.writePacket(response);
		if(response.getPacketMarker()==ErrorPacket.PACKET_MARKER){
			return;
		}
		int paramnum=0,colnum=0;
		colnum|=(response.getBytes()[5+4]&0xff);
		colnum|=(response.getBytes()[6+4]<<8&0x00ff00);
		paramnum|=(response.getBytes()[7+4]&0xff);
		paramnum|=(response.getBytes()[8+4]<<8&0x00ff00);
		if(paramnum>0){
			for(int i=0;i<paramnum;i++){
				response=mysqlTransport.readPacket();
				clientTransport.writePacket(response);
			}
			response=mysqlTransport.readPacket();
			clientTransport.writePacket(response);
		}
		if(colnum>0){
			for(int i=0;i<colnum;i++){
				response=mysqlTransport.readPacket();
				clientTransport.writePacket(response);
			}
			response=mysqlTransport.readPacket();
			clientTransport.writePacket(response);
		}
	}

}
