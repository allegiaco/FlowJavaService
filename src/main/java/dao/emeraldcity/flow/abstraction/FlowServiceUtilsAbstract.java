package dao.emeraldcity.flow.abstraction;

import com.nftco.flow.sdk.*;

import java.math.BigDecimal;

public abstract class FlowServiceUtilsAbstract {

    protected FlowAccessApi accessAPI;

    public FlowServiceUtilsAbstract(FlowAccessApi accessAPI) {
        this.accessAPI = accessAPI;
    }



    public FlowAccessApi accessAPI() {
        return accessAPI;
    }

    public void setAccessAPI(FlowAccessApi accessAPI) {
        this.accessAPI = accessAPI;
    }

    public FlowAccount getAccount(FlowAddress address) {
        return this.accessAPI.getAccountAtLatestBlock(address);
    }

    public FlowAccountKey getAccountKey(FlowAddress address, int keyIndex) {
        FlowAccount account = this.getAccount(address);
        return account.getKeys().get(keyIndex);
    }

    public FlowBlock getLatestBlock() {
        return this.accessAPI.getLatestBlock(true);
    }

    public FlowId getLatestBlockID() {
        return this.accessAPI.getLatestBlockHeader().getId();
    }

    public Long getLatestBlockHeight() {
        return this.accessAPI.getLatestBlock(true).getHeight();
    }



    public FlowTransactionResult getTransactionResult(FlowId txID) {
        FlowTransactionResult result = this.accessAPI.getTransactionResultById(txID);
        return result;
    }

    public FlowTransactionResult waitForSeal(FlowId txID) {

        FlowTransactionResult txResult;

        while (true) {

            txResult = this.getTransactionResult(txID);
            if (txResult.getStatus().equals(FlowTransactionStatus.SEALED)) {
                return txResult;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
