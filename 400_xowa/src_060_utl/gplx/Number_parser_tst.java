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
package gplx;
import org.junit.*;
public class Number_parser_tst {
	private final Number_parser_fxt fxt = new Number_parser_fxt();
	@Before public void init() {fxt.Clear();}
	@Test  public void Integer() {
		fxt.Test_int("1", 1);
		fxt.Test_int("1234", 1234);
		fxt.Test_int("1234567890", 1234567890);
		fxt.Test_int("-1234", -1234);
		fxt.Test_int("+1", 1);
		fxt.Test_int("00001", 1);
	}
	@Test  public void Decimal() {
		fxt.Test_dec("1.23", DecimalAdp_.parse_("1.23"));
		fxt.Test_dec("1.023", DecimalAdp_.parse_("1.023"));
		fxt.Test_dec("-1.23", DecimalAdp_.parse_("-1.23"));
	}
	@Test  public void Double_long() {
		fxt.Test_dec(".42190046219457", DecimalAdp_.parse_(".42190046219457"));
	}
	@Test  public void Exponent() {
		fxt.Test_int("1E2", 100);
		fxt.Test_dec("1.234E2", DecimalAdp_.parse_("123.4"));
		fxt.Test_dec("1.234E-2", DecimalAdp_.parse_(".01234"));
		fxt.Test_dec("123.4E-2", DecimalAdp_.parse_("1.234"));
		fxt.Test_dec("+6.0E-3", DecimalAdp_.parse_(".006"));
	}
	@Test  public void Err() {
		fxt.Test_err("+", true);
		fxt.Test_err("-", true);
		fxt.Test_err("a", true);
		fxt.Test_err("1-2", false);
		fxt.Test_err("1..1", true);
		fxt.Test_err("1,,1", true);
		fxt.Test_err("1", false);
	}
	@Test  public void Hex() {
		fxt.Test_hex("0x1"	, 1);
		fxt.Test_hex("0xF"	, 15);
		fxt.Test_hex("0x20"	, 32);
		fxt.Test_hex("x20"	, 0, false);
		fxt.Test_hex("d"	, 0, false);	// PURPOSE: d was being converted to 13; no.w:Hovedbanen; DATE:2014-04-13
	}
	@Test  public void Ignore() {
		fxt.Init_ignore("\n\t");
		fxt.Test_int("1"	, 1);
		fxt.Test_int("1\n"	, 1);
		fxt.Test_int("1\t"	, 1);
		fxt.Test_int("1\n2"	, 12);
		fxt.Test_err("1\r"	, true);
	}
}
class Number_parser_fxt {
	private final Number_parser parser = new Number_parser();
	public void Clear() {parser.Clear();}
	public void Init_ignore(String chars) {parser.Ignore_chars_(Bry_.new_a7(chars));}
	public void Test_int(String raw, int expd) {
		byte[] raw_bry = Bry_.new_a7(raw);
		int actl = parser.Parse(raw_bry, 0, raw_bry.length).Rv_as_int(); 
		Tfds.Eq(expd, actl, raw);
	}
	public void Test_dec(String raw, DecimalAdp expd) {
		byte[] raw_bry = Bry_.new_a7(raw);
		DecimalAdp actl = parser.Parse(raw_bry, 0, raw_bry.length).Rv_as_dec(); 
		Tfds.Eq(expd.Xto_decimal(), actl.Xto_decimal(), raw);
	}
	public void Test_err(String raw, boolean expd) {
		byte[] raw_bry = Bry_.new_a7(raw);
		boolean actl = parser.Parse(raw_bry, 0, raw_bry.length).Has_err(); 
		Tfds.Eq(expd, actl, raw);
	}
	public void Test_hex(String raw, int expd_val) {Test_hex(raw, expd_val, true);}
	public void Test_hex(String raw, int expd_val, boolean expd_pass) {
		parser.Hex_enabled_(true);
		byte[] raw_bry = Bry_.new_a7(raw);
		int actl = parser.Parse(raw_bry, 0, raw_bry.length).Rv_as_int();
		if (expd_pass) {
			Tfds.Eq(expd_val, actl, raw);
			Tfds.Eq(true, !parser.Has_err());
		}
		else 
			Tfds.Eq(false, !parser.Has_err());
		parser.Hex_enabled_(false);
	}
}
