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
package gplx.core.xmls; import gplx.*; import gplx.core.*;
public class Gfo_xml_wtr {
	private final Bry_bfr bfr = Bry_bfr.reset_(255), txt_bfr = Bry_bfr.reset_(32);
	private byte quote_byte = Byte_ascii.Apos;
	private byte[] quote_escape = Bry_quote_1_escape;
	private List_adp nde_stack = List_adp_.new_();
	private Gfo_xml_nde nde_cur = null;
	private int indent = 0;
	public void Quote_(boolean apos) {
		if (apos) {
			this.quote_byte = Byte_ascii.Apos;
			this.quote_escape = Bry_quote_1_escape;
		}
		else {
			this.quote_byte = Byte_ascii.Quote;
			this.quote_escape = Bry_quote_2_escape;
		}
	}
	public Gfo_xml_wtr Nde_lhs_bgn_grp(String v) {return Nde_lhs_bgn(Bool_.Y, v);}
	public Gfo_xml_wtr Nde_lhs_bgn_itm(String v) {return Nde_lhs_bgn(Bool_.N, v);}
	private Gfo_xml_wtr Nde_lhs_bgn(boolean grp, String v) {
		nde_cur = new Gfo_xml_nde(grp, v);
		nde_stack.Add(nde_cur);
		bfr.Add_byte_repeat(Byte_ascii.Space, indent);
		bfr.Add_byte(Byte_ascii.Angle_bgn).Add_str_u8(v);
		indent += 2;
		return this;
	}
	public Gfo_xml_wtr Nde_lhs_end() {
		bfr.Add_byte(Byte_ascii.Angle_end);
		if (nde_cur.Grp()) bfr.Add_byte_nl();
		return this;
	}
	public Gfo_xml_wtr Nde_lhs(String v) {return Nde_lhs(Bool_.Y, v);}
	private Gfo_xml_wtr Nde_lhs(boolean grp, String v) {
		this.Nde_lhs_bgn(grp, v);
		this.Nde_lhs_end();
		return this;
	}
	public Gfo_xml_wtr Nde_rhs() {
		Gfo_xml_nde nde = (Gfo_xml_nde)List_adp_.Pop(nde_stack);
		indent -= 2;
		if (nde.Grp()) bfr.Add_byte_repeat(Byte_ascii.Space, indent);
		bfr.Add(Bry_nde_rhs_bgn).Add_str_u8(nde.Name()).Add_byte(Byte_ascii.Angle_end);	// EX: </node>
		bfr.Add_byte_nl();
		return this;
	}
	public Gfo_xml_wtr Nde_txt_str(String name, String text) {
		this.Nde_lhs(Bool_.N, name);
		this.Txt_str_u8(text);
		this.Nde_rhs();
		return this;
	}
	public Gfo_xml_wtr Nde_txt_bry(String name, byte[] text) {
		this.Nde_lhs(Bool_.N, name);
		this.Txt_bry(text);
		this.Nde_rhs();
		return this;
	}
	public Gfo_xml_wtr Nde_txt_int(String name, int text) {
		this.Nde_lhs(Bool_.N, name);
		this.Txt_bry(Int_.Xto_bry(text));
		this.Nde_rhs();
		return this;
	}
	public Gfo_xml_wtr Atr_bgn(String key) {
		bfr.Add_byte_space().Add_str_u8(key).Add_byte(Byte_ascii.Eq).Add_byte(quote_byte);
		return this;
	}
	public Gfo_xml_wtr Atr_val_str_a7(String v)		{bfr.Add_str_a7(v); return this;}
	public Gfo_xml_wtr Atr_val_str_u8(String v)		{bfr.Add_str_u8 (v); return this;}
	public Gfo_xml_wtr Atr_val_bry		(byte[] v)	{bfr.Add(v); return this;}
	public Gfo_xml_wtr Atr_val_int		(int v)		{bfr.Add_int_variable(v); return this;}
	public Gfo_xml_wtr Atr_end() {
		bfr.Add_byte(quote_byte);
		return this;
	}
	public Gfo_xml_wtr Atr_kv_int(String key, int val)			{return Atr_kv_bry(key, Int_.Xto_bry(val));}
	public Gfo_xml_wtr Atr_kv_str_a7(String key, String val)	{return Atr_kv_bry(key, Bry_.new_a7(val));}
	public Gfo_xml_wtr Atr_kv_str_u8(String key, String val)	{return Atr_kv_bry(key, Bry_.new_u8(val));}
	public Gfo_xml_wtr Atr_kv_bry(String key, byte[] val) {
		bfr.Add_byte_space().Add_str_u8(key);
		bfr.Add_byte(Byte_ascii.Eq);
		Atr_val_quote(val);
		return this;
	}
	private Gfo_xml_wtr Atr_val_quote(byte[] val_bry) {
		bfr.Add_byte(quote_byte);
		bfr.Add_bry_escape(quote_byte, quote_escape, val_bry, 0, val_bry.length);
		bfr.Add_byte(quote_byte);
		return this;
	}
	public Gfo_xml_wtr Txt_bry(byte[] txt) {
		int len = txt.length;
		boolean dirty = false;
		for (int i = 0; i < len; ++i) {
			byte[] escape = null;
			byte b = txt[i];
			switch (b) {
				case Byte_ascii.Lt:		escape = Bry_escape_lt; break;
				case Byte_ascii.Gt:		escape = Bry_escape_gt; break;
				case Byte_ascii.Amp:	escape = Bry_escape_amp; break;
				default: break;
			}
			if (escape != null && !dirty) {
				bfr.Add_mid(txt, 0, i);
				dirty = true;
			}
			if (dirty) {
				if (escape == null) bfr.Add_byte(b);
				else				bfr.Add(escape);
			}
		}
		if (dirty)	bfr.Add_bfr_and_clear(txt_bfr);
		else		bfr.Add(txt);
		return this;
	}
	public Gfo_xml_wtr Txt_str_u8(String txt)	{return Txt_bry(Bry_.new_u8(txt));}
	public String Bld_str() {return bfr.Xto_str_and_clear();}
	private static final byte[]
	  Bry_nde_rhs_bgn		= Bry_.new_a7("</")
//		, Bry_nde_inline		= Bry_.new_a7("/>")
	, Bry_quote_1_escape	= Bry_.new_a7("&apos;")
	, Bry_quote_2_escape	= Bry_.new_a7("&quot;")
	, Bry_escape_lt			= Bry_.new_a7("&lt;")
	, Bry_escape_gt			= Bry_.new_a7("&gt;")
	, Bry_escape_amp		= Bry_.new_a7("&amp;")
	;
}
class Gfo_xml_nde {
	public Gfo_xml_nde(boolean grp, String name) {this.grp = grp; this.name = name;}
	public boolean Grp() {return grp;} private final boolean grp;
	public String Name() {return name;} private final String name;
}
