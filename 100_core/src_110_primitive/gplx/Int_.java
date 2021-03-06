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
import gplx.core.strings.*;
public class Int_ implements GfoInvkAble {
	public static final String Cls_val_name = "int";
	public static final Class<?> Cls_ref_type = Integer.class; 
	public static final int Base1 = 1;
	public static final int Const_dlm_len = 1;
	public static final int Const_position_after_char = 1;
	public static final int Null = Int_.MinValue;
	public static int coerce_(Object v) {
		try {String s = String_.as_(v); return s == null ? Int_.cast_(v) : Int_.parse_(s);}
		catch (Exception e) {throw Err_.cast_(e, int.class, v);}
	}
	public static int[] Ary_empty = new int[0];
	public static int[] Ary(int... v) {return v;}
	public static int[] Ary_copy(int[] ary) {return Ary_copy(ary, ary.length);}
	public static int[] Ary_copy(int[] ary, int new_len) {
		int old_len = ary.length;
		int[] rv = new int[new_len];
		for (int i = 0; i < old_len; i++)
			rv[i] = ary[i];
		return rv;
	}
	public static void Ary_copy_to(int[] src, int src_len, int[] trg) {
		for (int i = 0; i < src_len; ++i)
			trg[i] = src[i];
	}
	public static int[] AryRng(int bgn, int end) {
		int len = end - bgn + 1;
		int[] rv = new int[len];
		for (int i = 0; i < len; i++)
			rv[i] = bgn + i;
		return rv;
	}
	public static boolean Bounds_chk(int bgn, int end, int len) {return bgn > -1 && end < len;}
	public static final int
		  MinValue	= Integer.MIN_VALUE	
		, MaxValue	= Integer.MAX_VALUE	
		, Neg1		= -1
		, Neg1_count = -1
		;
	public static int parse_or_(String raw, int or) {
		if (raw == null) return or;
		int rawLen = String_.Len(raw); if (rawLen == 0) return or;
		int rv = 0, tmp = 0, factor = 1;
		for (int i = rawLen; i > 0; i--) {
			char c = String_.CharAt(raw, i - 1);
			switch (c) {
				case '0': tmp = 0; break; case '1': tmp = 1; break; case '2': tmp = 2; break; case '3': tmp = 3; break; case '4': tmp = 4; break;
				case '5': tmp = 5; break; case '6': tmp = 6; break; case '7': tmp = 7; break; case '8': tmp = 8; break; case '9': tmp = 9; break;
				case '-': rv *= -1; continue;	// NOTE: note continue
				default: return or;
			}
			rv += (tmp * factor);
			factor *= 10;
		}
		return rv;
	}
	public static int EnsureLessThan(int v, int max) {return v >= max ? max : v;}
	public static boolean In(int v, int comp0, int comp1) {return v == comp0 || v == comp1;}
	public static boolean In(int v, int... ary) {
		for (int itm : ary)
			if (v == itm) return true;
		return false;
	}
	public static int BoundEnd(int v, int end) {return v >= end ? end - 1 : v;}
	public static int Min(int lhs, int rhs) {return lhs < rhs ? lhs : rhs;}
	public static int Max(int lhs, int rhs) {return lhs > rhs ? lhs : rhs;}
	public static int ModIfNeg1(int v, int or) {return v == -1 ? or : v;}
	public static boolean RangeCheck(int v, int max) {return v >= 0 && v < max;}
	public static void RangeCheckOrFail_list(int v, int max, String s)		{if (v < 0   || v >= max) throw Err_.new_("bounds check failed").Add("msg", s).Add("v", v).Add("min",   0).Add("max", max - 1);}
	public static void RangeCheckOrFail(int v, int min, int max, String s)	{if (v < min || v >= max) throw Err_.new_("bounds check failed").Add("msg", s).Add("v", v).Add("min", min).Add("max", max);}
	public static boolean Between(int v, int lhs, int rhs) {
		int lhsCompare = v == lhs ? 0 : (v < lhs ? -1 : 1);
		int rhsCompare = v == rhs ? 0 : (v < rhs ? -1 : 1);
		return (lhsCompare * rhsCompare) != 1;	// 1 when v is (a) greater than both or (b) less than both
	}
	public static int Div(int v, float divisor) {return (int)((float)v / divisor);}
	public static int DivAndRoundUp(int v, int divisor) {
		int whole = v / divisor;
		int partial = v % divisor == 0 ? 0 : 1;
		return whole + partial;
	}
	public static int Mult(int v, float multiplier) {
		float product = ((float)v * multiplier);	// WORKAROUND (DotNet): (int)((float)v * multiplier) returns 0 for 100 and .01f
		return (int)product;
	}
	public static int Compare(int lhs, int rhs) {
		if		(lhs == rhs) 	return CompareAble_.Same;
		else if (lhs < rhs)		return CompareAble_.Less;
		else 					return CompareAble_.More;
	}
	public static int DigitCount(int v) {
		int log10 = Log10(v);
		return v > -1 ? log10 + 1 : log10 * -1 + 2;
	}
	public static int Log10(int v) {
		if (v == 0) return 0;
		int sign = 1;
		if (v < 0) {
			if (v == Int_.MinValue) return -9;	// NOTE: Int_.MinValue * -1 = Int_.MinValue
			v *= -1;
			sign = -1;
		}
		int rv = Log10AryLen - 2;	// rv will only happen when v == Int_.MaxValue
		int bgn = 0;
		if (v > 1000) {				// optimization to reduce number of ops to < 5
			bgn = 3;
			if (v > 1000000) bgn = 6;
		}
		for (int i = bgn; i < Log10AryLen; i++) {
			if (v < Log10Ary[i]) {rv = i - 1; break;}
		}
		return rv * sign;
	}	public static int[] Log10Ary = new int[] {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, Int_.MaxValue}; public static int Log10AryLen = 11;
	public Int_ FailIfNeg1(String key, int val) {
		if (val < 0) throw Err_.new_("key must be >= 0").Add("key", key).Add("val", val);
		return this;
	}
	public static String Xto_str_pad_bgn_space(int v, int reqdPlaces)	{return Xto_str_pad_bgn_zero(v, reqdPlaces, Byte_ascii.Space, true);}	// EX: 1, 3 returns "  1"
	public static String Xto_str_pad_bgn_zero(int v, int reqdPlaces)	{return Xto_str_pad_bgn_zero(v, reqdPlaces, Byte_ascii.Num_0, true);}	// EX: 1, 3 returns "001"
	static String Xto_str_pad_bgn_zero(int val, int places, byte pad_chr, boolean bgn) {
		int len = DigitCount(val);
		int pad_len = places - len; if (pad_len < 0) return Int_.Xto_str(val);
		Bry_bfr bfr = Bry_bfr.new_();
		boolean neg = val < 0;
		if (bgn) {	// special logic to handle negative numbers; EX: -1 -> "-001", not "00-1"
			if (neg) {
				bfr.Add_byte(Byte_ascii.Dash);
				val *= -1;
				--len;
			}
		}
		else
			bfr.Add_int_fixed(val, len);
		bfr.Add_byte_repeat(pad_chr, pad_len);
		if (bgn) bfr.Add_int_fixed(val, len);	// NOTE: neg handled above
		return bfr.Xto_str();
	}
	public static int read_(Object o) {String s = String_.as_(o); return s != null ? Int_.parse_(s) : Int_.cast_(o);}
	public static int parse_(String raw) {try {return Integer.parseInt(raw);} catch(Exception e) {throw Err_.parse_type_exc_(e, int.class, raw);}}
	public static int cast_(Object obj) {try {return (Integer)obj;} catch(Exception exc) {throw Err_.type_mismatch_exc_(exc, int.class, obj);}}
	public static int cast_or_(Object obj, int or) {try {return (Integer)obj;} catch(Exception e) {Err_.Noop(e); return or;}}
	public static int Xby_double_(double v) {return (int)v;}
	public static String Xto_str(int v) {return new Integer(v).toString();}
	public static String Xto_str_fmt(int v, String fmt) {return new java.text.DecimalFormat(fmt).format(v);}
	public static boolean TypeMatch(Class<?> type) {return type == int.class || type == Integer.class;}
	public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, Invk_XtoStr_PadBgn))			{
			int v = m.ReadInt(GfsCore_.Arg_primitive), pad = m.ReadInt("pad");
			return ctx.Deny() ? (Object)this : Xto_str_pad_bgn_zero(v, pad);
		}
		else if	(ctx.Match(k, "Add")) {
			int v = m.ReadInt(GfsCore_.Arg_primitive), operand = m.ReadInt("operand");
			return ctx.Deny() ? (Object)this : v + operand;
		}
		else												return GfoInvkAble_.Rv_unhandled;			
	}	public static final String Invk_XtoStr_PadBgn = "XtoStr_PadBgn";
        public static final Int_ Gfs = new Int_();
//		public static int Xto_int_hex(String v) {return Integer.parseInt(v, 16);}	
	public static int Xto_int_hex(byte[] src) {return Xto_int_hex(src, 0, src.length);}
	public static int Xto_int_hex(byte[] src, int bgn, int end) {
		int rv = 0; int factor = 1;
		for (int i = end - 1; i >= bgn; i--) {
			int val = Xto_int_hex(src[i]);
			rv += (val * factor);
			factor *= 16;
		}
		return rv;
	}
	public static int Xto_int_hex(byte b) {
		switch (b) {
			case Byte_ascii.Num_0: case Byte_ascii.Num_1: case Byte_ascii.Num_2: case Byte_ascii.Num_3: case Byte_ascii.Num_4:
			case Byte_ascii.Num_5: case Byte_ascii.Num_6: case Byte_ascii.Num_7: case Byte_ascii.Num_8: case Byte_ascii.Num_9:
				return b - Byte_ascii.Num_0;
			case Byte_ascii.Ltr_A: case Byte_ascii.Ltr_B: case Byte_ascii.Ltr_C: case Byte_ascii.Ltr_D: case Byte_ascii.Ltr_E: case Byte_ascii.Ltr_F:
				return b - Byte_ascii.Ltr_A + 10;
			case Byte_ascii.Ltr_a: case Byte_ascii.Ltr_b: case Byte_ascii.Ltr_c: case Byte_ascii.Ltr_d: case Byte_ascii.Ltr_e: case Byte_ascii.Ltr_f:
				return b - Byte_ascii.Ltr_a + 10;
			default:
				return -1;
		}
	}
	public static String Xto_str_hex(int v) {
		String rv = Integer.toHexString(v); 
		int rvLen = String_.Len(rv);
		if (rvLen < 8) rv = String_.Repeat("0", 8 - rvLen) + rv;
		return String_.Upper(rv);
	}
	public static String Xto_str(int[] ary) {return Xto_str(ary, " ");}
	public static String Xto_str(int[] ary, String dlm) {
		String_bldr sb = String_bldr_.new_();
		for (int i = 0; i < ary.length; i++)
			sb.Add_spr_unless_first(Int_.Xto_str(ary[i]), dlm, i);
		return sb.XtoStr();
	}
	public static int[] Ary_parse(String raw_str, int reqd_len, int[] or) {
		byte[] raw_bry = Bry_.new_a7(raw_str);
		int raw_bry_len = raw_bry.length;
		int[] rv = new int[reqd_len];
		int cur_val = 0, cur_mult = 1, cur_idx = reqd_len - 1; boolean signed = false;
		for (int i = raw_bry_len - 1; i > -2; i--) {
			byte b = i == -1 ? Byte_ascii.Comma : raw_bry[i];
			switch (b) {
				case Byte_ascii.Num_0: case Byte_ascii.Num_1: case Byte_ascii.Num_2: case Byte_ascii.Num_3: case Byte_ascii.Num_4:
				case Byte_ascii.Num_5: case Byte_ascii.Num_6: case Byte_ascii.Num_7: case Byte_ascii.Num_8: case Byte_ascii.Num_9:
					if (signed) return or;
					cur_val += (b - Byte_ascii.Num_0) * cur_mult;
					cur_mult *= 10;
					break;
				case Byte_ascii.Space: case Byte_ascii.NewLine: case Byte_ascii.CarriageReturn: case Byte_ascii.Tab:
					break;
				case Byte_ascii.Comma:
					if (cur_idx < 0) return or;
					rv[cur_idx--] = cur_val;
					cur_val = 0; cur_mult = 1;
					signed = false;
					break;
				case Byte_ascii.Dash:
					if (signed) return or;
					cur_val *= -1;
					signed = true;
					break;
				case Byte_ascii.Plus:	// noop; all values positive by default
					if (signed) return or;
					signed = true;
					break;
				default:
					return or;
			}
		}
		return cur_idx == -1 ? rv : or;	// cur_idx == -1 checks for unfilled; EX: Ary_parse("1,2", 3, null) is unfilled
	}
	public static int[] Ary_parse(String raw_str, String spr) {
		String[] ary = String_.Split(raw_str, spr);
		int len = ary.length;
		int[] rv = new int[len];
		for (int i = 0; i < len; i++)
			rv[i] = Int_.parse_(ary[i]);
		return rv;
	}
	public static byte[] Xto_bry(int v) {return Bry_.new_a7(Xto_str(v));}
}
