package dao.emeraldcity.flow.implementation;

import com.google.protobuf.ByteString;
import com.nftco.flow.sdk.*;
import com.nftco.flow.sdk.crypto.Crypto;
import com.nftco.flow.sdk.crypto.PrivateKey;
import dao.emeraldcity.flow.abstraction.FlowServiceAbstract;
import dao.emeraldcity.flow.builders.FlowTransactionBuilder;
import dao.emeraldcity.flow.builders.ProposalKeyBuilder;
import dao.emeraldcity.flow.exceptions.ImportsException;
import dao.emeraldcity.flow.exceptions.TransactionException;
import dao.emeraldcity.flow.model.enums.NetType;
import dao.emeraldcity.flow.model.User;
import dao.emeraldcity.flow.reader.ReusableBufferedReader;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


public class FlowServiceImpl extends FlowServiceAbstract {

    public FlowServiceImpl(String payerPrivateKey, String payerFlowAddress, ReusableBufferedReader rbr, NetType netType) {
        super(payerPrivateKey, payerFlowAddress, rbr, netType);
    }

    public FlowAccount getAccount(FlowAddress address) {
        return this.flowServiceUtils.getAccessAPI().getAccountAtLatestBlock(address);
    }

    public BigDecimal getAccountBalance(FlowAddress address) {
        FlowAccount account = this.flowServiceUtils.getAccount(address);
        return account.getBalance();
    }

    public FlowId sendTx(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal)
            throws TransactionException {

        Optional<User> payerUser = this.users.stream()
                .filter(user -> user.getUserFlowAddress().equals(payerAddress))
                .findFirst();

        if(payerUser.isEmpty()) {
            throw new TransactionException("User that needs to pay is not registered with the Private Key", User.class);
        }

        FlowAccountKey payerAccountKey = this.flowServiceUtils.getAccountKey(payerAddress, 0);
        FlowAccountKey proposerAccountKey = this.flowServiceUtils.getAccountKey(proposerAddress, 0);

        FlowTransaction trx = null;
        try {
            trx = new FlowTransactionBuilder()
                    .addScript(new FlowScript(loadScript(transaction, scriptChanges)))
                    .addArgumentsList(argumentsList)
                    .setReferenceBlockId(this.flowServiceUtils.getLatestBlockID()).setGasLimit(9999L)
                    .setProposalKey(new ProposalKeyBuilder().setAddress(proposerAddress)
                                                            .setKeyIndex(proposerAccountKey.getId())
                                                            .setSequenceNumber(proposerAccountKey.getSequenceNumber())
                                                            .build())
                    .setPayerAddress(payerAddress)
                    .setAuthorizers(authorizers)
                    .build();
        } catch (ImportsException e) {
            e.printStackTrace();
        }

        PrivateKey payerPrivateKey = payerUser.get().getUserPrivateKey();

        Signer signer = Crypto.getSigner(payerPrivateKey, payerAccountKey.getHashAlgo());

        trx = trx.addEnvelopeSignature(payerAddress, payerAccountKey.getId(), signer);

        FlowId txID = this.flowServiceUtils.getAccessAPI().sendTransaction(trx);

        if (skipSeal)
            return txID;

        this.flowServiceUtils.waitForSeal(txID);
        return txID;
    }

    public FlowScriptResponse executeScript(String script, List<FlowArgument> argumentsList,
                                            Map<String, String> scriptChanges) {

        FlowScript flowScript = null;
        try {
          flowScript = new FlowScript(loadScript(script, scriptChanges));
        } catch (ImportsException e) {
            e.printStackTrace();
        }
        List<ByteString> arList = argumentsList.stream()
                                               .map(flowArgument -> flowArgument.getByteStringValue())
                                               .collect(Collectors.toList());

        FlowScriptResponse response = this.flowServiceUtils.getAccessAPI().executeScriptAtLatestBlock(flowScript, arList);
        return response;

    }

    public List<FlowEventPayload> getEventPayloadsFromTransactionResults (FlowId txID) {

        FlowTransactionResult flowTransactionResult = this.flowServiceUtils.getAccessAPI().getTransactionResultById(txID);

        return flowTransactionResult.getEvents().stream()
                .map(event -> event.getPayload())
                .collect(Collectors.toList());

    }
}
