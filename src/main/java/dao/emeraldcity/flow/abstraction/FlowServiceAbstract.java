package dao.emeraldcity.flow.abstraction;

import com.nftco.flow.sdk.*;
import com.nftco.flow.sdk.crypto.Crypto;
import com.nftco.flow.sdk.crypto.PrivateKey;
import dao.emeraldcity.flow.builders.FlowTransactionBuilder;
import dao.emeraldcity.flow.builders.ProposalKeyBuilder;
import dao.emeraldcity.flow.exceptions.ImportsException;
import dao.emeraldcity.flow.exceptions.TransactionException;
import dao.emeraldcity.flow.handlers.FlowServiceUtilsHandler;
import dao.emeraldcity.flow.model.FlowTransactionObject;
import dao.emeraldcity.flow.model.User;
import dao.emeraldcity.flow.model.enums.NetType;
import dao.emeraldcity.flow.reader.ReusableBufferedReader;
import dao.emeraldcity.flow.utils.FlowServiceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class FlowServiceAbstract {

    protected FlowServiceUtils flowServiceUtils;
    protected ReusableBufferedReader reader;
    protected FlowTransactionObject flowTransactionObject;
    protected List<User> users = new ArrayList<>();

    public FlowServiceAbstract(String payerPrivateKey, String payerFlowAddress, ReusableBufferedReader rbr, NetType netType) {
        this.flowServiceUtils = FlowServiceUtilsHandler.getFlowServiceUtils(netType);
        this.reader = rbr;
        users.add(new User(payerFlowAddress, payerPrivateKey));
    }

    public boolean setUser(User user) {
        if (user.getUserFlowAddress().getBase16Value().length() == 16 && user.getUserPrivateKey().getHex().length() == 64) {
            this.users.add(user);
            return true;
        }
        return false;
    }

    public abstract FlowScriptResponse executeScript(String script, List<FlowArgument> argumentsList,
                                            Map<String, String> scriptChanges);

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

        try {
            reader.setSource(new FileReader(scriptFile));
            List<String> imports = new ArrayList<>();
            List<String> code = new ArrayList<>();
            String line;
            int numberOfImports = scriptChanges.size();

            // READ THE WHOLE CONTRACT AND DIVIDE IMPORTS FROM CODE

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

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().getBytes();
    }
}
