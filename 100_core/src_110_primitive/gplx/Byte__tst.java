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
public class Byte__tst {
	@Test  public void int_() {
		tst_int_(   0,    0);
		tst_int_( 127,  127);
		tst_int_( 128,  128);	// NOTE: JAVA defines byte as -128 -> 127
		tst_int_( 255,  255);
		tst_int_( 256,    0);	// NOTE: 256 will cast to 1; (byte)256 works same in both JAVA/.NET
	}	void tst_int_(int v, int expd) {Tfds.Eq((byte)expd, Byte_.By_int(v));}	// WORKAROUND/JAVA: expd is of type int b/c java promotes numbers to ints
	@Test  public void XtoInt() {
		tst_XtoInt(   0,    0);
		tst_XtoInt( 127,  127);
		tst_XtoInt( 128,  128);
		tst_XtoInt( 255,  255);
		tst_XtoInt( 256,    0);
	}	void tst_XtoInt(int v, int expd) {Tfds.Eq(expd, Byte_.Xto_int((byte)v));}	// WORKAROUND/JAVA: v is of type int b/c java promotes numbers to ints
}
