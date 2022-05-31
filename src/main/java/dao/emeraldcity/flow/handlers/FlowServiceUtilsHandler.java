package dao.emeraldcity.flow.handlers;

import dao.emeraldcity.flow.model.enums.NetType;
import dao.emeraldcity.flow.utils.FlowServiceUtils;

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
