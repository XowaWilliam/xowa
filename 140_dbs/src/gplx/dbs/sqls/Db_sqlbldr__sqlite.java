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
package gplx.dbs.sqls; import gplx.*; import gplx.dbs.*;
interface Db_sqlbldr {}
public class Db_sqlbldr__sqlite implements Db_sqlbldr {
	private final Bry_bfr tmp_bfr = Bry_bfr.reset_(1024);
	public String Bld_create_idx(Db_meta_idx idx) {
		tmp_bfr.Add_str_a7("CREATE ");
		if (idx.Unique())
			tmp_bfr.Add_str_a7("UNIQUE ");
		tmp_bfr.Add_str_a7("INDEX ");
		tmp_bfr.Add_str_a7("IF NOT EXISTS ");
		tmp_bfr.Add_str_a7(idx.Name());
		tmp_bfr.Add_str_a7(" ON ");
		tmp_bfr.Add_str_a7(idx.Tbl());
		tmp_bfr.Add_str_a7(" (");
		String[] flds = idx.Flds();
		int flds_len = flds.length;
		for (int i = 0; i < flds_len; ++i) {
			String fld = flds[i];
			if (i != 0) tmp_bfr.Add_str_a7(", ");
			tmp_bfr.Add_str_a7(fld);
		}
		tmp_bfr.Add_str_a7(");");
		return tmp_bfr.Xto_str_and_clear();
	}
	public String Bld_create_tbl(Db_meta_tbl tbl) {
		tmp_bfr.Add_str_a7("CREATE TABLE IF NOT EXISTS ").Add_str_a7(tbl.Name()).Add_byte_nl();
		Db_meta_fld[] flds = tbl.Flds();
		int flds_len = flds.length;
		for (int i = 0; i < flds_len; ++i) {
			Db_meta_fld fld = flds[i];
			tmp_bfr.Add_byte(i == 0 ? Byte_ascii.Paren_bgn : Byte_ascii.Comma).Add_byte_space();
			Bld_fld(tmp_bfr, fld);
			tmp_bfr.Add_byte_nl();
		}
		tmp_bfr.Add_str_a7(");");
		return tmp_bfr.Xto_str_and_clear();
	}
	public String Bld_alter_tbl_add(String tbl, Db_meta_fld fld) {
		tmp_bfr.Add_str_a7("ALTER TABLE ").Add_str_a7(tbl).Add_str_a7(" ADD ");
		Bld_fld(tmp_bfr, fld);
		tmp_bfr.Add_byte_semic();
		return tmp_bfr.Xto_str_and_clear();
	}
	public String Bld_drop_tbl(String tbl) {
		return String_.Format("DROP TABLE IF EXISTS {0};", tbl);
	}
	private void Bld_fld(Bry_bfr tmp_bfr, Db_meta_fld fld) {
		tmp_bfr.Add_str_a7(fld.Name()).Add_byte_space();
		Tid_to_sql(tmp_bfr, fld.Tid(), fld.Len()); tmp_bfr.Add_byte_space();
		tmp_bfr.Add_str_a7(fld.Nullable() ? "NULL " : "NOT NULL ");
		if (fld.Default_value() != Db_meta_fld.Default_value_null) {
			tmp_bfr.Add_str_a7("DEFAULT ");
			boolean quote = Bool_.N;
			switch (fld.Tid()) {
				case Db_meta_fld.Tid_str: case Db_meta_fld.Tid_text: quote = Bool_.Y; break;
			}
			if (quote) tmp_bfr.Add_byte_apos();
			tmp_bfr.Add_str_u8(Object_.Xto_str_strict_or_null(fld.Default_value()));
			if (quote) tmp_bfr.Add_byte_apos();
			tmp_bfr.Add_byte_space();
		}
		if (fld.Primary()) tmp_bfr.Add_str_a7("PRIMARY KEY ");
		if (fld.Autonum()) tmp_bfr.Add_str_a7("AUTOINCREMENT ");
		tmp_bfr.Del_by_1();	// remove trailing space
	}
	public static void Tid_to_sql(Bry_bfr tmp_bfr, int tid, int len) {// REF: https://www.sqlite.org/datatype3.html
		switch (tid) {
			case Db_meta_fld.Tid_bool:		tmp_bfr.Add_str_a7("boolean"); break;
			case Db_meta_fld.Tid_byte:		tmp_bfr.Add_str_a7("tinyint"); break;
			case Db_meta_fld.Tid_short:		tmp_bfr.Add_str_a7("smallint"); break;
			case Db_meta_fld.Tid_int:		tmp_bfr.Add_str_a7("integer"); break;	// NOTE: must be integer, not int, else "int PRIMARY KEY AUTONUMBER" will fail; DATE:2015-02-12
			case Db_meta_fld.Tid_long:		tmp_bfr.Add_str_a7("bigint"); break;
			case Db_meta_fld.Tid_float:		tmp_bfr.Add_str_a7("float"); break;
			case Db_meta_fld.Tid_double:	tmp_bfr.Add_str_a7("double"); break;
			case Db_meta_fld.Tid_str:		tmp_bfr.Add_str_a7("varchar(").Add_int_variable(len).Add_byte(Byte_ascii.Paren_end); break;
			case Db_meta_fld.Tid_text:		tmp_bfr.Add_str_a7("text"); break;
			case Db_meta_fld.Tid_bry:		tmp_bfr.Add_str_a7("blob"); break;
			default:						throw Err_.unhandled(tid);
		}
	}
        public static final Db_sqlbldr__sqlite I = new Db_sqlbldr__sqlite(); Db_sqlbldr__sqlite() {}
}
