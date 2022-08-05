package com.javaservice.flow.builders;

import com.nftco.flow.sdk.*;

import java.util.List;

public class FlowTransactionBuilder {

    private TransactionBuilder txBuilder;

    public FlowTransactionBuilder() {

        this.txBuilder = new TransactionBuilder();
    }

    public FlowTransactionBuilder addScript(FlowScript flowScript) {

        txBuilder.setScript(flowScript);

        return this;
    }

    public FlowTransactionBuilder addArgumentsList(List<FlowArgument> argumentsList) {

        txBuilder.setArguments(argumentsList);

        return this;
    }

    public FlowTransactionBuilder setReferenceBlockId(FlowId flowId) {

        txBuilder.setReferenceBlockId(flowId);

        return this;
    }

    public FlowTransactionBuilder setGasLimit(Long gasLimit) {

        txBuilder.setGasLimit(gasLimit);

        return this;
    }

    public FlowTransactionBuilder setProposalKey(FlowTransactionProposalKey proposalKey) {

        txBuilder.setProposalKey(proposalKey);

        return this;
    }

    public FlowTransactionBuilder setPayerAddress(FlowAddress payerAddress) {

        txBuilder.setPayerAddress(payerAddress);

        return this;
    }

    public FlowTransactionBuilder setAuthorizers(List<FlowAddress> authorizers) {

        txBuilder.setAuthorizers(authorizers);

        return this;
    }


    public FlowTransaction build() {
        return this.txBuilder.build();
    }
}
