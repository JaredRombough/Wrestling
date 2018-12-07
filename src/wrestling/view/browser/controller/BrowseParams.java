package wrestling.view.browser.controller;

import wrestling.model.interfaces.iSortFilter;
import wrestling.model.modelView.PromotionView;
import wrestling.model.segmentEnum.BrowseMode;

public class BrowseParams {

    public BrowseMode browseMode;
    public iSortFilter filter;
    public PromotionView promotion;
}
