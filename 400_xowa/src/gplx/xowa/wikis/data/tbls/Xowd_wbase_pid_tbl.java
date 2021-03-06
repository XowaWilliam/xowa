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
package gplx.xowa.wikis.data.tbls; import gplx.*; import gplx.xowa.*; import gplx.xowa.wikis.*; import gplx.xowa.wikis.data.*;
import gplx.dbs.*; import gplx.xowa.xtns.wdatas.*;
public class Xowd_wbase_pid_tbl implements RlsAble {
	private final String tbl_name; private final Db_meta_fld_list flds = Db_meta_fld_list.new_();
	private final String fld_src_lang, fld_src_ttl, fld_trg_ttl;
	private final Db_conn conn; private Db_stmt stmt_select, stmt_insert;
	public Xowd_wbase_pid_tbl(Db_conn conn, boolean schema_is_1) {
		this.conn = conn;
		String fld_prefix = "";
		if (schema_is_1)	{tbl_name = "wdata_pids"; fld_prefix = "wp_";}
		else				{tbl_name = "wbase_pid";}
		fld_src_lang		= flds.Add_str(fld_prefix + "src_lang", 255);
		fld_src_ttl			= flds.Add_str(fld_prefix + "src_ttl", 512);
		fld_trg_ttl			= flds.Add_str(fld_prefix + "trg_ttl", 512);
		conn.Rls_reg(this);
	}
	public void Create_tbl() {conn.Ddl_create_tbl(Db_meta_tbl.new_(tbl_name, flds));}
	public void Create_idx() {conn.Ddl_create_idx(Xoa_app_.Usr_dlg(), Db_meta_idx.new_normal_by_tbl(tbl_name, "src", fld_src_lang, fld_src_ttl));}
	public void Insert_bgn() {conn.Txn_bgn(); stmt_insert = conn.Stmt_insert(tbl_name, flds);}
	public void Insert_end() {conn.Txn_end(); stmt_insert = Db_stmt_.Rls(stmt_insert);}
	public void Insert_cmd_by_batch(byte[] src_lang, byte[] src_ttl, byte[] trg_ttl) {
		stmt_insert.Clear()
			.Val_bry_as_str(fld_src_lang, src_lang).Val_bry_as_str(fld_src_ttl, src_ttl).Val_bry_as_str(fld_trg_ttl, trg_ttl)
			.Exec_insert();
	}
	public int Select_pid(byte[] src_lang, byte[] src_ttl) {
		if (stmt_select == null) stmt_select = conn.Stmt_select(tbl_name, flds, fld_src_lang, fld_src_ttl);
		Db_rdr rdr = stmt_select.Clear()
				.Crt_bry_as_str(fld_src_lang, src_lang).Crt_bry_as_str(fld_src_ttl, src_ttl)
				.Exec_select__rls_manual();
		try {
			if (!rdr.Move_next()) return Wdata_wiki_mgr.Pid_null;	// occurs when pid exists, but does not have entry for language; see hu.w:Marco Polo argali; DATE: 2014-02-01
			byte[] pid_bry = rdr.Read_bry_by_str(fld_trg_ttl);
			return pid_bry == null ?  Wdata_wiki_mgr.Pid_null : Bry_.Xto_int_or(pid_bry, 1, pid_bry.length, Wdata_wiki_mgr.Pid_null);
		} finally {rdr.Rls();}
	}
	public void Rls() {
		stmt_insert = Db_stmt_.Rls(stmt_insert);
		stmt_select = Db_stmt_.Rls(stmt_select);
	}
}
