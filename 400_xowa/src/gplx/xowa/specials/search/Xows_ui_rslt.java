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
package gplx.xowa.specials.search; import gplx.*; import gplx.xowa.*; import gplx.xowa.specials.*;
class Xows_ui_rslt {
	private final List_adp rslt_list = List_adp_.new_();
	public int			Len()					{return rslt_list.Count();}
	public void			Add(Xows_db_row rslt)	{rslt_list.Add(rslt);}
	public Xows_db_row	Get_at(int i)			{return (Xows_db_row)rslt_list.Get_at(i);}
}
