package openwrestling.view.browser.controller;

import openwrestling.model.interfaces.iSortFilter;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.segmentEnum.BrowseMode;

public class BrowseParams {

    public BrowseMode browseMode;
    public iSortFilter filter;
    public PromotionView promotion;
}
