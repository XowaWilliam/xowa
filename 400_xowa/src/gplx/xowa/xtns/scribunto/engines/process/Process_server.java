/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012 gnosygnu@gmail.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package gplx.xowa.xtns.scribunto.engines.process; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*; import gplx.xowa.xtns.scribunto.*; import gplx.xowa.xtns.scribunto.engines.*;
import gplx.core.threads.*;
import gplx.texts.HexDecUtl;
import gplx.xowa.xtns.scribunto.*;
import gplx.core.threads.Thread_adp_;
import gplx.ios.*;
import java.io.*;
public class Process_server implements Scrib_server {
    private Process process;
    private OutputStream stream_write; private Process_server_gobbler_recv stream_read; private byte[] bry_header = new byte[16], bry_body = new byte[Io_mgr.Len_kb * 4], bry_error = new byte[Io_mgr.Len_kb * 4];
    private Process_stream_rdr process_rdr;
    private Process_server_gobbler_error error_reader;
	public int Server_timeout() {return server_timeout;} public Scrib_server Server_timeout_(int v) {server_timeout = v; return this;} int server_timeout = 8000;
	public int Server_timeout_polling() {return server_timeout_polling;} public Scrib_server Server_timeout_polling_(int v) {server_timeout_polling = v; return this;} int server_timeout_polling = 1;
	public int Server_timeout_busy_wait() {return server_timeout_busy_wait;} public Scrib_server Server_timeout_busy_wait_(int v) {server_timeout_busy_wait = v; return this;} int server_timeout_busy_wait = 250;
    public void Init(String... process_args) {
        process_rdr = new Process_stream_rdr(bry_header, bry_body);
    	ProcessBuilder pb = new ProcessBuilder(process_args);
    	pb.redirectErrorStream(false);
    	try {process = pb.start();}
    	catch (Exception e) {throw Err_.err_(e, "process init failed: {0} {1}", String_.AryXtoStr(process_args), Err_.Message_gplx_brief(e));}
        stream_write = process.getOutputStream();
        error_reader = new Process_server_gobbler_error(process.getErrorStream(), bry_error);
        error_reader.Start();
    }
    public byte[] Server_comm(byte[] cmd, Object[] cmd_objs) {
    	Server_send(cmd, cmd_objs);
    	return Server_recv();
    }
    public void Server_send(byte[] cmd, Object[] cmd_objs) {
        if (process == null) throw Err_.new_("process not started");
        cmd_last = cmd;
//        stream_read.Data_reset();
        stream_read = new Process_server_gobbler_recv(process.getInputStream(), process_rdr).Start();
        try {
	        stream_write.write(cmd);
	        stream_write.flush();
        }
        catch (Exception e) {throw Err_.err_(e, "failed to write to output");}
    }	private byte[] cmd_last;
    public byte[] Server_recv() {
    	long time_bgn = System.currentTimeMillis();
    	long time_woke = time_bgn;
    	while (true) {
    		byte[] rv = stream_read.Data();
    		if (rv != null) return rv;
    		long time_now = System.currentTimeMillis();
    		if (time_now > time_woke + server_timeout_busy_wait) {
				if (time_now > time_bgn + server_timeout) throw Scrib_xtn_mgr.err_("lua_timeout: timeout={0} cmd={1}", server_timeout, String_.new_u8(cmd_last));
				Thread_adp_.Sleep(server_timeout_polling);
				time_woke = System.currentTimeMillis();
			}
        }
    }
   public void Term() {
	   if (error_reader != null) error_reader.Term();	// NOTE: need to interrupt close, else process stays open
	   if (stream_read != null) stream_read.Term();
	   if (process != null) process.destroy();
   }
}
class Process_server_gobbler_error extends Thread {
	private byte[] bfr;
	private InputStream stream;
    public Process_server_gobbler_error(InputStream stream, byte[] bfr) {
    	super("process.lua.error");
    	this.stream = stream;
    	this.bfr = bfr;
    }
    public Process_server_gobbler_error Start() {this.start(); return this;}
    public void run() {
        try {
        	while (true) {	// loop b/c one gobbler is reused for multiple calls
        		stream.read(bfr);
        		if (terminating) break;
        		// ThreadAdp_.Sleep(100); commented out; DATE:2014-03-20
        	}
        }
        catch (Exception e) {
        	if (terminating)
        		return;
        	else
        		throw Err_.err_(e, "failed to write to output");
        }
    }
    public void Term() {
    	terminating = true;
    	this.interrupt();
    }	boolean terminating = false;
}
class Process_server_gobbler_recv extends Thread {    
    private Process_stream_rdr process_rdr;
    private InputStream stream;
    public Process_server_gobbler_recv(InputStream stream, Process_stream_rdr process_rdr) {
    	super("process.lua.read");
    	this.stream = stream; this.process_rdr = process_rdr;
    }
    public void Data_reset() {data = null;}
    public byte[] Data() {return data;} private byte[] data;
    public Process_server_gobbler_recv Start() {
    	process_rdr.Rdr().UnderRdr_(stream);
    	this.start();
    	return this;
    }
    public void run() {
    	data = process_rdr.Read();
    }
    public void Term() {
    	this.interrupt();
    }
}
