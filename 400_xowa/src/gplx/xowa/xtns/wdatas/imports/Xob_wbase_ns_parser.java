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
package gplx.xowa.xtns.wdatas.imports; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*; import gplx.xowa.xtns.wdatas.*;
import gplx.dbs.*;
import gplx.xowa.wikis.*;
class Xob_wbase_ns_parser {
	private final Hash_adp_bry ns_mgr_hash = Hash_adp_bry.cs_();
	private final Xowmf_site_tbl tbl_site; private final Xowmf_ns_tbl tbl_itm;
	public Xob_wbase_ns_parser(Db_conn conn) {
		this.tbl_site	= new Xowmf_site_tbl(conn);
		this.tbl_itm	= new Xowmf_ns_tbl(conn);
	}
	public void Find(Xob_wbase_ns_parser_rslt rv, byte[] wiki_abrv, byte[] ttl) {	// enwiki, Category:Abc
		Xow_ns_mgr ns_mgr = (Xow_ns_mgr)ns_mgr_hash.Get_by_bry(wiki_abrv);
		rv.Init(Xow_ns_.Id_main, 0);	// default to Main ns
		int ttl_len = ttl.length;
		int colon_pos = Bry_finder.Find_fwd(ttl, Byte_ascii.Colon, 0, ttl_len); if (colon_pos == Bry_finder.Not_found) return;
		if (ns_mgr == null) {			// ns_mgr not found; load from db
			wiki_abrv = Bry_.Replace(wiki_abrv, Byte_ascii.Underline, Byte_ascii.Dash);
			byte[] wiki_domain = Xow_wiki_alias.Parse__domain_name(wiki_abrv, 0, wiki_abrv.length);
			int site_id = tbl_site.Select_id(String_.new_u8(wiki_domain)); if (site_id == -1) {Xoa_app_.Usr_dlg().Warn_many("", "", "wbase.ns_parser:unknown wmf_abrv; abrv=~{0}", wiki_abrv); return;}
			ns_mgr = new Xow_ns_mgr(gplx.xowa.langs.cases.Xol_case_mgr_.Utf8());
			tbl_itm.Select_all(ns_mgr, site_id); if (ns_mgr.Count() == 0) {Xoa_app_.Usr_dlg().Warn_many("", "", "wbase.ns_parser:no ns found; abrv=~{0}", wiki_abrv); return;}
			ns_mgr_hash.Add_bry_obj(wiki_abrv, ns_mgr);
		}
		Xow_ns ns = ns_mgr.Names_get_or_null(ttl, 0, colon_pos); if (ns == null) return; // not a ns; EX: "No_namespace:Page_title"
		rv.Init(ns.Id(), colon_pos + 1);
	}		
}
class Xob_wbase_ns_parser_rslt {
	public int Ns_id() {return ns_id;} private int ns_id;
	public int Ttl_bgn() {return ttl_bgn;} private int ttl_bgn;
	public void Init(int ns_id, int ttl_bgn) {
		this.ns_id = ns_id; this.ttl_bgn = ttl_bgn;
	}
}
