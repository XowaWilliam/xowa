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
import org.junit.*;
public class Db_conn_info_tst {
	@Before public void setup() {
		regy.Add(Db_conn_info_mock._);
	}	private final Db_conn_info_pool regy = new Db_conn_info_pool();
	@Test  public void Parse() {
		tst_Parse("gplx_key=mock;id=1;", kv_("id", "1"));							 // one; gplx_key removed
		tst_Parse("gplx_key=mock;id=1;name=me;", kv_("id", "1"), kv_("name", "me")); // many
		tst_Parse("gplx_key=mock;id=1;name=me" , kv_("id", "1"), kv_("name", "me")); // no semi-colon at end
	}
	private KeyVal kv_(String key, Object val) {return KeyVal_.new_(key, val);}
	private void tst_Parse(String raw, KeyVal... expd) {
		Db_conn_info_mock mock = (Db_conn_info_mock)regy.Parse(raw);
		Tfds.Eq_ary_str(expd, mock.Kvs());
	}
}
class Db_conn_info_mock extends Db_conn_info__base {
	public KeyVal[] Kvs() {return kvs;} KeyVal[] kvs;
	@Override public String Tid() {return Tid_const;} public static final String Tid_const = "mock";
	@Override public Db_conn_info New_self(String raw, GfoMsg m) {
		Db_conn_info_mock rv = new Db_conn_info_mock();
		rv.kvs = new KeyVal[m.Args_count()];
		for (int i = 0; i < m.Args_count(); i++)
			rv.kvs[i] = m.Args_getAt(i);
		return rv;
	}
        public static final Db_conn_info_mock _ = new Db_conn_info_mock(); Db_conn_info_mock() {}
}
