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
package gplx.xowa.hdumps; import gplx.*; import gplx.xowa.*;
import org.junit.*; import gplx.xowa.files.*; import gplx.xowa.hdumps.dbs.*;
public class Xodb_hdump_mgr__write_tst {
	@Before public void init() {fxt.Clear();} private Xodb_hdump_mgr__write_fxt fxt = new Xodb_hdump_mgr__write_fxt();
	@Test   public void Image_full() {
		fxt.Expd_itms_xfers(fxt.Make_xfer(0, 0, 0, "A.png", "trg/orig/7/0/A.png"));
		fxt.Test_write
		( "[[File:A.png|test_caption]]"
		, "<a href=\"/wiki/File:A.png\" class=\"image\" xowa_title=\"A.png\"><img id=\"xowa_file_img_0\" alt=\"test_caption\" xowa_img='0' /></a>");
	}
	@Test   public void Image_thumb() {
		fxt.Expd_itms_xfers(fxt.Make_xfer(0, 0, 0, "A.png", "trg/thumb/7/0/A.png/220px.png"));
		fxt.Test_write
		( "[[File:A.png|thumb|test_caption]]", String_.Concat_lines_nl_skip_last
		( "<div class=\"thumb tright\">"
		, "  <div id=\"xowa_file_div_0\" class=\"thumbinner\" xowa_img_style='0'>"
		, "    <a href=\"/wiki/File:A.png\" class=\"image\" xowa_title=\"A.png\"><img id=\"xowa_file_img_0\" alt=\"\" xowa_img='0' /></a>"
		, "    <div class=\"thumbcaption\"><xowa_mgnf id='0'/>"
		, "      test_caption"
		, "    </div>"
		, "  </div>"
		, "</div>"
		));
	}
	@Test   public void Audio_thumb() {
		fxt.Expd_itms_xfers(fxt.Make_xfer(0, 220, -1, "A.oga", ""));
		fxt.Test_write
		( "[[File:A.oga|thumb|test_caption]]", String_.Concat_lines_nl_skip_last
		( "<div class=\"thumb tright\">"
		, "  <div id=\"xowa_file_div_0\" class=\"thumbinner\" xowa_img_style='0'>"
		, "    <div id=\"xowa_media_div\"><xowa_play id='0'/><xowa_info id='0'/>"
		, "    </div>"
		, "    <div class=\"thumbcaption\"><xowa_mgnf id='0'/>"
		, "      test_caption"
		, "    </div>"
		, "  </div>"
		, "</div>"
		));
	}
	@Test   public void Video_thumb() {
		fxt.Expd_itms_xfers(fxt.Make_xfer(0, 0, 0, "A.ogv", ""));
		fxt.Test_write
		( "[[File:A.ogv|thumb|test_caption]]", String_.Concat_lines_nl_skip_last
		( "<div class=\"thumb tright\">"
		, "  <div id=\"xowa_file_div_0\" class=\"thumbinner\" xowa_img_style='0'>"
		, "    <div id=\"xowa_media_div\">"
		, "      <div>"
		, "        <a href=\"/wiki/File:A.ogv\" class=\"image\" title=\"A.ogv\">"
		, "          <img id=\"xowa_file_img_0\" xowa_img='0' alt=\"\" />"
		, "        </a>"
		, "      </div><xowa_play id='0'/>"
		, "    </div>"
		, "    <div class=\"thumbcaption\"><xowa_mgnf id='0'/>"
		, "      test_caption"
		, "    </div>"
		, "  </div>"
		, "</div>"
		));
	}
}
class Xodb_hdump_mgr__base_fxt {
	protected Xodb_hdump_mgr hdump_mgr;
	protected Bry_bfr bfr = Bry_bfr.reset_(255);
	protected Xow_wiki wiki; protected Xoa_page page;
	public Xop_fxt Fxt() {return fxt;} protected Xop_fxt fxt; 
	public void Clear() {
		if (fxt == null) {
			fxt = new Xop_fxt();
			wiki = fxt.Wiki();
			page = wiki.Ctx().Cur_page();
			hdump_mgr = new Xodb_hdump_mgr(wiki);
		}
		fxt.Reset();
		page.Revision_data().Id_(0);
		this.Clear_end();
 		}
	@gplx.Virtual public void Clear_end() {}
	public void Exec_write(String raw) {
		Xop_root_tkn root = fxt.Exec_parse_page_all_as_root(Bry_.new_utf8_(raw));
		page.Root_(root);
		hdump_mgr.Write(bfr, page);
	}
}
class Xodb_hdump_mgr__write_fxt extends Xodb_hdump_mgr__base_fxt {
	private ListAdp expd_itms_xfers = ListAdp_.new_();
	@Override public void Clear_end() {expd_itms_xfers.Clear();}
	public Xof_xfer_itm Make_xfer(int uid, int img_w, int img_h, String lnki_ttl, String img_src) {
		Xof_xfer_itm rv = new Xof_xfer_itm();
		rv.Init_for_test__hdump(uid, img_w, img_h, Bry_.new_utf8_(lnki_ttl), Bry_.new_utf8_(img_src));
		return rv;
	}
	public void Expd_itms_xfers(Xof_xfer_itm... itms) {expd_itms_xfers.AddMany((Object[])itms);}
	public void Test_write(String raw, String expd_html) {
		this.Exec_write(raw);
            Tfds.Eq_str_lines(expd_html, String_.new_utf8_(page.Hdump_data().Body()));
		if (expd_itms_xfers.Count() > 0) Tfds.Eq_ary_str(Xfer_to_str_ary(expd_itms_xfers), Xfer_to_str_ary(page.Hdump_data().Imgs()));
	}
	private static String[] Xfer_to_str_ary(ListAdp list) {
		int len = list.Count();
		String[] rv = new String[len];
		for (int i = 0; i < len; ++i) {
			Xof_xfer_itm itm = (Xof_xfer_itm)list.FetchAt(i);
			rv[i] = String_.Concat_with_str("|", Int_.XtoStr(itm.Html_uid()), Int_.XtoStr(itm.Html_w()), Int_.XtoStr(itm.Html_h()), String_.new_utf8_(itm.Html_view_src_rel()));
		}
		return rv;
	}
}