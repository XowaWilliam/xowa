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
package gplx.fsdb.meta; import gplx.*; import gplx.fsdb.*;
import gplx.dbs.*; import gplx.dbs.cfgs.*; import gplx.fsdb.meta.*;
public class Fsm_cfg_mgr {		
	private final Db_cfg_tbl tbl; private final Hash_adp grp_hash = Hash_adp_.new_();
	public Fsm_cfg_mgr(Fsdb_db_mgr db_conn_mgr, Db_conn conn) {
		this.tbl = new Db_cfg_tbl(conn, db_conn_mgr.File__cfg_tbl_name());
	}
	public void Ctor_by_load() {
		Db_cfg_hash hash		= Grps_get_or_load(Grp_core);
		this.next_id			= hash.Get(Key_next_id).To_int_or(-1); if (next_id == -1) throw Err_.new_("next_id not found in cfg; url={0}", tbl.Conn().Conn_info().Xto_api());
		this.schema_thm_page	= hash.Get(Key_schema_thm_page).To_yn_or_n();
		this.patch__next_id		= hash.Get(Key_patch__next_id).To_yn_or_n();
		this.patch__page_gt_1	= hash.Get(Key_patch__page_gt_1).To_yn_or_n();
	}
	public Db_cfg_tbl				Tbl() {return tbl;}
	public int Next_id()			{return next_id++;} private int next_id = 1;
	public void Next_id_commit()	{tbl.Update_int("core", "next_id", next_id);}
	public boolean Schema_thm_page()	{return schema_thm_page;} private boolean schema_thm_page = true;
	public boolean Patch_next_id()		{return patch__next_id;} private boolean patch__next_id = true;
	public void Patch_next_id_exec(int last_id) {
		if (last_id >= next_id)
			next_id = last_id + 1;
		tbl.Insert_yn(Grp_core, Key_patch__next_id, Bool_.Y);
	}
	public boolean Patch__page_gt_1() {return patch__page_gt_1;} private boolean patch__page_gt_1 = false;
	public void Patch__save(String cfg_key) {tbl.Insert_yn(Fsm_cfg_mgr.Grp_core, cfg_key, Bool_.Y);}
	public Db_cfg_hash Grps_get_or_load(String grp_key) {
		Db_cfg_hash rv = (Db_cfg_hash)grp_hash.Get_by(grp_key);
		if (rv == null) {
			rv = tbl.Select_as_hash(grp_key);
			grp_hash.Add(grp_key, rv);
		}
		return rv;
	}
	public static final String Grp_core = "core";
	public static final String Key_next_id = "next_id", Key_schema_thm_page = "schema.thm.page", Key_patch__next_id = "patch.next_id", Key_patch__page_gt_1 = "patch.page_gt_1";
}
