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
package gplx.xowa; import gplx.*;
import gplx.dbs.*; import gplx.xowa.apps.*; import gplx.xowa.files.exts.*;
public class Xoa_app_fxt {
	public static Xoae_app app_() {
		Io_mgr.I.InitEngine_mem();
		Db_conn_bldr.I.Reg_default_mem();
		return app_("linux", Io_url_.mem_dir_("mem/xowa/"));
	}
	public static Xoae_app app_(String op_sys, Io_url root_dir) {
		Io_url user_dir = root_dir.GenSubDir_nest("user", "test_user");
		Gfo_usr_dlg__log_base._.Log_dir_(user_dir.GenSubDir_nest("tmp", "current"));			
		Xoae_app app = new Xoae_app(Gfo_usr_dlg_.Test(), Xoa_app_type.Itm_cmd, root_dir, root_dir.GenSubDir("wiki"), root_dir.GenSubDir("file"), user_dir, root_dir.GenSubDir_nest("user", "anonymous", "wiki"), op_sys);
		app.Setup_mgr().Dump_mgr().Data_storage_format_(gplx.ios.Io_stream_.Tid_raw);	// TEST: set data_storage_format to file, else bldr tests will fails (expects plain text)
		GfsCore._.Clear();							// NOTE: must clear
		GfsCore._.AddCmd(app, Xoae_app.Invk_app);	// NOTE: must add app to GfsCore; app.Gfs_mgr() always adds current app to GfsCore; note this causes old test to leave behind GfsCore for new test
		GfsCore._.AddCmd(app, Xoae_app.Invk_xowa);	// add alias for app; DATE:2014-06-09
		return app;
	}
	public static Xowe_wiki wiki_tst_(Xoae_app app) {return wiki_(app, "en.wikipedia.org");}
	public static Xowe_wiki wiki_(Xoae_app app, String key) {return wiki_(app, key, app.Lang_mgr().Lang_en());}
	public static Xowe_wiki wiki_(Xoae_app app, String key, Xol_lang lang) {
		Io_url wiki_dir = app.Fsys_mgr().Wiki_dir().GenSubDir(key);
		Xowe_wiki rv = new Xowe_wiki(app, wiki_dir, Xow_ns_mgr_.default_(lang.Case_mgr()), lang);
		rv.File_mgr().Meta_mgr().Depth_(2);					// TEST: written for 2 depth
		rv.Props().Main_page_(Xoa_page_.Main_page_bry);		// TEST: default to Main Page (nothing tests loading Main Page from wiki.gfs)			
		rv.Ns_mgr().Ids_get_or_null(Xow_ns_.Id_main).Subpages_enabled_(true);
		app.Wiki_mgr().Add(rv);
		return rv;
	}
	public static void repo_(Xoae_app app, Xowe_wiki wiki) {
		app.File_mgr().Repo_mgr().Set("src:wiki", "mem/wiki/repo/src/", wiki.Domain_str()).Ext_rules_(Xof_rule_grp.Grp_app_default).Dir_depth_(2);
		app.File_mgr().Repo_mgr().Set("trg:wiki", "mem/wiki/repo/trg/", wiki.Domain_str()).Ext_rules_(Xof_rule_grp.Grp_app_default).Dir_depth_(2).Primary_(true);
		wiki.File_mgr().Repo_mgr().Add_repo(Bry_.new_u8("src:wiki"), Bry_.new_u8("trg:wiki"));
		app.File_mgr().Repo_mgr().Set("src:c", "mem/wiki/repo/src/", "commons.wikimedia.org").Ext_rules_(Xof_rule_grp.Grp_app_default).Dir_depth_(2);
		app.File_mgr().Repo_mgr().Set("trg:c", "mem/wiki/repo/trg/", "commons.wikimedia.org").Ext_rules_(Xof_rule_grp.Grp_app_default).Dir_depth_(2).Primary_(true);
		wiki.File_mgr().Repo_mgr().Add_repo(Bry_.new_u8("src:c"), Bry_.new_u8("trg:c"));
	}
	public static void Init_gui(Xoae_app app, Xowe_wiki wiki) {
		app.Gui_mgr().Browser_win().Init_by_kit(gplx.gfui.Mem_kit._);
		app.Gui_mgr().Browser_win().Tab_mgr().Tabs_new_init(wiki, Xoae_page.Empty);
	}
	public static Xob_bldr bldr_(Xoae_app app) {
		Xob_bldr rv = new Xob_bldr(app);
		rv.Sort_mem_len_(Io_mgr.Len_kb).Dump_fil_len_(Io_mgr.Len_kb).Make_fil_len_(Io_mgr.Len_kb);
		return rv;
	}
	public static final Io_url Root_dir = Op_sys.Cur().Tid_is_lnx() ? Io_url_.new_dir_("/xowa/") : Io_url_.new_dir_("C:\\xowa\\");
}
