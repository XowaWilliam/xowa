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
package gplx.xowa.xtns.poems; import gplx.*; import gplx.xowa.*; import gplx.xowa.xtns.*;
import gplx.html.*; import gplx.xowa.html.*;
public class Poem_nde implements Xox_xnde {
	private Xop_root_tkn xtn_root;
	public void Xtn_parse(Xowe_wiki wiki, Xop_ctx ctx, Xop_root_tkn root, byte[] src, Xop_xnde_tkn xnde) {// REF.MW: Poem.php|wfRenderPoemTag
		int itm_bgn = xnde.Tag_open_end(), itm_end = xnde.Tag_close_bgn();
		if (itm_bgn == src.length)	return;  // NOTE: handle inline where there is no content to parse; EX: <poem/>
		if (itm_bgn >= itm_end)		return;  // NOTE: handle inline where there is no content to parse; EX: a<poem/>b
		if (src[itm_bgn] 		== Byte_ascii.NewLine)	++itm_bgn;	// ignore 1st \n; 
		if (src[itm_end - 1] 	== Byte_ascii.NewLine				// ignore last \n; 
			&& itm_end != itm_bgn)						--itm_end;	// ...if not same as 1st \n; EX: <poem>\n</poem>
		Poem_xtn_mgr xtn_mgr = (Poem_xtn_mgr)wiki.Xtn_mgr().Get_or_fail(Poem_xtn_mgr.XTN_KEY);
		byte[] poem_bry = Parse_lines(wiki.Utl__bfr_mkr(), src, itm_bgn, itm_end);
		xtn_root = xtn_mgr.Parser().Parse_text_to_wdom_old_ctx(ctx, poem_bry, true); // NOTE: ignoring paragraph mode; technically MW enables para mode, but by replacing "\n" with "<br/>\n" all the logic with para/pre mode is skipped
	}
	public void Xtn_write(Bry_bfr bfr, Xoae_app app, Xop_ctx ctx, Xoh_html_wtr html_wtr, Xoh_wtr_ctx hctx, Xop_xnde_tkn xnde, byte[] src) {
		if (xtn_root == null) return;	// inline poem; write nothing; EX: <poem/>
		bfr.Add(Div_poem_bgn);
		html_wtr.Write_tkn(bfr, ctx, hctx, xtn_root.Root_src(), xnde, Xoh_html_wtr.Sub_idx_null, xtn_root);
		bfr.Add(Div_poem_end);			
	}
	private static byte[] Parse_lines(Bry_bfr_mkr bfr_mkr, byte[] src, int src_bgn, int src_end) {
		Bry_bfr bfr = bfr_mkr.Get_k004().Mkr_rls();
		int line_bgn = src_bgn; boolean line_is_1st = true;
		while (line_bgn < src_end) {																			// iterate over each \n
			boolean indent_enabled = false;
			if (line_is_1st)	line_is_1st = false;
			else {
				int line_end_w_br = line_bgn + Xowa_br_mark.length;
				if (	line_end_w_br < src_end																	// check for out of bounds; PAGE:en.s:The Hebrew Nation did not write it; DATE:2015-01-31
					&&	Bry_.Match(src, line_bgn, line_end_w_br, Xowa_br_mark))									// "<br/>\n" already inserted by XOWA; do not insert again; DATE:2014-10-20
					bfr.Add_byte_nl();
				else
					bfr.Add(Html_tag_.Br_inl).Add_byte_nl().Add(Xowa_br_mark);									// add "<br/>\n" unless 1st line; EX: "<poem>\n\s" should not add leading "<br/>\n"
			}
			switch (src[line_bgn]) {
				case Byte_ascii.Space:																			// "\n\s" -> "\n&#160;"
					int space_end = Bry_finder.Find_fwd_while(src, line_bgn, src_end, Byte_ascii.Space);
					int space_count = space_end - line_bgn;
					line_bgn = space_end;
					for (int i = 0; i < space_count; ++i)
						bfr.Add(Html_entity_.Nbsp_num_bry);
					break;
				case Byte_ascii.Colon: {																		// "\n:" -> <span class='mw-poem-indented' style='display: inline-block; margin-left: #em;'>
					int colon_end = Bry_finder.Find_fwd_while(src, line_bgn, src_end, Byte_ascii.Colon);
					int colon_count = colon_end - line_bgn;
					line_bgn = colon_end;
					bfr.Add(Indent_bgn).Add_int_variable(colon_count).Add(Indent_end);							// add <span class='mw-poem-indented' style='display: inline-block; margin-left: #em;'>
					indent_enabled = true;
					break;
				}
			}
			int line_end = Bry_finder.Find_fwd(src, Byte_ascii.NewLine, line_bgn, src_end);						// find end "\n"
			if (line_end == Bry_finder.Not_found) line_end = src_end;											// no "\n"; use eos;
			bfr.Add_mid(src, line_bgn, line_end);																// add everything from line_bgn to line_end
			if (indent_enabled) bfr.Add(Html_tag_.Span_rhs);													// if "\n:", add </span>
			line_bgn = line_end + 1;																			// +1 to skip over end "\n"
		}
		return bfr.Xto_bry_and_clear();
	}
	private static byte[]
	  Div_poem_bgn = Bry_.new_a7("<div class=\"poem\">\n<p>\n")	// NOTE: always enclose in <p>; MW does this implicitly in its modified parse; DATE:2014-04-27
	, Div_poem_end = Bry_.new_a7("\n</p>\n</div>")
	, Indent_bgn = Bry_.new_a7("\n<span class='mw-poem-indented' style='display: inline-block; margin-left: ")
	, Indent_end = Bry_.new_a7("em;'>")
	, Xowa_br_mark = Bry_.new_a7("<!--xowa.br-->")
	;
}
