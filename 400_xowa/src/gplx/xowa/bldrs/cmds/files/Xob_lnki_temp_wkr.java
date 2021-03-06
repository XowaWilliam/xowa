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
package gplx.xowa.bldrs.cmds.files; import gplx.*; import gplx.xowa.*; import gplx.xowa.bldrs.*; import gplx.xowa.bldrs.cmds.*;
import gplx.dbs.*; import gplx.dbs.cfgs.*; import gplx.xowa.wikis.data.tbls.*; import gplx.xowa.pages.*;
import gplx.xowa.files.*;
import gplx.xowa.wikis.*;
import gplx.xowa.parsers.lnkis.redlinks.*; import gplx.xowa.parsers.logs.*; import gplx.xowa.html.hdumps.bldrs.*; import gplx.xowa.xtns.scribunto.*; import gplx.xowa.xtns.wdatas.*;
import gplx.fsdb.meta.*; import gplx.xowa.files.fsdb.*; import gplx.fsdb.*;
public class Xob_lnki_temp_wkr extends Xob_dump_mgr_base implements Xopg_redlink_logger {
	private Xob_lnki_temp_tbl tbl; private boolean wdata_enabled = true, xtn_ref_enabled = true, gen_html, gen_hdump;
	private Xop_log_invoke_wkr invoke_wkr; private Xop_log_property_wkr property_wkr;		
	private boolean ns_file_is_case_match_all = true; private Xowe_wiki commons_wiki;
	private Xob_hdump_bldr hdump_bldr; private Xob_link_dump_cmd link_dump_cmd; private boolean hzip_enabled = true;
	public Xob_lnki_temp_wkr(Xob_bldr bldr, Xowe_wiki wiki) {this.Cmd_ctor(bldr, wiki);}
	@Override public String Cmd_key() {return Xob_cmd_keys.Key_file_lnki_temp;}
	@Override public byte Init_redirect()	{return Bool_.N_byte;}	// lnki_temp does not look at redirect pages
	@Override public int[] Init_ns_ary()		{return ns_ids;} private int[] ns_ids = Int_.Ary(Xow_ns_.Id_main);
	@Override protected void Init_reset(Db_conn conn) {
		Db_cfg_tbl cfg_tbl = new Db_cfg_tbl(conn, "xowa_cfg");
		cfg_tbl.Delete_all();
		invoke_wkr.Init_reset();
		property_wkr.Init_reset();
	}
	@Override protected Db_conn Init_db_file() {
		ctx.Lnki().File_wkr_(this);
		Xob_db_file make_db_file = Xob_db_file.new__file_make(wiki.Fsys_mgr().Root_dir());
		Db_conn conn = make_db_file.Conn();
		this.tbl = new Xob_lnki_temp_tbl(conn);
		tbl.Create_tbl();
		return conn;
	}
	@Override protected void Cmd_bgn_end() {
		ns_file_is_case_match_all = Ns_file_is_case_match_all(wiki);							// NOTE: must call after wiki.init
		wiki.Html_mgr().Page_wtr_mgr().Wkr(Xopg_view_mode.Tid_read).Ctgs_enabled_(false);		// disable categories else progress messages written (also for PERF)
		if (wiki.File__bin_mgr() != null)
			wiki.File__bin_mgr().Wkrs__del(gplx.xowa.files.bins.Xof_bin_wkr_.Key_http_wmf);	// remove wmf wkr, else will try to download images during parsing
		commons_wiki = app.Wiki_mgr().Get_by_key_or_make(Xow_domain_.Domain_bry_commons);
		Xop_log_mgr log_mgr = ctx.App().Log_mgr();
		log_mgr.Log_dir_(wiki.Fsys_mgr().Root_dir());	// put log in wiki dir, instead of user.temp
		invoke_wkr = this.Invoke_wkr();					// set member reference
		invoke_wkr = log_mgr.Make_wkr_invoke();
		property_wkr = this.Property_wkr();				// set member reference
		property_wkr = log_mgr.Make_wkr_property();
		wiki.Appe().Wiki_mgr().Wdata_mgr().Enabled_(wdata_enabled);
		if (!xtn_ref_enabled) gplx.xowa.xtns.cite.References_nde.Enabled = false;
		gplx.xowa.xtns.gallery.Gallery_xnde.Log_wkr = log_mgr.Make_wkr().Save_src_str_(Bool_.Y);
		gplx.xowa.xtns.imaps.Imap_xnde.Log_wkr = log_mgr.Make_wkr();
		gplx.xowa.Xop_xnde_wkr.Timeline_log_wkr = log_mgr.Make_wkr();
		gplx.xowa.xtns.scores.Score_xnde.Log_wkr = log_mgr.Make_wkr();
		gplx.xowa.xtns.hieros.Hiero_xnde.Log_wkr = log_mgr.Make_wkr();
		Xof_fsdb_mgr__sql trg_fsdb_mgr = new Xof_fsdb_mgr__sql();
		wiki.File__fsdb_mode().Tid_v2_bld_y_();
		Fsdb_db_mgr__v2 fsdb_core = Fsdb_db_mgr__v2_bldr.I.Get_or_make(wiki, Bool_.Y);
		trg_fsdb_mgr.Init_by_wiki(wiki);
		Fsm_mnt_mgr trg_mnt_mgr = trg_fsdb_mgr.Mnt_mgr();
		wiki.File_mgr().Init_file_mgr_by_load(wiki);										// must happen after fsdb.make
		wiki.File__bin_mgr().Wkrs__del(gplx.xowa.files.bins.Xof_bin_wkr_.Key_http_wmf);	// must happen after init_file_mgr_by_load; remove wmf wkr, else will try to download images during parsing
		wiki.File__orig_mgr().Wkrs_del(gplx.xowa.files.origs.Xof_orig_wkr_.Tid_wmf_api);
		trg_mnt_mgr = new Fsm_mnt_mgr(); trg_mnt_mgr.Ctor_by_load(fsdb_core);
		trg_mnt_mgr.Mnts__get_insert_idx_(Fsm_mnt_mgr.Mnt_idx_main);
		Fsm_mnt_mgr.Patch(trg_mnt_mgr.Mnts__get_main().Cfg_mgr().Tbl()); // NOTE: see fsdb_make; DATE:2014-04-26
		if (gen_hdump) {
			gplx.xowa.apis.xowa.bldrs.imports.Xoapi_import import_cfg = wiki.Appe().Api_root().Bldr().Wiki().Import();
			hdump_bldr = new Xob_hdump_bldr(wiki.Ns_mgr(), wiki.Db_mgr_as_sql(), tbl.Conn(), import_cfg.Html_db_max(), hzip_enabled);
			link_dump_cmd = new Xob_link_dump_cmd();
			link_dump_cmd.Init_by_wiki(wiki);
		}
		tbl.Insert_bgn();
		log_mgr.Txn_bgn();
	}
	@Override public void Exec_pg_itm_hook(int ns_ord, Xow_ns ns, Xowd_page_itm db_page, byte[] page_src) {
		Xoa_ttl ttl = Xoa_ttl.parse_(wiki, ns.Gen_ttl(db_page.Ttl_page_db()));
		byte[] ttl_bry = ttl.Page_db();
		byte page_tid = Xow_page_tid.Identify(wiki.Domain_tid(), ns.Id(), ttl_bry);
		if (page_tid != Xow_page_tid.Tid_wikitext) return; // ignore js, css, lua, json
		Xoae_page page = ctx.Cur_page();
		page.Clear();
		page.Bldr__ns_ord_(ns_ord);
		page.Ttl_(ttl).Revision_data().Id_(db_page.Id());
		page.Redlink_lnki_list().Clear();
		if (ns.Id_tmpl())
			parser.Parse_text_to_defn_obj(ctx, ctx.Tkn_mkr(), wiki.Ns_mgr().Ns_template(), ttl_bry, page_src);
		else {
			parser.Parse_page_all_clear(root, ctx, ctx.Tkn_mkr(), page_src);
			if (gen_html) {
				page.Root_(root);
				if (!page.Redirected())
					wiki.Html_mgr().Page_wtr_mgr().Gen(ctx.Cur_page(), Xopg_view_mode.Tid_read);
			}
			if (gen_hdump) {
				page.Root_(root);
				hdump_bldr.Insert_page(page);
				link_dump_cmd.Page_bgn(page.Revision_data().Id());
				List_adp lnki_list = page.Redlink_lnki_list().Lnki_list();
				int len = lnki_list.Count();
				for (int i = 0; i < len; ++i) {
					Xop_lnki_tkn lnki = (Xop_lnki_tkn)lnki_list.Get_at(i);
					Xoa_ttl trg_ttl = lnki.Ttl();
					link_dump_cmd.Add(lnki.Html_uid(), trg_ttl.Ns().Id(), trg_ttl.Page_db());
				}
			}
			root.Clear();
		}
	}
	@Override public void Exec_commit_hook() {
		tbl.Conn().Txn_sav();
		if (gen_hdump) {
			hdump_bldr.Commit();
			link_dump_cmd.Wkr_commit();
		}
	}
	@Override public void Exec_end_hook() {
		if (gen_hdump) {
			hdump_bldr.Bld_term();
			link_dump_cmd.Wkr_end();
		}
		String err_filter_mgr = invoke_wkr.Err_filter_mgr().Print();
		if (String_.Len_gt_0(err_filter_mgr)) usr_dlg.Warn_many("", "", err_filter_mgr);
		wiki.Appe().Log_mgr().Txn_end();
		tbl.Insert_end();
	}
	public void Wkr_exec(Xop_ctx ctx, byte[] src, Xop_lnki_tkn lnki, byte lnki_src_tid) {
		if (lnki.Ttl().ForceLiteralLink()) return; // ignore literal links which creat a link to file, but do not show the image; EX: [[:File:A.png|thumb|120px]] creates a link to File:A.png, regardless of other display-oriented args
		byte[] ttl = lnki.Ttl().Page_db();
		Xof_ext ext = Xof_ext_.new_by_ttl_(ttl);
		double lnki_time = lnki.Time();
		int lnki_page = lnki.Page();
		byte[] ttl_commons = Xto_commons(ns_file_is_case_match_all, commons_wiki, ttl);
		if (	Xof_lnki_page.Null_n(lnki_page) 				// page set
			&&	Xof_lnki_time.Null_n(lnki_time))			// thumbtime set
				usr_dlg.Warn_many("", "", "page and thumbtime both set; this may be an issue with fsdb: page=~{0} ttl=~{1}", ctx.Cur_page().Ttl().Page_db_as_str(), String_.new_u8(ttl));
		if (lnki.Ns_id() == Xow_ns_.Id_media)
			lnki_src_tid = Xob_lnki_src_tid.Tid_media;
		tbl.Insert_cmd_by_batch(ctx.Cur_page().Bldr__ns_ord(), ctx.Cur_page().Revision_data().Id(), ttl, ttl_commons, Byte_.By_int(ext.Id()), lnki.Lnki_type(), lnki_src_tid, lnki.W(), lnki.H(), lnki.Upright(), lnki_time, lnki_page);
	}
	@Override public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, Invk_wdata_enabled_))				wdata_enabled = m.ReadYn("v");
		else if	(ctx.Match(k, Invk_xtn_ref_enabled_))			xtn_ref_enabled = m.ReadYn("v");
		else if	(ctx.Match(k, Invk_gen_html_))					gen_html = m.ReadYn("v");
		else if	(ctx.Match(k, Invk_gen_hdump_))					gen_hdump = m.ReadYn("v");
		else if	(ctx.Match(k, Invk_hzip_enabled_))				hzip_enabled = m.ReadYn("v");
		else if	(ctx.Match(k, Invk_ns_ids_))					ns_ids = Int_.Ary_parse(m.ReadStr("v"), "|");
		else if	(ctx.Match(k, Invk_ns_ids_by_aliases))			ns_ids = Xob_lnki_temp_wkr_.Ns_ids_by_aliases(wiki, m.ReadStrAry("v", "|"));
		else if	(ctx.Match(k, Invk_property_wkr))				return this.Property_wkr();
		else if	(ctx.Match(k, Invk_invoke_wkr))					return this.Invoke_wkr();
		else	return super.Invk(ctx, ikey, k, m);
		return this;
	}
	private static final String Invk_wdata_enabled_ = "wdata_enabled_", Invk_xtn_ref_enabled_ = "xtn_ref_enabled_"
	, Invk_ns_ids_ = "ns_ids_", Invk_ns_ids_by_aliases = "ns_ids_by_aliases"
	, Invk_invoke_wkr = "invoke_wkr", Invk_property_wkr = "property_wkr"
	, Invk_gen_html_ = "gen_html_", Invk_gen_hdump_ = "gen_hdump_", Invk_hzip_enabled_ = "hzip_enabled_"
	;
	private Xop_log_invoke_wkr Invoke_wkr() {
		if (invoke_wkr == null) invoke_wkr = ((Scrib_xtn_mgr)bldr.App().Xtn_mgr().Get_or_fail(Scrib_xtn_mgr.XTN_KEY)).Invoke_wkr_or_new();
		return invoke_wkr;
	}
	private Xop_log_property_wkr Property_wkr() {
		if (property_wkr == null) property_wkr = bldr.App().Wiki_mgr().Wdata_mgr().Property_wkr_or_new();
		return property_wkr;
	}
	public static byte[] Xto_commons(boolean ns_file_is_case_match_all, Xowe_wiki commons_wiki, byte[] ttl_bry) {
		if (!ns_file_is_case_match_all) return null;	// return "" if wiki matches common
		Xoa_ttl ttl = Xoa_ttl.parse_(commons_wiki, Xow_ns_.Id_file, ttl_bry);
		byte[] rv = ttl.Page_db();
		return Bry_.Eq(rv, ttl_bry) ? null : rv;
	}
	public static boolean Ns_file_is_case_match_all(Xow_wiki wiki) {return wiki.Ns_mgr().Ns_file().Case_match() == Xow_ns_case_.Id_all;}
}
class Xob_lnki_temp_wkr_ {
	public static int[] Ns_ids_by_aliases(Xowe_wiki wiki, String[] aliases) {
		int[] rv = Xob_lnki_temp_wkr_.Ids_by_aliases(wiki.Ns_mgr(), aliases);
		int aliases_len = aliases.length;
		int ids_len = rv.length;
		for (int i = 0; i < aliases_len; i++) {
			String alias = aliases[i];
			int id = i < ids_len ? rv[i] : -1;
			wiki.Appe().Usr_dlg().Note_many("", "", "ns: ~{0} <- ~{1}", Int_.Xto_str_fmt(id, "0000"), alias);
		}
		if (aliases_len != ids_len) throw Err_.new_fmt_("mismatch in aliases and ids: {0} vs {1}", aliases_len, ids_len);
		return rv;
	}
	private static int[] Ids_by_aliases(Xow_ns_mgr ns_mgr, String[] aliases) {
		List_adp list = List_adp_.new_();
		int len = aliases.length;
		for (int i = 0; i < len; i++) {
			String alias = aliases[i];
			if (String_.Eq(alias, Xow_ns_.Key_main))
				list.Add(ns_mgr.Ns_main());
			else {
				Xow_ns ns = ns_mgr.Names_get_or_null(Bry_.new_u8(alias));
				if (ns != null)
					list.Add(ns);
			}
		}
		len = list.Count();
		int[] rv = new int[len];
		for (int i = 0; i < len; i++) {
			rv[i] = ((Xow_ns)list.Get_at(i)).Id();
		}
		return rv;
	}
}
