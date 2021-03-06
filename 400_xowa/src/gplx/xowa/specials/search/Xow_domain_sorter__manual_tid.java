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
import gplx.core.primitives.*; import gplx.xowa.wikis.*; import gplx.xowa.langs.*;
import gplx.xowa.wikis.domains.crts.*;
class Xow_domain_sorter__manual implements gplx.lists.ComparerAble {
	private final Xow_domain cur_domain;
	private final Xow_domain_crt_itm[] ary; private final int ary_len;
	public Xow_domain_sorter__manual(Xow_domain cur_domain, Xow_domain_crt_itm[] ary) {
		this.cur_domain = cur_domain; this.ary = ary; this.ary_len = ary.length;
	}
	public int compare(Object lhsObj, Object rhsObj) {
		Xow_domain lhs = (Xow_domain)lhsObj;
		Xow_domain rhs = (Xow_domain)rhsObj;
		int lhs_sort = Get_sort_idx_or_neg1(lhs);
		int rhs_sort = Get_sort_idx_or_neg1(rhs);
		if		(lhs_sort == -1 && rhs_sort != -1)	return rhs_sort;
		else if	(lhs_sort != -1 && rhs_sort == -1)	return lhs_sort;
		else if (lhs_sort != -1 && rhs_sort != -1)	return Int_.Compare(lhs_sort, rhs_sort);
		else										return Bry_.Compare(lhs.Domain_bry(), rhs.Domain_bry());
	}
	private int Get_sort_idx_or_neg1(Xow_domain domain) {
		int sort_idx = domain.Sort_idx(); if (sort_idx != -1) return sort_idx;
		sort_idx = Int_.MaxValue;
		for (int i = 0; i < ary_len; ++i) {
			Xow_domain_crt_itm crt = ary[i];
			if (crt.Matches(cur_domain, domain)) {sort_idx = i; break;}
		}
		domain.Sort_idx_(sort_idx);
		return sort_idx;
	}
	public static void Sort(Xow_domain_sorter__manual sorter, Xow_domain[] ary) {
		int len = ary.length;
		for (int i = 0; i < len; ++i)
			ary[i].Sort_idx_(-1);
		Array_.Sort(ary, sorter);
	}
}
class Xow_domain_sorter__manual_tid implements gplx.lists.ComparerAble {
	private final Hash_adp sort_hash = Hash_adp_.new_(); private final Int_obj_ref sort_key = Int_obj_ref.neg1_();
	public Xow_domain_sorter__manual_tid(int[] id_ary) {
		int len = id_ary.length;
		for (int i = 0; i < len; ++i) {
			int id_itm = id_ary[i];
			sort_hash.Add_if_dupe_use_nth(Int_obj_ref.new_(id_itm), Int_obj_ref.new_(i));
		}
	}
	public int compare(Object lhsObj, Object rhsObj) {
		Xow_domain lhs = (Xow_domain)lhsObj;
		Xow_domain rhs = (Xow_domain)rhsObj;
		int lhs_sort = Get_sort_idx_or_neg1(lhs.Domain_tid());
		int rhs_sort = Get_sort_idx_or_neg1(rhs.Domain_tid());
		if		(lhs_sort == -1 && rhs_sort != -1)	return rhs_sort;
		else if	(lhs_sort != -1 && rhs_sort == -1)	return lhs_sort;
		else if (lhs_sort != -1 && rhs_sort != -1)	return Int_.Compare(lhs_sort, rhs_sort);
		else										return Bry_.Compare(Xow_domain_type_.Get_type_as_bry(lhs.Domain_tid()), Xow_domain_type_.Get_type_as_bry(rhs.Domain_tid()));
	}
	private int Get_sort_idx_or_neg1(int tid) {
		Object o = sort_hash.Get_by(sort_key.Val_(tid));
		return o == null ? -1 : ((Int_obj_ref)o).Val();
	}
	public static Xow_domain_sorter__manual_tid new_(byte[]... id_brys) {
		int len = id_brys.length;
		int[] id_ints = new int[len];
		for (int i = 0; i < len; ++i) {
			byte[] id_bry = id_brys[i];
			int id_int = Xow_domain_type_.Get_type_as_tid(id_bry);
			id_ints[i] = id_int;
		}
		return new Xow_domain_sorter__manual_tid(id_ints);
	}
}
class Xow_domain_sorter__manual_lang implements gplx.lists.ComparerAble {
	private final Hash_adp sort_hash = Hash_adp_.new_(); private final Int_obj_ref sort_key = Int_obj_ref.neg1_();
	public Xow_domain_sorter__manual_lang(int[] id_ary) {
		int len = id_ary.length;
		for (int i = 0; i < len; ++i) {
			int id_int = id_ary[i];
			sort_hash.Add_if_dupe_use_nth(Int_obj_ref.new_(id_int), Int_obj_ref.new_(i));
		}
	}
	public int compare(Object lhsObj, Object rhsObj) {
		Xow_domain lhs = (Xow_domain)lhsObj;
		Xow_domain rhs = (Xow_domain)rhsObj;
		int lhs_sort = Get_sort_idx_or_neg1(lhs.Lang_uid());
		int rhs_sort = Get_sort_idx_or_neg1(rhs.Lang_uid());
		if		(lhs_sort == -1 && rhs_sort != -1)	return rhs_sort;
		else if	(lhs_sort != -1 && rhs_sort == -1)	return lhs_sort;
		else if (lhs_sort != -1 && rhs_sort != -1)	return Int_.Compare(lhs_sort, rhs_sort);
		else										return Bry_.Compare(lhs.Lang_key(), rhs.Lang_key());
	}
	private int Get_sort_idx_or_neg1(int tid) {
		Object o = sort_hash.Get_by(sort_key.Val_(tid));
		return o == null ? -1 : ((Int_obj_ref)o).Val();
	}
	public static Xow_domain_sorter__manual_lang new_(byte[]... id_brys) {
		int len = id_brys.length;
		int[] id_ints = new int[len];
		for (int i = 0; i < len; ++i) {
			byte[] id_bry = id_brys[i];
			int id_int = Xol_lang_itm_.Get_by_key_or_intl(id_bry).Id();
			id_ints[i] = id_int;
		}
		return new Xow_domain_sorter__manual_lang(id_ints);
	}
}