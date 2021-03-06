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
package gplx.xowa.langs.cases; import gplx.*; import gplx.xowa.*; import gplx.xowa.langs.*;
import gplx.core.btries.*; import gplx.intl.*;
public class Xol_case_mgr implements GfoInvkAble, Gfo_case_mgr {
	private Bry_bfr tmp_bfr = Bry_bfr.new_(); private Btrie_fast_mgr upper_trie = Btrie_fast_mgr.cs_(), lower_trie = Btrie_fast_mgr.cs_(); private Xol_case_itm[] itms;
	public Xol_case_mgr(byte tid) {this.tid = tid;}
	public byte Tid() {return tid;} private byte tid;
	public Gfo_case_itm Get_or_null(byte bgn_byte, byte[] src, int bgn, int end) {
		Object rv = lower_trie.Match_bgn_w_byte(bgn_byte, src, bgn, end);
		return rv == null
			? (Gfo_case_itm)upper_trie.Match_bgn_w_byte(bgn_byte, src, bgn, end)
			: (Gfo_case_itm)rv;
	}
	public void Clear() {upper_trie.Clear(); lower_trie.Clear();}
	public boolean Match_any_exists(byte b, byte[] src, int bgn_pos, int end_pos) {
		return upper_trie.Match_bgn_w_byte(b, src, bgn_pos, end_pos) != null
			|| lower_trie.Match_bgn_w_byte(b, src, bgn_pos, end_pos) != null
			;
	}
	public Object Match_upper(byte b, byte[] src, int bgn_pos, int end_pos) {return upper_trie.Match_bgn_w_byte(b, src, bgn_pos, end_pos);}
	public void Add_bulk(byte[] raw) {Add_bulk(Xol_case_itm_.parse_xo_(raw));}
	public Xol_case_mgr Add_bulk(Xol_case_itm[] ary) {
		itms = ary;
		int itms_len = itms.length;
		for (int i = 0; i < itms_len; i++) {
			Xol_case_itm itm = itms[i];
			switch (itm.Tid()) {
				case Xol_case_itm_.Tid_both:
					upper_trie.Add(itm.Src_ary(), itm);
					lower_trie.Add(itm.Trg_ary(), itm);
					break;
				case Xol_case_itm_.Tid_upper:
					upper_trie.Add(itm.Src_ary(), itm);
					break;
				case Xol_case_itm_.Tid_lower:
					lower_trie.Add(itm.Src_ary(), itm);
					break;
			}
		}
		return this;
	}
	public byte[] Case_reuse_upper(byte[] src, int bgn, int end) {return Case_reuse(Bool_.Y, src, bgn, end);}
	public byte[] Case_reuse_lower(byte[] src, int bgn, int end) {return Case_reuse(Bool_.N, src, bgn, end);}
	public byte[] Case_reuse(boolean upper, byte[] src, int bgn, int end) {
		int pos = bgn;
		tmp_bfr.Clear();
		Btrie_fast_mgr trie = upper ? upper_trie : lower_trie;
		while (true) {
			if (pos >= end) break;
			byte b = src[pos];
			int b_len = gplx.intl.Utf8_.Len_of_char_by_1st_byte(b);
			Object o = trie.Match_bgn_w_byte(b, src, pos, end);	// NOTE: used to be (b, src, bgn, end) which would never case correctly; DATE:2013-12-25
			if (o != null && pos < end) {	// pos < end used for casing 1st letter only; upper_1st will pass end of 1
				Xol_case_itm itm = (Xol_case_itm)o;
				if (upper)
					itm.Case_reuse_upper(src, pos, b_len);
				else
					itm.Case_reuse_lower(src, pos, b_len);
			}
			else {}	// noop
			pos += b_len;
		}
		return src;
	}
	public byte[] Case_reuse_1st_upper(byte[] src) {	// NOTE: optimized version called by Frame_ttl; DATE:2014-06-21
		int src_len = src.length;
		if (src_len == 0) return src; // empty bry
		byte b = src[0];
		int b_len = gplx.intl.Utf8_.Len_of_char_by_1st_byte(b);
		Object o = upper_trie.Match_bgn_w_byte(b, src, 0, b_len);
		if (o == null) return src;	// 1st letter is not a lower case char (either num, symbol, or upper)
		Xol_case_itm itm = (Xol_case_itm)o;
		itm.Case_build_upper(tmp_bfr);
		int pos = upper_trie.Match_pos();
		tmp_bfr.Add_mid(src, pos, src_len);
		return tmp_bfr.Xto_bry_and_clear();
	}
	public byte[] Case_build_upper(byte[] src) {return Case_build_upper(src, 0, src.length);}
	public byte[] Case_build_upper(byte[] src, int bgn, int end) {return Case_build(Bool_.Y, src, bgn, end);}
	public byte[] Case_build_lower(byte[] src) {return Case_build_lower(src, 0, src.length);}
	public byte[] Case_build_lower(byte[] src, int bgn, int end) {return Case_build(Bool_.N, src, bgn, end);}
	public byte[] Case_build(boolean upper, byte[] src, int bgn, int end) {
		int pos = bgn;
		tmp_bfr.Clear();
		Btrie_fast_mgr trie = upper ? upper_trie : lower_trie;
		while (true) {
			if (pos >= end) break;
			byte b = src[pos];
			int b_len = gplx.intl.Utf8_.Len_of_char_by_1st_byte(b);
			Object o = trie.Match_bgn_w_byte(b, src, pos, end);	// NOTE: used to be (b, src, bgn, end) which would never case correctly; DATE:2013-12-25
			if (o != null && pos < end) {	// pos < end used for casing 1st letter only; upper_1st will pass end of 1
				Xol_case_itm itm = (Xol_case_itm)o;
				if (upper)
					itm.Case_build_upper(tmp_bfr);
				else
					itm.Case_build_lower(tmp_bfr);
			}
			else {
				tmp_bfr.Add_mid(src, pos, pos + b_len);
			}
			pos += b_len;
		}
		return tmp_bfr.Xto_bry_and_clear();
	}
	public byte[] Case_build_1st_upper(Bry_bfr bfr, byte[] src, int bgn, int end) {return Case_build_1st(bfr, Bool_.Y, src, bgn, end);}
	public byte[] Case_build_1st_lower(Bry_bfr bfr, byte[] src, int bgn, int end) {return Case_build_1st(bfr, Bool_.N, src, bgn, end);}
	public byte[] Case_build_1st(Bry_bfr bfr, boolean upper, byte[] src, int bgn, int end) {
		if (bgn == end) return Bry_.Empty;	// upper "" -> ""
		int b_len = gplx.intl.Utf8_.Len_of_char_by_1st_byte(src[bgn]);
		bfr.Add(Case_build(upper, src, bgn, bgn + b_len));
		bfr.Add_mid(src, bgn + b_len, end);
		return bfr.Xto_bry_and_clear();
	}
	public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, Invk_add_bulk))				Add_bulk(m.ReadBry("v"));
		else if	(ctx.Match(k, Invk_clear))					throw Err_.not_implemented_();
		else	return GfoInvkAble_.Rv_unhandled;
		return this;
	}	private static final String Invk_clear = "clear", Invk_add_bulk = "add_bulk";
}
