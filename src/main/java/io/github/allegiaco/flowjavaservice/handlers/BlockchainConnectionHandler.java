package io.github.allegiaco.flowjavaservice.handlers;

import com.nftco.flow.sdk.Flow;
import com.nftco.flow.sdk.FlowAccessApi;

public class BlockchainConnectionHandler {

    private static final String testnetHost = "access.devnet.nodes.onflow.org";
    private static final int port = 9000;
    private static final String mainnetHost = "access.mainnet.nodes.onflow.org";

    private static FlowAccessApi testnetAccess;
    private static FlowAccessApi mainnetAccess;


    public static synchronized FlowAccessApi getTestnetConnection() {
        if (testnetAccess == null) {
            testnetAccess = Flow.newAccessApi(testnetHost, port);
        }
        return testnetAccess;
    }

    public static synchronized FlowAccessApi getMainnetConnection() {
        if (mainnetAccess == null) {
            mainnetAccess = Flow.newAccessApi(mainnetHost, port);
        }
        return mainnetAccess;
    }

}
