package com.zju.ccnt.or.net.com.handler;

import com.zju.ccnt.or.net.impl.TransportImpl;
import com.zju.ccnt.or.net.impl.packet.AbstractPacket;
/**
 * 
 * @author Xianglong yao
 */
public abstract class AbstractPacketHandler implements IPacketHandler {

	@Override
	public void handlePacket(AbstractPacket p,TransportImpl clientTransport,
			TransportImpl mysqlTransport) throws Exception {
		//a packet containing a Protocol::LengthEncodedInteger column_count
		AbstractPacket response=mysqlTransport.readPacket();
		clientTransport.writePacket(response);
		clientTransport.flush();
	}

}
