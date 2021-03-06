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
package gplx.xowa.bldrs.cmds.wikis; import gplx.*; import gplx.xowa.*; import gplx.xowa.bldrs.*; import gplx.xowa.bldrs.cmds.*;
import gplx.dbs.*;
class Xobd_page_dump_tbl {
	public final static String Tbl_name = "page_dump";
	private final String fld_id, fld_title, fld_namespace, fld_is_redirect;
	private final Db_conn conn; private final Db_meta_fld_list flds = Db_meta_fld_list.new_();
	public Xobd_page_dump_tbl(Db_conn conn) {
		this.conn = conn;
		this.fld_id				= flds.Add_int_pkey("page_id");
		this.fld_title			= flds.Add_str("page_title", 255);
		this.fld_namespace		= flds.Add_int("page_namespace");
		this.fld_is_redirect	= flds.Add_int("page_is_redirect");
	}
	public void Create_data(Io_url page_db_url, int text_db_id) {
		conn.Ddl_create_tbl(Db_meta_tbl.new_(Tbl_name, flds));
		Db_attach_cmd.new_(conn, "page_db", page_db_url)
			.Add_fmt("text_db_prep.clone_page", Sql_insert_data, text_db_id)
			.Exec();
		conn.Ddl_create_idx(Db_meta_idx.new_unique_by_tbl(Tbl_name, "main", fld_id, fld_namespace, fld_is_redirect, fld_title));
	}
	private static final String Sql_insert_data = String_.Concat_lines_nl
	( "INSERT INTO page_dump (page_id, page_title, page_namespace, page_is_redirect)"
	, "SELECT  p.page_id"
	, ",       p.page_title"
	, ",       p.page_namespace"
	, ",       p.page_is_redirect"
	, "FROM    <attach_db>page p"
	, "WHERE   p.page_text_db_id = {0};"
	);
}
