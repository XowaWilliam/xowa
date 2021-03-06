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
package gplx.xowa; import gplx.*;
import gplx.dbs.*;
public class Xoa_test_ {
	public static boolean Db_skip()			{return Bool_.N;}
	public static boolean Db_is_mem_dflt()		{return Bool_.Y;}
	public static void Db_init(Io_url sqlite_url) {Db_init(Db_is_mem_dflt(), sqlite_url);}
	public static void Db_init(boolean db_is_mem, Io_url sqlite_url) {
		if (db_is_mem) {
			Io_mgr.I.InitEngine_mem();
			Db_conn_bldr.I.Reg_default_mem();
		}
		else {
			Io_mgr.I.DeleteDirDeep(sqlite_url);
			Db_conn_bldr.I.Reg_default_sqlite();
		}
	}
	public static Io_url Url_root()			{return Io_url_.Usr().GenSubDir_nest("xowa", "dev", "tst", "400_xowa");}
	public static Io_url Url_wiki_enwiki()	{return Url_root().GenSubDir_nest("root", "wiki", "en.wikipedia.org");}
	public static Io_url Url_file_enwiki()	{return Url_root().GenSubDir_nest("root", "file", "en.wikipedia.org");}
}
