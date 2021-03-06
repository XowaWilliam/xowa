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
package gplx.dbs; import gplx.*;
public class Db_rdr_ {
	public static final Db_rdr Empty = new Db_rdr__empty();
}
class Db_rdr__empty implements Db_rdr {
	public boolean			Move_next()						{return false;}
	public byte[]		Read_bry(String k)				{return Bry_.Empty;}
	public byte[]		Read_bry_by_str(String k)		{return Bry_.Empty;}
	public byte[]		Read_bry_in_parts(String tbl, String fld, String crt_key, Object crt_val) {return Bry_.Empty;}
	public byte			Read_byte(String k)				{return Byte_.Max_value_127;}
	public String 		Read_str(String k)				{return String_.Empty;}
	public DateAdp		Read_date_by_str(String k)		{return DateAdp_.MinValue;}
	public int 			Read_int(String k)				{return Int_.MinValue;}
	public long 		Read_long(String k)				{return Long_.MinValue;}
	public float		Read_float(String k)			{return Float_.NaN;}
	public double		Read_double(String k)			{return Double_.NaN;}
	public boolean			Read_bool_by_byte(String k)		{return false;}
	public void			Rls() {}
}
