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
package gplx.xowa.users.data; import gplx.*; import gplx.xowa.*; import gplx.xowa.users.*;
import gplx.xowa.specials.*;
public class Xoud_history_special implements Bry_fmtr_arg, Xows_page {
	private List_adp rows = List_adp_.new_();
	public Xows_special_meta Special_meta() {return Xows_special_meta_.Itm__page_history;}
	public void Special_gen(Xowe_wiki wiki, Xoae_page page, Xoa_url url, Xoa_ttl ttl) {
		Xoae_app app = wiki.Appe();
		Xoud_history_mgr mgr = app.User().User_db_mgr().History_mgr();
		mgr.Select(rows, 100);
		Bry_bfr bfr = app.Utl__bfr_mkr().Get_m001(); 
		html_grp.Bld_bfr_many(bfr, this);
		page.Data_raw_(bfr.To_bry_and_rls());
	}
	public void XferAry(Bry_bfr bfr, int idx) {
		int len = rows.Count();
		for (int i = 0; i < len; i++) {
			Xoud_history_row row = (Xoud_history_row)rows.Get_at(i);
			html_itm.Bld_bfr_many(bfr, row.History_wiki(), row.History_page(), row.History_count(), row.History_time().XtoStr_fmt_yyyy_MM_dd_HH_mm());
		}
	}
	private static Bry_fmtr html_grp = Bry_fmtr.new_(String_.Concat_lines_nl_skip_last
	( "<table class='sortable'>"
	, "  <tr>"
	, "    <th>page</th>"
	, "    <th>wiki</th>"
	, "    <th>views</th>"
	, "    <th>time</th>"
	, "  </tr>~{itms}"
	, "</table>"
	), "itms"
	);
	private static Bry_fmtr html_itm = Bry_fmtr.new_(String_.Concat_lines_nl_skip_last
	( ""
	, "  <tr>"
	, "    <td>[[~{itm_wiki}:~{itm_page}|~{itm_page}]]</td>"
	, "    <td>~{itm_wiki}</td>"
	, "    <td>~{itm_count}</td>"
	, "    <td>~{itm_last}</td>"
	, "  </tr>"
	), "itm_wiki", "itm_page", "itm_count", "itm_last"
	);
}
