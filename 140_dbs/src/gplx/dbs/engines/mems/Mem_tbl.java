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
package gplx.dbs.engines.mems; import gplx.*; import gplx.dbs.*; import gplx.dbs.engines.*;
import gplx.core.primitives.*; import gplx.core.criterias.*; import gplx.dbs.qrys.*;
public class Mem_tbl {
	private final List_adp rows = List_adp_.new_(); private final List_adp where_rows = List_adp_.new_();
	private final Hash_adp autonum_hash = Hash_adp_.new_();		
	public Mem_tbl(Db_meta_tbl meta) {this.meta = meta;}
	public Db_meta_tbl Meta() {return meta;} private final Db_meta_tbl meta;
	public int Insert(Db_stmt__mem stmt) {
		Mem_itm itm = new Mem_itm();
		Db_meta_fld[] flds = meta.Flds();
		int len = flds.length;
		for (int i = 0; i < len; ++i) {
			Db_meta_fld fld = flds[i];
			String fld_name = fld.Name();
			Object val = fld.Autonum() ? Autonum_calc(fld_name) : stmt.Args_get_by(fld_name);
			itm.Set_by(fld_name, val);
		}
		rows.Add(itm);
		return 1;
	}
	private int Autonum_calc(String name) {
		Int_obj_ref autonum_itm = (Int_obj_ref)autonum_hash.Get_by(name);
		if (autonum_itm == null) {
			autonum_itm = Int_obj_ref.new_(0);
			autonum_hash.Add(name, autonum_itm);
		}
		return autonum_itm.Val_add();
	}
	public int Update(Db_stmt__mem stmt) {
		Db_qry_update qry = (Db_qry_update)stmt.Qry();
		qry.Where().Val_from_args(stmt.Crts());
		Select_rows_where(where_rows, stmt, qry.Where());
		int where_rows_len = where_rows.Count();
		String[] update_cols = qry.Cols_for_update(); int update_cols_len = update_cols.length;
		for (int i = 0; i < where_rows_len; ++i) {
			Mem_itm itm = (Mem_itm)where_rows.Get_at(i);
			for (int j = 0; j < update_cols_len; ++j)
				itm.Set_by(update_cols[j], stmt.Args_get_at(j));
		}
		return where_rows_len;
	}
	public int Delete(Db_stmt__mem stmt) {
		Db_qry_delete qry = (Db_qry_delete)stmt.Qry();
		qry.Where().Val_from_args(stmt.Crts());
		Select_rows_where(where_rows, stmt, qry.Where());
		int where_rows_len = where_rows.Count();
		for (int i = 0; i < where_rows_len; ++i) {
			Mem_itm itm = (Mem_itm)where_rows.Get_at(i);
			rows.Del(itm);
		}
		return where_rows_len;
	}
	public Db_rdr Select(Db_stmt__mem stmt) {
		String[] select = null; Criteria where = null;
		Db_qry__select_in_tbl qry = Db_qry__select_in_tbl.as_(stmt.Qry());
		if (qry == null) {
			Db_qry__select_cmd qry2 = (Db_qry__select_cmd)stmt.Qry();
			select = qry2.Cols_ary();
			where = qry2.Where();
		}
		else {
			select = qry.Select_flds();
			where = qry.Where();
		}
		where.Val_from_args(stmt.Crts());
		Select_rows_where(where_rows, stmt, where);
		return new Db_rdr__mem(select, (Mem_itm[])where_rows.To_ary_and_clear(Mem_itm.class));
	}
	private void Select_rows_where(List_adp rv, Db_stmt__mem stmt, Criteria crt) {
		rv.Clear();
		int rows_len = rows.Count();
		for (int i = 0; i < rows_len; ++i) {
			Mem_itm itm = (Mem_itm)rows.Get_at(i);
			if (crt.Matches(itm)) 
				rv.Add(itm);
		}
	}
}
