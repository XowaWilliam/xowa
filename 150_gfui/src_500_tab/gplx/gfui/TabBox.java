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
package gplx.gfui; import gplx.*;
public class TabBox extends GfuiElemBase {
	public int Tabs_Count() {return mgr.Count();}
	public TabPnlItm Tabs_SelectedItm() {return mgr.CurTab();}
	public GfuiElem Tabs_FetchAt(int i) {return pnlBox.SubElems().Get_by(mgr.Get_at(i).Key());}
	public GfuiElem Tabs_SelectedPnl() {return pnlBox.SubElems().Get_by(mgr.CurTab().Key());}
	public void Tabs_Select(int idx) {mgr.Select(idx);}
	public GfuiElem Tabs_Add(String key, String name) {
		TabPnlItm newTab = mgr.Add(key, name);
		TabBtnAreaMgr.Add(this, newTab);
		GfuiElem pnl = TabPnlAreaMgr.Add(this, newTab);
		mgr.Select(newTab); // always select added tab
		return pnl;
	}
	public void Tabs_DelAt(int idx) {
		TabBtnAreaMgr.Del(this, mgr.Get_at(idx));
		TabPnlAreaMgr.Del(this, mgr.Get_at(idx));
		mgr.Del_at(idx);
	}
	@gplx.Internal protected TabBoxMgr Mgr() {return mgr;} TabBoxMgr mgr = TabBoxMgr.new_();
	@gplx.Internal protected GfuiElem BtnBox() {return btnBox;} GfuiElem btnBox; 
	@gplx.Internal protected GfuiElem PnlBox() {return pnlBox;} GfuiElem pnlBox;		
	@Override public Object Invk(GfsCtx ctx, int ikey, String k, GfoMsg m) {
		if		(ctx.Match(k, TabBoxEvt_tabSelectByBtn.Key))	TabBoxEvt_tabSelectByBtn.Rcvd(ctx.MsgSrc(), this);
		else if	(ctx.Match(k, TabBoxEvt_tabSelect.Key))			TabBoxEvt_tabSelect.Select(this, ctx, m);
		else if	(ctx.Match(k, TabBoxEvt_nameChange.Key))		TabBoxEvt_nameChange.Rcvd(this, ctx, m);
		else return super.Invk(GfsCtx._, 0, k, m);
		return this;
	}
	@Override public void ctor_GfuiBox_base(KeyValHash ctorArgs) {
		super.ctor_GfuiBox_base(ctorArgs);
		btnBox = TabBtnAreaMgr.new_("btnBox", this);
		pnlBox = TabPnlAreaMgr.new_("pnlBox", this);
		this.Inject_(TabBnd_selectTab.new_());
		GfoEvMgr_.SubSame(mgr, TabBoxEvt_tabSelect.Key, this);
		GfoEvMgr_.SubSame(mgr, TabBoxEvt_nameChange.Key, this);

		this.Lyt_activate();
		this.Lyt().Bands_add(GftBand.fillWidth_());
		this.Lyt().Bands_add(GftBand.fillAll_());
	}
}
class TabBtnAreaMgr {
	public static GfuiElemBase new_(String key, GfuiElem owner) {
		GfuiElemBase rv = new GfuiElemBase();
		rv.ctor_GfuiBox_base(GfuiElem_.init_focusAble_false_());
		rv.Owner_(owner, key);
		return rv;
	}
	public static GfuiBtn Add(TabBox tabBox, TabPnlItm itm) {
		GfuiElem btnBox = tabBox.BtnBox();
		GfuiBtn btn = GfuiBtn_.new_(itm.Key());
		btn.TipText_(itm.Name()).Text_(itm.Name()).Width_(80);
		btn.TextMgr().Font_(btn.TextMgr().Font().Size_(8));
		btn.Click_invk(GfoInvkAbleCmd.new_(tabBox, TabBoxEvt_tabSelectByBtn.Key));
		btn.Inject_(TabBnd_reorderTab.new_(tabBox));
		if (btnBox.SubElems().Count() > 0) {	// place button after last
			GfuiElem lastBtn = btnBox.SubElems().Get_at(btnBox.SubElems().Count() - 1);
			btn.X_(lastBtn.X() + lastBtn.Width());
		}
		btnBox.SubElems().Add(btn);
		return btn;
	}
	public static void Del(TabBox tabBox, TabPnlItm itm) {
		GfuiElem btnBox = tabBox.BtnBox();
		int idx = itm.Idx();
		GfuiBtn btn = (GfuiBtn)btnBox.SubElems().Get_at(idx);
		btnBox.SubElems().Del_at(idx);
		for (int i = idx; i < btnBox.SubElems().Count(); i++) {
			GfuiBtn cur = (GfuiBtn)btnBox.SubElems().Get_at(i);
			cur.X_(cur.X() - btn.Width());
		}
	}
	public static void Select(TabBox tabBox, TabPnlItm curTabItm, TabPnlItm newTabItm) {
		if (curTabItm != null) {
			GfuiBtn curBtn = (GfuiBtn)tabBox.BtnBox().SubElems().Get_at(curTabItm.Idx());
			Select(curBtn, false);
		}
		GfuiBtn newBtn = (GfuiBtn)tabBox.BtnBox().SubElems().Get_at(newTabItm.Idx());
		Select(newBtn, true);
	}
	public static void Move_to(TabBox tabBox, int curIdx, int newIdx) {
		GfuiElemList btns = tabBox.BtnBox().SubElems();
		btns.Move_to(curIdx, newIdx);
		int curX = 0;
		for (int i = 0; i < btns.Count(); i++) {
			GfuiBtn cur = (GfuiBtn)btns.Get_at(i);
			cur.X_(curX);
			curX += cur.Width();
		}
	}
	static void Select(GfuiBtn btn, boolean enabled) {
		btn.TextMgr().Color_(enabled ? ColorAdp_.Black : ColorAdp_.Gray);
		btn.TextMgr().Font().Style_(enabled ? FontStyleAdp_.Bold : FontStyleAdp_.Plain);
		if (enabled)
			btn.Border().All_(PenAdp_.black_());
		else
			btn.Border().None_();
	}
}
class TabPnlAreaMgr {
	public static GfuiElemBase new_(String key, GfuiElem owner) {
		GfuiElemBase rv = new GfuiElemBase();
		rv.ctor_GfuiBox_base(GfuiElem_.init_focusAble_false_());
		rv.Owner_(owner, key);
		rv.Lyt_activate();
		return rv;
	}
	public static GfuiElemBase Add(TabBox tabBox, TabPnlItm itm) {
		GfuiElem pnlBox = tabBox.PnlBox();
		GfuiElemBase pnl = pnl_(tabBox, pnlBox, itm.Key());
		return pnl;
	}
	public static void Del(TabBox tabBox, TabPnlItm itm) {
		tabBox.PnlBox().SubElems().Del_at(itm.Idx());
		((GfuiElemBase)tabBox.PnlBox()).Lyt().SubLyts().Del_at(itm.Idx());
	}
	public static void Select(TabBox tabBox, TabPnlItm curTabItm, TabPnlItm newTabItm) {
		if (curTabItm != null) {
			GfuiElem curTab = tabBox.PnlBox().SubElems().Get_at(curTabItm.Idx());
			curTab.Visible_set(false);
		}
		GfuiElem newTab = tabBox.PnlBox().SubElems().Get_at(newTabItm.Idx());
		newTab.Visible_set(true);
		newTab.Zorder_front();
		newTab.Focus();
	}
	@gplx.Internal protected static GfuiElemBase pnl_(TabBox tabBox, GfuiElem pnlBox, String key) {
		GfuiElemBase rv = new GfuiElemBase();
		rv.ctor_GfuiBox_base(GfuiElem_.init_focusAble_false_());
		rv.Owner_(pnlBox, key);
		rv.Lyt_activate();
		rv.Lyt().Bands_add(GftBand.fillAll_());
		((GfuiElemBase)pnlBox).Lyt().SubLyts().Add(GftGrid.new_().Bands_add(GftBand.fillAll_()));
		return rv;
	}	
}
