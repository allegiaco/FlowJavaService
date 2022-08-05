package com.github.allegiaco.flowjavaservice.utils;

import com.nftco.flow.sdk.*;
import com.github.allegiaco.flowjavaservice.abstraction.FlowServiceUtilsAbstract;

import java.math.BigDecimal;

public final class FlowServiceUtils extends FlowServiceUtilsAbstract {

    public FlowServiceUtils(FlowAccessApi accessAPI) {
        super(accessAPI);
    }

    public BigDecimal getAccountBalance(FlowAddress address) {
        FlowAccount account = this.getAccount(address);
        return account.getBalance();
    }

}