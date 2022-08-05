package io.github.allegiaco.flowjavaservice.builders;

import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowTransactionProposalKey;
import com.nftco.flow.sdk.FlowTransactionProposalKeyBuilder;

public class ProposalKeyBuilder {

    private FlowTransactionProposalKeyBuilder pkb;

    public ProposalKeyBuilder() {
        this.pkb = new FlowTransactionProposalKeyBuilder();
    }

    public ProposalKeyBuilder setAddress(FlowAddress address) {
        pkb.setAddress(address);
        return this;
    }

    public ProposalKeyBuilder setKeyIndex(Number value) {
        pkb.setKeyIndex(value);
        return this;
    }

    public ProposalKeyBuilder setSequenceNumber(Number value) {
        pkb.setSequenceNumber(value);
        return this;
    }

    public FlowTransactionProposalKey build() {
        return this.pkb.build();
    }
}
