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
package gplx.xowa.files.xfers; import gplx.*; import gplx.xowa.*; import gplx.xowa.files.*;
import gplx.core.primitives.*; import gplx.dbs.*;
import gplx.ios.*; import gplx.xowa.wikis.*; import gplx.xowa.files.*;
public class Xof_xfer_queue_html_fxt extends Xof_xfer_queue_base_fxt {
	private final Xof_xfer_queue queue = new Xof_xfer_queue();
	@Override public void Clear(boolean src_repo_is_wmf) {
		Db_conn_bldr.I.Reg_default_mem();
		super.Clear(src_repo_is_wmf);
		this.Api_size().Clear();
	}
	public Xof_xfer_queue_html_fxt Lnki_orig_ (String lnki_ttl)							{return Lnki_(lnki_ttl, Bool_.N, Xof_img_size.Size_null_deprecated, Xof_img_size.Size_null_deprecated, Xop_lnki_tkn.Upright_null, Xof_lnki_time.Null_as_int);}
	public Xof_xfer_queue_html_fxt Lnki_thumb_(String lnki_ttl, int lnki_w)				{return Lnki_(lnki_ttl, Bool_.Y, lnki_w, Xof_img_size.Size_null_deprecated, Xop_lnki_tkn.Upright_null, Xof_lnki_time.Null_as_int);}
	public Xof_xfer_queue_html_fxt Lnki_thumb_(String lnki_ttl, int lnki_w, int lnki_h) {return Lnki_(lnki_ttl, Bool_.Y, lnki_w, lnki_h, Xop_lnki_tkn.Upright_null, Xof_lnki_time.Null_as_int);}
	public Xof_xfer_queue_html_fxt Lnki_(String lnki_ttl, boolean thumb, int lnki_w, int lnki_h, double upright, int seek_time) { // NOTE: only one xfer_itm; supports one Lnki_ per test only
		Xowe_wiki wiki = this.En_wiki();
		xfer_itm = wiki.Html_mgr().Html_wtr().Lnki_wtr().File_wtr().Lnki_eval(Xof_exec_tid.Tid_wiki_page, wiki.Ctx(), wiki.Ctx().Cur_page(), queue, Bry_.new_u8(lnki_ttl), thumb ? Xop_lnki_type.Id_thumb : Xop_lnki_type.Id_null, upright, lnki_w, lnki_h, Xof_lnki_time.X_int(seek_time), Xof_lnki_page.Null, false);
		return this;
	}
	public Xof_file_itm Xfer_itm() {return xfer_itm;} private Xof_file_itm xfer_itm; 
	public Xof_xfer_queue_html_fxt Src(Io_fil... v) {return (Xof_xfer_queue_html_fxt)Src_base(v);}
	public Xof_xfer_queue_html_fxt Trg(Io_fil... v) {return (Xof_xfer_queue_html_fxt)Trg_base(v);}
	public Xof_xfer_queue_html_fxt Html_src_(String v) {return (Xof_xfer_queue_html_fxt)Html_src_base_(v);}
	public Xof_xfer_queue_html_fxt Html_size_(int w, int h) {this.Html_w_(w); this.Html_h_(h); return this;}
	public Xof_xfer_queue_html_fxt Html_orig_src_(String v) {html_orig_src = v; return this;} private String html_orig_src;
	public Xof_xfer_queue_html_fxt ini_page_api(String wiki_str, String ttl_str, String redirect_str, int orig_w, int orig_h) {return ini_page_api(wiki_str, ttl_str, redirect_str, orig_w, orig_h, true);}
	public Xof_xfer_queue_html_fxt ini_page_api(String wiki_str, String ttl_str, String redirect_str, int orig_w, int orig_h, boolean pass) {
		String wiki_key = String_.Eq(wiki_str, "commons") ? Xow_domain_.Domain_str_commons : Xow_domain_.Domain_str_enwiki;
		this.Api_size().Ini(wiki_key, ttl_str, redirect_str, orig_w, orig_h, pass);
		return this;
	}
	@gplx.Virtual public void tst() {
		Xowe_wiki wiki = this.En_wiki();
		ini_src_fils();
		wiki.Appe().File_mgr().Wmf_mgr().Enabled_(true);
		wiki.File_mgr().Cfg_download().Enabled_(true);
		queue.Exec(wiki, Xoae_page.new_(wiki, wiki.Ttl_parse(Bry_.new_a7("A"))));
		tst_trg_fils();
		if (this.html_orig_src   != null)	Tfds.Eq(this.html_orig_src  , xfer_itm.Html_orig_url().To_http_file_str());
		if (this.Html_view_src() != null)	Tfds.Eq(this.Html_view_src(), xfer_itm.Html_view_url().To_http_file_str());
		if (this.Html_w() != -1)			Tfds.Eq(this.Html_w(), xfer_itm.Html_w());
		if (this.Html_h() != -1)			Tfds.Eq(this.Html_h(), xfer_itm.Html_h());
		queue.Clear();
	}
}
