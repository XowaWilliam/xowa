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
package gplx.xowa.servers.tcp; import gplx.*; import gplx.xowa.*; import gplx.xowa.servers.*;
import gplx.core.primitives.*;
import gplx.ios.*; import gplx.texts.*;
public class Xosrv_msg_rdr {
	public Xosrv_msg_rdr(byte[] default_body_bry, IoStream rdr) {this.default_body_bry = default_body_bry; default_body_bry_len = default_body_bry.length; this.rdr = rdr;} private byte[] header_bry = new byte[24], default_body_bry; int default_body_bry_len;
	public IoStream Rdr() {return rdr;} private IoStream rdr;
	public Xosrv_msg Read() {
		int bytes_read = rdr.Read(header_bry, 0, 24);	// 24 = version(1) + pipe + msg_len (10) + pipe + cksum (10) + pipe
		if (bytes_read < 24) {
			if (bytes_read == -1)	return Xosrv_msg.Exit;	// stream closed; should only occur when shutting down
			else					return Xosrv_msg.fail_("header is invalid; hdr:{0}", String_.new_u8(header_bry, 0, bytes_read));
		}
		byte version = header_bry[0];									if (version != Byte_ascii.Num_0)	return Xosrv_msg.fail_("version must be 0; version:{0}", Byte_.Xto_str(version));
		int body_len = Bry_.Xto_int_or(header_bry,  2, 12, -1); 	if (body_len == -1)					return Xosrv_msg.fail_("body_len is not number; body_len:{0}", String_.new_u8(header_bry,  2, 23));
		int cksum    = Bry_.Xto_int_or(header_bry, 13, 23, -1);	if (cksum == -1)					return Xosrv_msg.fail_("checksum is not number; cksum:{0}", String_.new_u8(header_bry, 13, 23));
		if (!Chk_bytes(header_bry, Byte_ascii.Pipe, 1, 12, 23)) return Xosrv_msg.fail_("message should be delimited by pipes at 1, 12 and 23; message:{0}", String_.new_u8(header_bry, 0, 24));
		if (cksum != (body_len * 2) + 1) return Xosrv_msg.fail_("checksum failed; body_len:{0} chksum:{1}", body_len, cksum);
		byte[] body_bry = body_len > default_body_bry_len ? new byte[body_len] : default_body_bry;
		rdr.Read(body_bry, 0, body_len);
		Int_obj_ref fld_bgn = Int_obj_ref.zero_(); Bool_obj_ref fail_ref = Bool_obj_ref.n_(); String_obj_ref fld_ref = String_obj_ref.null_();
		byte[] cmd_name		= Read_fld(body_bry, body_len, fld_bgn, fail_ref, fld_ref.Val_("cmd_name"));	if (fail_ref.Val()) return Read_fld_fail(fld_ref, body_bry);
		byte[] msg_id		= Read_fld(body_bry, body_len, fld_bgn, fail_ref, fld_ref.Val_("msg_id"));		if (fail_ref.Val()) return Read_fld_fail(fld_ref, body_bry);
		byte[] sender		= Read_fld(body_bry, body_len, fld_bgn, fail_ref, fld_ref.Val_("sender"));		if (fail_ref.Val()) return Read_fld_fail(fld_ref, body_bry);
		byte[] recipient	= Read_fld(body_bry, body_len, fld_bgn, fail_ref, fld_ref.Val_("recipient"));	if (fail_ref.Val()) return Read_fld_fail(fld_ref, body_bry);
		byte[] msg_date		= Read_fld(body_bry, body_len, fld_bgn, fail_ref, fld_ref.Val_("msg_date"));	if (fail_ref.Val()) return Read_fld_fail(fld_ref, body_bry);
		byte[] msg_text		= Bry_.Mid(body_bry, fld_bgn.Val(), body_len);
		return Xosrv_msg.new_(cmd_name, msg_id, sender, recipient, msg_date, msg_text);
	}
	private static byte[] Read_fld(byte[] bry, int bry_len, Int_obj_ref fld_bgn, Bool_obj_ref fail_ref, String_obj_ref fld_ref) {
		int fld_end = Bry_finder.Find_fwd(bry, Byte_ascii.Pipe, fld_bgn.Val(), bry_len);
		if (fld_end == Bry_.NotFound) {fail_ref.Val_y_(); return null;}
		byte[] rv = Bry_.Mid(bry, fld_bgn.Val(), fld_end);
		fld_bgn.Val_(fld_end + 1);	// +1 to place after pipe
		return rv;
	}
	private static Xosrv_msg Read_fld_fail(String_obj_ref fld_name, byte[] body_bry) {return Xosrv_msg.fail_("pipe not found for " + fld_name.Val() + "; body:{0}", String_.new_u8(body_bry));}
	private static boolean Chk_bytes(byte[] bry, byte expd, int... pos_ary) {
		int len = pos_ary.length;
		int bry_len = bry.length;
		for (int i = 0; i < len; i++) {
			int pos = pos_ary[i];
			if (pos >= bry_len) return false; // out of bounds; return false (don't fail)
			if (bry[pos] != expd) return false;
		}
		return true;
	}
}
