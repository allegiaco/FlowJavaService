package com.javaservice.flow.utils;

import com.nftco.flow.sdk.*;
import com.javaservice.flow.abstraction.FlowServiceUtilsAbstract;

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