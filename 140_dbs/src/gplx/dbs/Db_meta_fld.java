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
public class Db_meta_fld {
	public Db_meta_fld(String name, int tid, int len, boolean nullable, boolean primary, boolean autonum, Object default_value) {
		this.name = name; this.tid = tid; this.len = len;
		this.nullable = nullable; this.primary = primary; this.autonum = autonum; this.default_value = default_value;
	}
	public int Tid() {return tid;} private final int tid;
	public String Name() {return name;} private final String name;
	public int Len() {return len;} private final int len;
	public boolean Nullable() {return nullable;} private final boolean nullable;
	public boolean Primary() {return primary;} private final boolean primary;
	public boolean Autonum() {return autonum;} private final boolean autonum;
	public Object Default_value() {return default_value;} private final Object default_value;
	public static final int Tid_bool = 0, Tid_byte = 1, Tid_short = 2, Tid_int = 3, Tid_long = 4, Tid_float = 5, Tid_double = 6, Tid_str = 7, Tid_text = 8, Tid_bry = 9, Tid_decimal = 10, Tid_date = 11;
	public static final String Key_null = null;
	public static final int Len_null = -1;
	public static final String[] Ary_empy = String_.Ary_empty;
	public static final Object Default_value_null = null;
}
