package dao.emeraldcity.flow.abstraction;

import com.nftco.flow.sdk.*;
import dao.emeraldcity.flow.exceptions.ImportsException;
import dao.emeraldcity.flow.exceptions.TransactionException;
import dao.emeraldcity.flow.handlers.FlowServiceUtilsHandler;
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
import java.util.stream.Collectors;

public abstract class FlowServiceAbstract {

    protected FlowServiceUtils flowServiceUtils;
    protected ReusableBufferedReader reader;
    protected List<User> users = new ArrayList<>();

    public FlowServiceAbstract(String payerPrivateKey, String payerFlowAddress, ReusableBufferedReader rbr, NetType netType) {
        this.flowServiceUtils = FlowServiceUtilsHandler.getFlowServiceUtils(netType);
        this.reader = reader;
        users.add(new User(payerFlowAddress, payerPrivateKey));
    }

    public boolean setUser(User user) {
        if (user.getUserFlowAddress().getBase16Value().length() == 16 && user.getUserPrivateKey().getHex().length() == 64) {
            this.users.add(user);
            return true;
        }
        return false;
    }

    public abstract FlowId sendTx(String transaction, List<FlowArgument> argumentsList, Map<String, String> scriptChanges,
                                  FlowAddress proposerAddress, List<FlowAddress> authorizers, FlowAddress payerAddress, boolean skipSeal) throws TransactionException;

    public abstract FlowScriptResponse executeScript(String script, List<FlowArgument> argumentsList,
                                            Map<String, String> scriptChanges);

    public abstract FlowAccount getAccount(FlowAddress address);

    protected byte[] loadScript(String scriptPath, Map<String, String> scriptChanges) throws ImportsException {

        File scriptFile = new File(scriptPath);
        StringBuilder sb = new StringBuilder();

        try {
            reader.setSource(new FileReader(scriptFile));
            List<String> imports = new ArrayList<>();
            List<String> code = new ArrayList<>();
            String line = "";
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
                imports.forEach(im -> sb.append(im + "\n"));
            }
            code.forEach(co -> sb.append(co + "\n"));

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
