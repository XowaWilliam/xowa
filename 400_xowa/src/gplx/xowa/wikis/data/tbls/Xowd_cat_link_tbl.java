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
import gplx.dbs.*; import gplx.dbs.qrys.*; import gplx.xowa.ctgs.*; 
public class Xowd_cat_link_tbl implements RlsAble {
	private final String tbl_name; private final Db_meta_fld_list flds = Db_meta_fld_list.new_();
	private final String fld_from, fld_to_id, fld_sortkey, fld_timestamp, fld_type_id;
	private final Db_conn conn; private Db_stmt stmt_insert, stmt_select_in;
	public Db_conn Conn() {return conn;}
	public Xowd_cat_link_tbl(Db_conn conn, boolean schema_is_1) {
		this.conn = conn;
		this.tbl_name = schema_is_1 ? "categorylinks" : "cat_link";
		fld_from			= flds.Add_int	("cl_from");
		fld_to_id			= flds.Add_int	("cl_to_id");
		fld_type_id			= flds.Add_byte	("cl_type_id");
		fld_sortkey			= flds.Add_str	("cl_sortkey", 230);
		fld_timestamp		= flds.Add_str	("cl_timestamp", 14);
		conn.Rls_reg(this);
	}
	public Xowd_cat_link_tbl Create_tbl() {conn.Ddl_create_tbl(Db_meta_tbl.new_(tbl_name, flds)); return this;}
	public void Create_idx() {
		conn.Ddl_create_idx(Xoa_app_.Usr_dlg()
		, Db_meta_idx.new_normal_by_tbl(tbl_name, "main", fld_to_id, fld_type_id, fld_sortkey, fld_from)
		, Db_meta_idx.new_normal_by_tbl(tbl_name, "from", fld_from)
		);
	}
	public void Insert_bgn() {conn.Txn_bgn(); stmt_insert = conn.Stmt_insert(tbl_name, flds);}
	public void Insert_end() {conn.Txn_end(); stmt_insert = Db_stmt_.Rls(stmt_insert);}
	public void Insert_cmd_by_batch(int page_id, int ctg_page_id, byte ctg_tid, byte[] sortkey, int timestamp) {
		stmt_insert.Clear()
			.Val_int(fld_from			, page_id)
			.Val_int(fld_to_id			, ctg_page_id)
			.Val_byte(fld_type_id		, ctg_tid)
			.Val_bry_as_str(fld_sortkey	, sortkey)
			.Val_int(fld_timestamp		, timestamp)
			.Exec_insert();
	}
	public void Delete_all() {conn.Stmt_delete(tbl_name, Db_meta_fld.Ary_empy).Exec_delete();}
	public int Select_by_type(List_adp list, int cat_page_id, byte arg_tid, byte[] arg_sortkey, boolean arg_is_from, int limit) {
		String arg_sortkey_str = arg_sortkey == null ? "" : String_.new_u8(arg_sortkey);
		gplx.core.criterias.Criteria comp_crt = !arg_is_from 
			? Db_crt_.mte_(fld_sortkey, arg_sortkey_str)		// from:  sortkey >= 'val'
			: Db_crt_.lte_(fld_sortkey, arg_sortkey_str);		// until: sortkey <= 'val'
		Db_qry__select_cmd qry = Db_qry_.select_().Cols_(fld_from, fld_sortkey).From_(tbl_name)
			.Where_(gplx.core.criterias.Criteria_.And_many(Db_crt_.eq_(fld_to_id, -1), Db_crt_.eq_(fld_type_id, arg_tid), comp_crt))
			.OrderBy_(fld_sortkey, !arg_is_from)
			.Limit_(limit + 1);									// + 1 to get last_plus_one for next page / previous page
		Db_rdr rdr = conn.Stmt_new(qry).Crt_int(fld_to_id, cat_page_id).Crt_byte(fld_type_id, arg_tid).Crt_str(fld_sortkey, arg_sortkey_str).Exec_select__rls_auto();
		int count = 0;
		try {
			while (rdr.Move_next()) {
				int itm_page_id = rdr.Read_int(fld_from);
				byte[] itm_sortkey = rdr.Read_bry_by_str(fld_sortkey);				
				Xowd_page_itm itm = new Xowd_page_itm().Id_(itm_page_id).Xtn_(new Xoctg_page_xtn(arg_tid, itm_sortkey));
				list.Add(itm);
				++count;
			}
		}	finally {rdr.Rls();}
		list.Sort_by(Xowd_page_itm_sorter.Ctg_tid_sortkey_asc);
		return count;
	}
	public void Select_in(List_adp rv, int cat_id) {
		if (stmt_select_in == null) stmt_select_in = conn.Stmt_select(tbl_name, flds, fld_to_id);
		Db_rdr rdr = stmt_select_in.Clear().Crt_int(fld_to_id, cat_id).Exec_select__rls_manual();
		try {
			while (rdr.Move_next())
				rv.Add(new Xowd_page_itm().Id_(rdr.Read_int(fld_from)));
		}	finally {rdr.Rls();}
	}
	public void Rls() {
		stmt_insert = Db_stmt_.Rls(stmt_insert);
		stmt_select_in = Db_stmt_.Rls(stmt_select_in);
	}
}
