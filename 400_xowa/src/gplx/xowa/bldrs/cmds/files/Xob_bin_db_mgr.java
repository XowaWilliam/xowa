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
package gplx.xowa.bldrs.cmds.files; import gplx.*; import gplx.xowa.*; import gplx.xowa.bldrs.*; import gplx.xowa.bldrs.cmds.*;
import gplx.core.primitives.*; import gplx.ios.*;
import gplx.fsdb.meta.*;
class Xob_bin_db_mgr {
	private final int[] ns_ids; private final int ns_ids_len;
	private final Ordered_hash nth_hash = Ordered_hash_.new_(); private final Int_obj_ref tier_key = Int_obj_ref.neg1_();
	public Xob_bin_db_mgr(int[] ns_ids) {
		this.ns_ids = ns_ids; this.ns_ids_len = ns_ids.length;
	}
	public boolean Schema_is_1() {return schema_is_1;} private boolean schema_is_1;
	public void Init_by_mnt_mgr(Fsm_mnt_mgr trg_mnt_mgr) {
		Fsm_mnt_itm trg_mnt_itm = trg_mnt_mgr.Mnts__get_main(); 
		this.schema_is_1 = trg_mnt_itm.Db_mgr().File__schema_is_1();
		Fsm_bin_mgr bin_db_mgr = trg_mnt_itm.Bin_mgr();
		int len = ns_ids_len;
		for (int i = 0; i < len; ++i) {	// iterate ns_ids and add default nth
			int ns_id = ns_ids[i];
			Xob_bin_db_itm nth = new Xob_bin_db_itm(-1, null, ns_id, 0);
			nth_hash.Add(Int_obj_ref.new_(ns_ids[i]), nth);
		}
		len = bin_db_mgr.Dbs__len();
		for (int i = 0; i < len; ++i) {	// iterate bin_dbs to find max pt_id for each ns
			Fsm_bin_fil fil = bin_db_mgr.Dbs__get_at(i);
			Xob_bin_db_itm itm = schema_is_1 ? Xob_bin_db_itm.new_v1(fil) : Xob_bin_db_itm.new_v2(fil);
			int ns_id = itm.Ns_id();
			Xob_bin_db_itm nth = (Xob_bin_db_itm)nth_hash.Get_by(tier_key.Val_(ns_id));
			if (itm.Pt_id() > nth.Pt_id())	// update max pt_id
				nth.Set(itm.Id(), itm.Pt_id(), itm.Db_url()); // note that ns_id is same
		}
		len = nth_hash.Count();
		for (int i = 0; i < len; ++i) {	// iterated tiers to calculate max_size
			Xob_bin_db_itm nth = (Xob_bin_db_itm)nth_hash.Get_at(i);
			if (nth.Id() == -1) continue;	// ignore default nth
			IoItmFil nth_itm = Io_mgr.I.QueryFil(nth.Db_url());
			nth.Db_len_(nth_itm.Size());
		}
	}
	public boolean Tier_id_is_last(int tier_id) {return tier_id >= ns_ids_len;}	// assumes tier_id is 0 based; EX: 0,1,2 for 
	public int Get_ns_id(int tier_id) {return ns_ids[tier_id];}
	public int Increment_pt_id(Xob_bin_db_itm itm) {
		itm.Set(-1, itm.Pt_id() + 1, null);
		itm.Db_len_(0);
		return itm.Pt_id();
	}
	public String Gen_name(String domain_str, int ns_id, int pt_id) {
		return schema_is_1 ? Xob_bin_db_itm.Gen_name_v1(pt_id) : Xob_bin_db_itm.Gen_name_v2(domain_str, ns_id, pt_id);
	}
	public Xob_bin_db_itm Get_nth_by_tier(int tier_id) {
		if (schema_is_1) return (Xob_bin_db_itm)nth_hash.Get_by(tier_key.Val_(0)); // v1 is always in ns_0
		if (tier_id >= ns_ids_len) throw Err_.new_("tier out of range: tier_id={0} len={1}", tier_id, ns_ids_len);
		int ns_id = ns_ids[tier_id];
		return (Xob_bin_db_itm)nth_hash.Get_by(tier_key.Val_(ns_id));
	}
}
