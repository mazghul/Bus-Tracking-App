package com.vuedata.bustrackingapp;

import java.util.List;

/**
 * Created by Maz Vuedata on 14-02-2017.
 */

public interface LocationFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(Route route);
}
