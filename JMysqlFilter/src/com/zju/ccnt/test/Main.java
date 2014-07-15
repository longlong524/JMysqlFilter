package com.zju.ccnt.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import com.zju.ccnt.or.OpenReplicator;
import com.zju.ccnt.or.binlog.BinlogEventFilter;
import com.zju.ccnt.or.binlog.BinlogEventV4;
import com.zju.ccnt.or.binlog.impl.event.DeleteRowsEvent;
import com.zju.ccnt.or.binlog.impl.event.DeleteRowsEventV2;
import com.zju.ccnt.or.binlog.impl.event.QueryEvent;
import com.zju.ccnt.or.binlog.impl.event.UpdateRowsEvent;
import com.zju.ccnt.or.binlog.impl.event.UpdateRowsEventV2;
import com.zju.ccnt.or.binlog.impl.event.WriteRowsEvent;
import com.zju.ccnt.or.binlog.impl.event.WriteRowsEventV2;
import com.zju.ccnt.or.binlog.impl.event.XidEvent;
import com.zju.ccnt.or.common.glossary.Column;
import com.zju.ccnt.or.common.glossary.Pair;
import com.zju.ccnt.or.common.glossary.Row;

/**
 * 
 */

/**
 * 
 * @author yaoxianglong
 * @version 1.0
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		final OpenReplicator or = new OpenReplicator("localhost", 3306);
		
		or.setWriteBufferSize(4 * 1024 * 1024);
		
		or.setClientport(4402);
		
		or.setWriteSocketInterval(20);
		
		or.setWriteSize(2000);
		
		or.setBinlogEventListener(new BinlogEventFilter() {
			
			@Override
			public boolean accept(BinlogEventV4 event) {
				// System.err.println(event.getHeader().getEventType().name());
				// your code goes here
				// insert
				if (event instanceof QueryEvent) {
					QueryEvent qe = (QueryEvent) event;
					if ("BEGIN".equalsIgnoreCase(qe.getSql().toString())) {
						return true;
					}
				}
				// insert
				if (event instanceof WriteRowsEvent
						|| event instanceof WriteRowsEventV2) {
					WriteRowsEvent w = (WriteRowsEvent) ((event instanceof WriteRowsEventV2) ? ((WriteRowsEventV2) event)
							: ((WriteRowsEvent) event));
					// get the rows,filter the event
					List<Row> list = w.getRows();
					for (Row r : list) {
						// get the column data
						for (Column c : r.getColumns()) {
							if (c.getValue() == null) {

							}
						}
					}
					return true;
				}
				// delete event
				if (event instanceof DeleteRowsEvent
						|| event instanceof DeleteRowsEventV2) {
					DeleteRowsEvent w = (DeleteRowsEvent) ((event instanceof DeleteRowsEventV2) ? ((DeleteRowsEventV2) event)
							: ((DeleteRowsEvent) event));
					List<Row> list = w.getRows();
					for (Row r : list) {
						// get the column data
						for (Column c : r.getColumns()) {
							if (c.getValue() == null) {

							}
						}
					}
					return true;
				}
				// update
				if (event instanceof UpdateRowsEvent
						|| event instanceof UpdateRowsEventV2) {
					UpdateRowsEvent w = (UpdateRowsEvent) ((event instanceof UpdateRowsEventV2) ? ((UpdateRowsEventV2) event)
							: ((UpdateRowsEvent) event));
					List<Pair<Row>> list = w.getRows();
					for (Pair<Row> r : list) {
						// get the old row's column data
						for (Column c : r.getBefore().getColumns()) {
							if (c.getValue() == null) {

							}
						}
						// get the new row's column data
						for (Column c : r.getAfter().getColumns()) {
							if (c.getValue() == null) {

							}
						}
					}

					// filter the data
					return true;
				}
				// commit event
				if (event instanceof XidEvent) {
					XidEvent x = (XidEvent) event;
					return true;
				}

				return true;

			}
		});
		or.start();
		or.stop();
		System.out.println("press 'q' to stop");
		final BufferedReader br = new BufferedReader(new InputStreamReader(
				System.in));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			if (line.equals("q")) {
				or.stop();
				break;
			}
		}

	}

}
