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
import gplx.ios.*; import gplx.dbs.*; import gplx.fsdb.data.*;
public class Fsm_mnt_itm {
	public Fsm_mnt_itm(int id, String name, String url_rel) {this.id = id; this.name = name; this.url_rel = url_rel;}
	public int				Id() {return id;} private final int id;
	public String			Name() {return name;} private final String name;
	public String			Url_rel() {return url_rel;} private final String url_rel;
	public Fsm_atr_mgr		Atr_mgr() {return atr_mgr;} private Fsm_atr_mgr atr_mgr;
	public Fsm_bin_mgr		Bin_mgr() {return bin_mgr;} private Fsm_bin_mgr bin_mgr;
	public Fsm_cfg_mgr		Cfg_mgr() {return cfg_mgr;} private Fsm_cfg_mgr cfg_mgr;
	public Fsdb_db_mgr		Db_mgr() {return db_mgr;} private Fsdb_db_mgr db_mgr;
	public void Ctor_by_load(Fsdb_db_mgr db_mgr) {
		this.db_mgr = db_mgr;
		Db_conn conn = db_mgr.File__abc_file__at(id).Conn();
		cfg_mgr = new Fsm_cfg_mgr(db_mgr, conn);
		atr_mgr = new Fsm_atr_mgr(db_mgr, conn, this);
		bin_mgr = new Fsm_bin_mgr(db_mgr, conn, id);
		cfg_mgr.Ctor_by_load();
		atr_mgr.Ctor_by_load(cfg_mgr.Schema_thm_page());
		bin_mgr.Ctor_by_load();
		if (!cfg_mgr.Patch_next_id()) Fsm_mnt_itm_.Patch_next_id(this, name);
	}
	public int Next_id() {return cfg_mgr.Next_id();}
	public Fsd_fil_itm		Select_fil_or_null(byte[] dir, byte[] fil) {return atr_mgr.Select_fil_or_null(dir, fil);}
	public boolean				Select_thm(boolean exact, Fsd_thm_itm rv, byte[] dir, byte[] fil) {
		Fsd_fil_itm fil_itm = atr_mgr.Select_fil_or_null(dir, fil);
		return fil_itm == Fsd_fil_itm.Null ? Bool_.N : atr_mgr.Select_thm(exact, rv, fil_itm.Dir_id(), fil_itm.Fil_id());
	}
	public void				Insert_img(Fsd_img_itm rv, Fsm_atr_fil atr_fil, Fsm_bin_fil bin_fil, byte[] dir, byte[] fil, int ext_id, int img_w, int img_h, long bin_len, Io_stream_rdr bin_rdr) {
		int fil_id = atr_fil.Insert_img(rv, dir, fil, ext_id, img_w, img_h, bin_fil.Id(), bin_len, bin_rdr);
		bin_fil.Insert(fil_id, Fsd_bin_tbl.Owner_tid_fil, bin_len, bin_rdr);
	}
	public void				Insert_fil(Fsd_fil_itm rv, Fsm_atr_fil atr_fil, Fsm_bin_fil bin_fil, byte[] dir, byte[] fil, int ext_id, long bin_len, gplx.ios.Io_stream_rdr bin_rdr) {
		int fil_id = atr_fil.Insert_fil(rv, dir, fil, ext_id, bin_fil.Id(), bin_len, bin_rdr);
		bin_fil.Insert(fil_id, Fsd_bin_tbl.Owner_tid_fil, bin_len, bin_rdr);
	}
	public void				Insert_thm(Fsd_thm_itm rv, Fsm_atr_fil atr_fil, Fsm_bin_fil bin_fil, byte[] dir, byte[] fil, int ext_id, int w, int h, double time, int page, long bin_len, Io_stream_rdr bin_rdr) {
		int thm_id = atr_fil.Insert_thm(rv, dir, fil, ext_id, w, h, time, page, bin_fil.Id(), bin_len, bin_rdr);
		bin_fil.Insert(thm_id, Fsd_bin_tbl.Owner_tid_thm, bin_len, bin_rdr);
	}
	public void Txn_bgn() {atr_mgr.Txn_bgn(); bin_mgr.Txn_bgn();}
	public void Txn_end() {atr_mgr.Txn_end(); bin_mgr.Txn_end();}
}
class Fsm_mnt_itm_ {
	public static void Patch_next_id(Fsm_mnt_itm abc_mgr, String name) {
		if (!String_.Eq(name, "fsdb.user")) return;
		Fsm_atr_mgr atr_mgr = abc_mgr.Atr_mgr();
		Fsm_cfg_mgr cfg_mgr = abc_mgr.Cfg_mgr();
		int last_id = -1;
		Fsm_atr_fil atr_fil = atr_mgr.Db__core();
		int max_fil_id = Db_conn_.Select_fld0_as_int_or(atr_fil.Conn(), "SELECT Max(fil_id) AS MaxId FROM fsdb_fil;", -1);
		int max_thm_id = Db_conn_.Select_fld0_as_int_or(atr_fil.Conn(), "SELECT Max(thm_id) AS MaxId FROM fsdb_xtn_thm;", -1);
		last_id = max_fil_id > max_thm_id ? max_fil_id : max_thm_id;
		cfg_mgr.Patch_next_id_exec(last_id);
	}
}
