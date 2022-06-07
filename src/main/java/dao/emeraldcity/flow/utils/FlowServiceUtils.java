package dao.emeraldcity.flow.utils;

import com.nftco.flow.sdk.*;
import dao.emeraldcity.flow.abstraction.FlowServiceUtilsAbstract;

import java.math.BigDecimal;

public final class FlowServiceUtils extends FlowServiceUtilsAbstract {

    private FlowAccessApi accessAPI;

    public FlowServiceUtils(FlowAccessApi accessAPI) {
        super(accessAPI);
    }

    public BigDecimal getAccountBalance(FlowAddress address) {
        FlowAccount account = this.getAccount(address);
        return account.getBalance();
    }

}
