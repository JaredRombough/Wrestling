package openwrestling.view.browser.controller;

import openwrestling.model.interfaces.iSortFilter;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.segmentEnum.BrowseMode;

public class BrowseParams {

    public BrowseMode browseMode;
    public iSortFilter filter;
    public Promotion promotion;
}
