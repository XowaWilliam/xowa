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
package gplx.xowa.bldrs.css; import gplx.*; import gplx.xowa.*; import gplx.xowa.bldrs.*;
import gplx.core.btries.*; import gplx.core.primitives.*;
class Xob_css_parser {
	private final Bry_bfr bfr = Bry_bfr.new_(255);
	private final Xob_mirror_mgr mgr;
	private final Xob_css_parser__url url_parser; private final Xob_css_parser__import import_parser;
	public Xob_css_parser(Xob_mirror_mgr mgr) {
		this.mgr = mgr;
		this.url_parser = new Xob_css_parser__url(mgr.Site_url());
		this.import_parser = new Xob_css_parser__import(url_parser);
	}
	public void Parse(byte[] src) {
		int src_len = src.length; int pos = 0;
		while (pos < src_len) {
			byte b = src[pos];
			Object o = tkns_trie.Match_bgn_w_byte(b, src, pos, src_len);
			if (o == null) {
				bfr.Add_byte(b);
				++pos;
			}
			else {
				byte tkn_tid = ((Byte_obj_val)o).Val();
				int match_pos = tkns_trie.Match_pos();
				Xob_css_tkn__base tkn = null;
				switch (tkn_tid) {
					case Tkn_url:		tkn = url_parser.Parse(src, src_len, pos, match_pos); break;
					case Tkn_import:	tkn = import_parser.Parse(src, src_len, pos, match_pos); break;
				}
				tkn.Process(mgr);
				pos = tkn.Write(bfr, src);
			}
		}
	}
	private static final byte Tkn_import = 1, Tkn_url = 2;
	private static final Btrie_slim_mgr tkns_trie = Btrie_slim_mgr.ci_ascii_()
	.Add_str_byte("@import"		, Tkn_import)
	.Add_str_byte(" url("		, Tkn_url)
	;
}
