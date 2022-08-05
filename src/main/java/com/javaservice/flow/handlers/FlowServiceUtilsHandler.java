package com.javaservice.flow.handlers;

import com.javaservice.flow.utils.FlowServiceUtils;
import com.javaservice.flow.model.enums.NetType;

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
