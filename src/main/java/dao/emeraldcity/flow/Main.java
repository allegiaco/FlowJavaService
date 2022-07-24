package dao.emeraldcity.flow;

import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.cadence.Field;
import dao.emeraldcity.flow.abstraction.FlowServiceAbstract;
import dao.emeraldcity.flow.builders.ArgumentsBuilder;
import dao.emeraldcity.flow.exceptions.TransactionException;
import dao.emeraldcity.flow.implementation.FlowService;
import dao.emeraldcity.flow.model.User;
import dao.emeraldcity.flow.model.enums.NetType;
import dao.emeraldcity.flow.reader.ReusableBufferedReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) {

        User user = new User.Builder()
                            .userFlowAddress("0xd34e6d685806bcd1")
                            .userPrivateKey("7d5fd2648ff31d16826774fe95b026e32309642679f84ca6606ffe308ec43f93")
                            .userDescription("abc")
                            .build();

        System.out.println(user.getUserPrivateKey().getHex());
        System.out.println(user.getUserFlowAddress().getBase16Value());
        System.out.println(user.getUserFlowAddress().getFormatted());


        FlowService flowService = new FlowService.Builder(NetType.TESTNET)
                                                .payerPrivateKey("b7bedcc776d6e7d5ae3162dfe988fa91ec170db33f436fa306aa19315899c75f")
                                                .payerFlowAddress("0x0042e6f28d52f7d7")
                                                .reusableReader(new ReusableBufferedReader())
                                                .build();


        flowService.setUser(user);
        flowService.checkIfUserIsPresent(user);

        String script = "./src/get_collection_ids.cdc";

        String nonFungibleTokenPath = "\"../../contracts/NonFungibleToken.cdc\"";
        String kittyItemsPath = "\"../../contracts/KittyItems.cdc\"";
        String nonFungibleTokenAddress = "631e88ae7f1d7c20";
        String kittyItemsAddress = "0042e6f28d52f7d7";

        Map<String, String> scriptChanges = new HashMap<String, String>();
        scriptChanges.put(nonFungibleTokenPath, "0x".concat(nonFungibleTokenAddress));
        scriptChanges.put(kittyItemsPath, "0x".concat(kittyItemsAddress));
        String account = "0042e6f28d52f7d7";

        var argumentsList = new ArgumentsBuilder()
                .argumentField("AddressField", account)
                .build();

        var response = flowService.executeScriptWithChanges(script, argumentsList, scriptChanges);

        var t = (Field<?>[]) response.getJsonCadence().getValue();
        List.of(t).forEach(f -> System.out.println(f.getValue()));

        System.out.println(script);

        try {
           FlowId flowId = flowService.prepareTransaction()
                                      .addTransactionText("abc")
                                      .addArgumentsList(new ArgumentsBuilder().argumentField("AddressField", "abc")
                                                                              .build())
                                      .addScriptChanges(new HashMap<>())
                                      .addProposerAddress(new FlowAddress("Ox098..."))
                                      .addPayerAddress(new FlowAddress("Ox098..."))
                                      .addAuthorizers(List.of(new FlowAddress("0x786...")))
                                      .setSkipSeal(true)
                                      .sendTx();
        } catch (TransactionException e) {
            e.printStackTrace();
        }

       /* FlowTransactionResult transactionResult = flowService.getTransactionResult(new FlowId("3a18d2d68df91f1fd21a43ec26dffce0c2b3a24629fc53c9ccace91d935c8c49"));

        var a = flowService.returnTransactionValues(transactionResult);*/
        FlowAddress addressMainNet = new FlowAddress("bd434b9547d84aaa");

        /*FlowAccount account2 = flowService2.getAccount(addressMainNet);
        System.out.println(account2.getBalance());
        System.out.println();

        account2.getKeys().forEach(key -> System.out.println(key.getId() + " " + key.getSequenceNumber() + " " + key.getWeight() + " " + key.getPublicKey().getBase16Value()));*/
        /*a.forEach(mp -> {
            System.out.println("********");
            mp.entrySet().stream().forEach(es -> {
                System.out.println(es.getKey() + "  " + es.getValue().getValue().getValue());
                System.out.println();
            });
        });*/


    }
}
