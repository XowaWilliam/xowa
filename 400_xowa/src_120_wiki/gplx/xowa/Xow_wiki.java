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
import gplx.core.primitives.*;	
import gplx.xowa.wikis.*; import gplx.xowa.wikis.ttls.*; import gplx.xowa.wikis.data.*; import gplx.xowa.wikis.xwikis.*;
import gplx.xowa.files.*; import gplx.xowa.files.repos.*; import gplx.xowa.files.origs.*; import gplx.xowa.files.bins.*; import gplx.fsdb.meta.*; import gplx.fsdb.*;
import gplx.xowa.html.*; import gplx.xowa.html.wtrs.*; import gplx.xowa.html.hzips.*; import gplx.xowa.html.hdumps.*;
public interface Xow_wiki extends Xow_ttl_parser {
	boolean						Type_is_edit();
	Xoa_app						App();
	Xol_lang					Lang();
	byte[]						Domain_bry();			// EX: en.wikipedia.org
	String						Domain_str();
	int							Domain_tid();			// Xow_domain_type_.Tid_wikipedia
	byte[]						Domain_abrv();			// enwiki
	Xow_domain					Domain_itm();
	Xow_fsys_mgr				Fsys_mgr();
	Xowd_db_mgr					Data__core_mgr();
	Xof_fsdb_mode				File__fsdb_mode();
	Fsdb_db_mgr					File__fsdb_core();
	Xow_repo_mgr				File__repo_mgr();
	Xof_orig_mgr				File__orig_mgr();
	Xof_bin_mgr					File__bin_mgr();
	Fsm_mnt_mgr					File__mnt_mgr();
	boolean						Html__hdump_enabled();
	Xow_hzip_mgr				Html__hzip_mgr();
	Xohd_hdump_rdr				Html__hdump_rdr();
	Xoh_page_wtr_mgr_base		Html__page_wtr_mgr();
	Xow_xwiki_mgr				Xwiki_mgr();
	void						Init_by_wiki();
}
