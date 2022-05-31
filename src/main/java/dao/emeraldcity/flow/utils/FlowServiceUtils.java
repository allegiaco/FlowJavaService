package dao.emeraldcity.flow.utils;

import com.nftco.flow.sdk.*;

import java.math.BigDecimal;

public class FlowServiceUtils {

    private FlowAccessApi accessAPI;

    public FlowServiceUtils(FlowAccessApi accessAPI) {
        this.accessAPI = accessAPI;
    }

    public FlowAccessApi getAccessAPI() {
        return accessAPI;
    }

    public void setAccessAPI(FlowAccessApi accessAPI) {
        this.accessAPI = accessAPI;
    }


    public FlowAccount getAccount(FlowAddress address) {
        return this.accessAPI.getAccountAtLatestBlock(address);
    }

    public BigDecimal getAccountBalance(FlowAddress address) {
        FlowAccount account = this.getAccount(address);
        return account.getBalance();
    }

    public FlowBlock getLatestBlock() {
        return this.accessAPI.getLatestBlock(true);
    }

    public Long getLatestBlockHeight() {
        return this.accessAPI.getLatestBlock(true).getHeight();
    }

    public FlowId getLatestBlockID() {
        return this.accessAPI.getLatestBlockHeader().getId();
    }

    public FlowAccountKey getAccountKey(FlowAddress address, int keyIndex) {
        FlowAccount account = this.getAccount(address);
        return account.getKeys().get(keyIndex);
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
