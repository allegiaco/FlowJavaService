package com.github.allegiaco.flowjavaservice.model;

import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowArgument;

import java.util.List;
import java.util.Map;

public class FlowTransactionObject {

    private String transaction;
    private List<FlowArgument> argumentsList;
    private Map<String, String> scriptChanges;
    private FlowAddress proposerAddress;
    private List<FlowAddress> authorizers;
    private FlowAddress payerAddress;
    private boolean skipSeal;

    public FlowTransactionObject() {

    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public List<FlowArgument> getArgumentsList() {
        return argumentsList;
    }

    public void setArgumentsList(List<FlowArgument> argumentsList) {
        this.argumentsList = argumentsList;
    }

    public Map<String, String> getScriptChanges() {
        return scriptChanges;
    }

    public void setScriptChanges(Map<String, String> scriptChanges) {
        this.scriptChanges = scriptChanges;
    }

    public FlowAddress getProposerAddress() {
        return proposerAddress;
    }

    public void setProposerAddress(FlowAddress proposerAddress) {
        this.proposerAddress = proposerAddress;
    }

    public List<FlowAddress> getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(List<FlowAddress> authorizers) {
        this.authorizers = authorizers;
    }

    public FlowAddress getPayerAddress() {
        return payerAddress;
    }

    public void setPayerAddress(FlowAddress payerAddress) {
        this.payerAddress = payerAddress;
    }

    public boolean getSkipSeal() {
        return skipSeal;
    }

    public void setSkipSeal(boolean skipSeal) {
        this.skipSeal = skipSeal;
    }

    public boolean isTransactionReady() {
        if (this.transaction != null && this.argumentsList != null && this.scriptChanges != null && this.proposerAddress != null && this.authorizers != null && this.payerAddress != null) {
            return true;
        }
        return false;
    }


}
