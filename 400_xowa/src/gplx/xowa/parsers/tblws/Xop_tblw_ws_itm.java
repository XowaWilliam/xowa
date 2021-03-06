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
package gplx.xowa.parsers.tblws; import gplx.*; import gplx.xowa.*; import gplx.xowa.parsers.*;
import gplx.core.btries.*;
public class Xop_tblw_ws_itm {
	public byte Tblw_type() {return tblw_type;} private byte tblw_type;
	public int Hook_len() {return hook_len;} private int hook_len;
	public Xop_tblw_ws_itm(byte tblw_type, int hook_len) {this.tblw_type = tblw_type; this.hook_len = hook_len;}

	public static final byte Type_tb = Xop_tblw_wkr.Tblw_type_tb, Type_te = Xop_tblw_wkr.Tblw_type_te, Type_tr = Xop_tblw_wkr.Tblw_type_tr, Type_tc = Xop_tblw_wkr.Tblw_type_tc
		, Type_th = Xop_tblw_wkr.Tblw_type_th, Type_td = Xop_tblw_wkr.Tblw_type_td, Type_nl = 16, Type_xnde = 17;
	public static Btrie_slim_mgr trie_() {// MW.REF:Parser.php|doBlockLevels
		Btrie_slim_mgr rv = Btrie_slim_mgr.cs_();
		trie_itm(rv, Type_tb, Xop_tblw_lxr_ws.Hook_tb);
		trie_itm(rv, Type_te, Xop_tblw_lxr_ws.Hook_te);
		trie_itm(rv, Type_tr, Xop_tblw_lxr_ws.Hook_tr);
		trie_itm(rv, Type_th, Xop_tblw_lxr_ws.Hook_th);
		trie_itm(rv, Type_tc, Xop_tblw_lxr_ws.Hook_tc);
		trie_itm(rv, Type_td, Bry_.bytes_(Byte_ascii.Pipe));
		trie_itm(rv, Type_nl, Bry_.bytes_(Byte_ascii.NewLine));
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_table);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_tr);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_td);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_th);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_blockquote);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_h1);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_h2);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_h3);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_h4);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_h5);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_h6);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_pre);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_p);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_div);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_hr);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_li);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_ul);
		trie_itm_xnde(rv, Xop_xnde_tag_.Tag_ol);
		return rv;
	}
	private static void trie_itm(Btrie_slim_mgr trie, byte type, byte[] bry) {trie.Add_obj(bry, new Xop_tblw_ws_itm(type, bry.length));}
	private static void trie_itm_xnde(Btrie_slim_mgr trie, Xop_xnde_tag tag) {
		byte[] tag_name = tag.Name_bry();
		int tag_name_len = tag_name.length;
		trie.Add_obj(Bry_.Add(Bry_xnde_bgn, tag_name), new Xop_tblw_ws_itm(Type_xnde, tag_name_len));
		trie.Add_obj(Bry_.Add(Bry_xnde_end, tag_name), new Xop_tblw_ws_itm(Type_xnde, tag_name_len + 1));
	}	static byte[] Bry_xnde_bgn = new byte[] {Byte_ascii.Lt, Byte_ascii.Slash}, Bry_xnde_end = new byte[] {Byte_ascii.Lt};
}
