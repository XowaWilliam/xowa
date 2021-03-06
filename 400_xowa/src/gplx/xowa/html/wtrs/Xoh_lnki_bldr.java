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
package gplx.xowa.html.wtrs; import gplx.*; import gplx.xowa.*; import gplx.xowa.html.*;
import gplx.html.*;
public class Xoh_lnki_bldr {
	private final Xoa_app app; private final Xoh_href_parser href_parser; private final byte[] img_root_dir;
	private final Bry_bfr tmp_bfr = Bry_bfr.reset_(255);
	private byte[] href, title, id, caption;
	private byte[] img_rel_path; private int img_w, img_h; private boolean img_pos_is_left;
	public Xoh_lnki_bldr(Xoa_app app, Xoh_href_parser href_parser) {
		this.app = app; this.href_parser = href_parser;
		this.img_root_dir = app.Fsys_mgr().Root_dir().GenSubDir_nest("user", "anonymous", "app", "img").To_http_file_bry();
	}
	public Xoh_lnki_bldr Clear() {
		href = title = id = caption = null;
		img_rel_path = null; img_w = 0; img_h = 0;
		img_pos_is_left = true;
		return this;
	}
	public Xoh_lnki_bldr Id_(byte[] v) {this.id = Html_utl.Escape_for_atr_val_as_bry(tmp_bfr, Byte_ascii.Apos, v); return this;}
	public Xoh_lnki_bldr Href_(Xow_wiki wiki, byte[] bry) {return Href_(wiki.Domain_bry(), wiki.Ttl_parse(bry));}
	public Xoh_lnki_bldr Href_(byte[] domain_bry, Xoa_ttl ttl) {
		href_parser.Build_to_bfr(tmp_bfr, app, domain_bry, ttl, Bool_.Y);
		this.href = tmp_bfr.Xto_bry_and_clear();
		return this;
	}
	public Xoh_lnki_bldr Title_(byte[] title) {
		this.title = Html_utl.Escape_for_atr_val_as_bry(tmp_bfr, Byte_ascii.Apos, title);
		return this;
	}
	public Xoh_lnki_bldr Img_pos_is_left_(boolean v) {this.img_pos_is_left = v; return this;}
	public Xoh_lnki_bldr Img_16x16(byte[] rel_path) {return Img_(rel_path, 16, 16);}
	private Xoh_lnki_bldr Img_(byte[] rel_path, int w, int h) {
		this.img_rel_path = rel_path;
		this.img_w = w;
		this.img_h = h;
		return this;
	}
	public Xoh_lnki_bldr Caption_(byte[] text) {
		this.caption = Html_utl.Escape_html_as_bry(tmp_bfr, text, Bool_.Y, Bool_.Y, Bool_.Y, Bool_.Y, Bool_.Y);
		return this;
	}
	public byte[] Bld_to_bry() {
		Bld(tmp_bfr);
		byte[] rv = tmp_bfr.Xto_bry_and_clear();
		this.Clear();
		return rv;
	}
	public void Bld(Bry_bfr bfr) {
		bfr.Add_str_a7("<a href='").Add(href);
		if (title != null)
			bfr.Add_str_a7("' title='").Add(title);
		if (id != null)
			bfr.Add_str_a7("' id='").Add(id);
		bfr.Add_str_a7("'>");
		if ( img_pos_is_left && img_rel_path != null)
			Bld_img(bfr);
		if (caption != null)
			bfr.Add(caption);
		if (!img_pos_is_left && img_rel_path != null)
			Bld_img(bfr);
		bfr.Add_str_a7("</a>");
	}
	private void Bld_img(Bry_bfr bfr) {
		bfr.Add_str_a7("<img src='").Add(img_root_dir).Add(img_rel_path).Add_str_a7("' width='").Add_int_variable(img_w).Add_str_a7("' height='").Add_int_variable(img_h).Add_str_a7("'/>");
	}
}
