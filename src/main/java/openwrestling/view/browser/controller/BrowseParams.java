package openwrestling.view.browser.controller;

import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.interfaces.iSortFilter;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;

public class BrowseParams {

    public BrowseMode browseMode;
    public iSortFilter filter;
    public Promotion promotion;
}
