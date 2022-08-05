package io.github.allegiaco.flowjavaservice.abstraction;

import com.google.protobuf.ByteString;
import com.nftco.flow.sdk.*;
import com.nftco.flow.sdk.crypto.Crypto;
import com.nftco.flow.sdk.crypto.PrivateKey;
import io.github.allegiaco.flowjavaservice.builders.FlowTransactionBuilder;
import io.github.allegiaco.flowjavaservice.builders.ProposalKeyBuilder;
import io.github.allegiaco.flowjavaservice.exceptions.ImportsException;
import io.github.allegiaco.flowjavaservice.exceptions.TransactionException;
import io.github.allegiaco.flowjavaservice.handlers.FlowServiceUtilsHandler;
import io.github.allegiaco.flowjavaservice.model.FlowTransactionObject;
import io.github.allegiaco.flowjavaservice.model.User;
import io.github.allegiaco.flowjavaservice.model.enums.NetType;
import io.github.allegiaco.flowjavaservice.reader.ReusableBufferedReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FlowServiceAbstract {

    protected FlowServiceUtilsAbstract flowServiceUtils;
    protected FlowTransactionObject flowTransactionObject;
    protected List<User> users = new ArrayList<>();
    protected ReusableBufferedReader reusableBufferedReader;

    public FlowServiceAbstract(String payerPrivateKey, String payerFlowAddress, ReusableBufferedReader rbr, NetType netType) {
        this.flowServiceUtils = FlowServiceUtilsHandler.getFlowServiceUtils(netType);
        this.reusableBufferedReader = rbr;
        users.add(new User(payerFlowAddress, payerPrivateKey));
    }

    public FlowServiceAbstract(String payerPrivateKey, String payerFlowAddress, ReusableBufferedReader rbr, FlowServiceUtilsAbstract flowService) {
        this.flowServiceUtils = flowService;
        this.reusableBufferedReader = rbr;
        users.add(new User(payerFlowAddress, payerPrivateKey));
    }

    public FlowServiceAbstract(String payerPrivateKey, String payerFlowAddress, NetType netType) {
        this.flowServiceUtils = FlowServiceUtilsHandler.getFlowServiceUtils(netType);
        users.add(new User(payerFlowAddress, payerPrivateKey));
    }

    public boolean setUser(User user) {
        if (user.getUserFlowAddress().getBase16Value().length() == 16 && user.getUserPrivateKey().getHex().length() == 64) {
            this.users.add(user);
            return true;
        }
        return false;
    }

    public Optional<User> getUser(FlowAddress userFlowAddress) {

        return this.users.stream()
                .filter(user -> user.getUserFlowAddress().equals(userFlowAddress))
                .findFirst();
    }

    public boolean checkIfUserIsPresent(User user) {

        return this.users.stream()
                .filter(u -> u.equals(user))
                .findFirst().isPresent();
    }

    public abstract FlowScriptResponse executeScriptWithChanges(String script, List<FlowArgument> argumentsList,
                                                                Map<String, String> scriptChanges);

    public FlowScriptResponse executeScript(String script, List<FlowArgument> argumentsList) {

        Map<String, String> scriptChanges = new HashMap<>();

        FlowScript flowScript = null;
        try {
            flowScript = new FlowScript(loadScript(script, scriptChanges));
        } catch (ImportsException e) {
            e.printStackTrace();
        }
        List<ByteString> arList = argumentsList.stream()
                .map(flowArgument -> flowArgument.getByteStringValue())
                .collect(Collectors.toList());

        return this.flowServiceUtils.accessAPI().executeScriptAtLatestBlock(flowScript, arList);

    }

    public abstract FlowAccount getAccount(FlowAddress address);

    public FlowServiceAbstract prepareTransaction() {
        this.flowTransactionObject = new FlowTransactionObject();
        return this;
    }

    public FlowServiceAbstract addTransactionText(String transaction) {
        this.flowTransactionObject.setTransaction(transaction);
        return this;
    }

    public FlowServiceAbstract addArgumentsList(List<FlowArgument> argumentsList) {
        this.flowTransactionObject.setArgumentsList(argumentsList);
        return this;
    }

    public FlowServiceAbstract addScriptChanges(Map<String, String> scriptChanges) {
        this.flowTransactionObject.setScriptChanges(scriptChanges);
        return this;
    }

    public FlowServiceAbstract addProposerAddress(FlowAddress proposerAddress) {
        this.flowTransactionObject.setProposerAddress(proposerAddress);
        return this;
    }

    public FlowServiceAbstract addAuthorizers(List<FlowAddress> authorizers) {
        this.flowTransactionObject.setAuthorizers(authorizers);
        return this;
    }

    public FlowServiceAbstract addPayerAddress(FlowAddress payerAddress) {
        this.flowTransactionObject.setPayerAddress(payerAddress);
        return this;
    }

    public FlowServiceAbstract setSkipSeal(boolean skipSeal) {
        this.flowTransactionObject.setSkipSeal(skipSeal);
        return this;
    }

    public abstract FlowId sendTx(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                  FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal) throws TransactionException;

    public FlowId sendTx() throws TransactionException {

        if (!this.flowTransactionObject.isTransactionReady()) {
            throw new TransactionException("Missing fields in the Transaction", FlowTransactionObject.class);
        }

        Optional<User> payerUser = this.users.stream()
                .filter(user -> user.getUserFlowAddress().equals(this.flowTransactionObject.getPayerAddress()))
                .findFirst();

        if(payerUser.isEmpty()) {
            throw new TransactionException("User that needs to pay is not registered with the Private Key", User.class);
        }

        FlowAccountKey payerAccountKey = this.flowServiceUtils.getAccountKey(this.flowTransactionObject.getPayerAddress(), 0);
        FlowAccountKey proposerAccountKey = this.flowServiceUtils.getAccountKey(this.flowTransactionObject.getProposerAddress(), 0);

        FlowTransaction trx = null;
        try {
            trx = new FlowTransactionBuilder()
                    .addScript(new FlowScript(loadScript(this.flowTransactionObject.getTransaction(), this.flowTransactionObject.getScriptChanges())))
                    .addArgumentsList(this.flowTransactionObject.getArgumentsList())
                    .setReferenceBlockId(this.flowServiceUtils.getLatestBlockID()).setGasLimit(9999L)
                    .setProposalKey(new ProposalKeyBuilder().setAddress(this.flowTransactionObject.getProposerAddress())
                            .setKeyIndex(proposerAccountKey.getId())
                            .setSequenceNumber(proposerAccountKey.getSequenceNumber())
                            .build())
                    .setPayerAddress(this.flowTransactionObject.getPayerAddress())
                    .setAuthorizers(this.flowTransactionObject.getAuthorizers())
                    .build();
        } catch (ImportsException e) {
            e.printStackTrace();
        }

        PrivateKey payerPrivateKey = payerUser.get().getUserPrivateKey();

        Signer signer = Crypto.getSigner(payerPrivateKey, payerAccountKey.getHashAlgo());

        trx = trx.addEnvelopeSignature(this.flowTransactionObject.getPayerAddress(), payerAccountKey.getId(), signer);

        FlowId txID = this.flowServiceUtils.accessAPI().sendTransaction(trx);

        if (this.flowTransactionObject.getSkipSeal())
            return txID;

        this.flowServiceUtils.waitForSeal(txID);
        this.flowTransactionObject = null;
        return txID;
    }

    protected byte[] loadScript(String scriptPath, Map<String, String> scriptChanges) throws ImportsException {

        File scriptFile = new File(scriptPath);
        StringBuilder sb = new StringBuilder();

        List<String> imports = new ArrayList<>();
        List<String> code = new ArrayList<>();
        String line;
        int numberOfImports = scriptChanges.size();

        if(this.reusableBufferedReader != null) {

            System.out.println("I'm using the reusable reader");
            try {
                reusableBufferedReader.setSource(new FileReader(scriptFile));
                do {
                    line = reusableBufferedReader.readLine();
                    if (line != null) {
                        if(line.contains("import") && line.contains("from")) {
                            imports.add(line);
                        } else {
                            code.add(line);
                        }
                    }

                } while (line != null);
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
                try {
                    reusableBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {

            System.out.println("I'm using the standard buffered reader");
            BufferedReader reader = null;
            try {
                reader = new BufferedReader( new FileReader(scriptFile));
                do {
                    line = reader.readLine();
                    if (line != null) {
                        if(line.contains("import") && line.contains("from")) {
                            imports.add(line);
                        } else {
                            code.add(line);
                        }
                    }

                } while (line != null);
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

            // CHECK IF THE IMPORTS CORRESPOND
            if (numberOfImports != 0) {
                if (imports.size() != numberOfImports) {
                    throw new ImportsException("Imports mismatch, check if you are using the correct script");
                }

                // CHANGE THE VALUES OF THE IMPORT
                imports = imports.stream().map(i -> {
                    for (Map.Entry<String, String> change : scriptChanges.entrySet()) {
                        if (i.contains(change.getKey())) {
                            i = i.replace(change.getKey(), change.getValue());
                            scriptChanges.remove(change.getKey());
                            return i;
                        }
                    }
                    return i;
                }).collect(Collectors.toList());

                if (scriptChanges.size() != 0) {
                    throw new ImportsException("Imports doesn't match");
                }
            }
            if (!imports.isEmpty()) {
                imports.forEach(im -> sb.append(im).append("\n"));
            }
            code.forEach(co -> sb.append(co).append("\n"));

        return sb.toString().getBytes();
    }

}
