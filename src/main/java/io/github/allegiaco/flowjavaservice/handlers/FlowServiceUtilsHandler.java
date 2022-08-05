package io.github.allegiaco.flowjavaservice.handlers;

import io.github.allegiaco.flowjavaservice.utils.FlowServiceUtils;
import io.github.allegiaco.flowjavaservice.model.enums.NetType;

public class FlowServiceUtilsHandler {

    private static FlowServiceUtils flowServiceUtilsTestNet;
    private static FlowServiceUtils flowServiceUtilsMainNet;

    public static FlowServiceUtils getFlowServiceUtils(NetType netType) {
        if (netType.equals(NetType.TESTNET)) {
            if (flowServiceUtilsTestNet == null) {
                flowServiceUtilsTestNet = new FlowServiceUtils(BlockchainConnectionHandler.getTestnetConnection());
            }
            return flowServiceUtilsTestNet;
        }
        if (netType.equals(NetType.MAINNET)) {
            if (flowServiceUtilsMainNet == null) {
                flowServiceUtilsMainNet = new FlowServiceUtils(BlockchainConnectionHandler.getMainnetConnection());
            }
            return flowServiceUtilsMainNet;
        }
        return null;
    }



}
