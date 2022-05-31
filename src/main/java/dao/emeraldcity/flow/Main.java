package dao.emeraldcity.flow;

import com.nftco.flow.sdk.FlowAccount;
import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.FlowId;
import com.nftco.flow.sdk.FlowTransactionResult;
import dao.emeraldcity.flow.abstraction.FlowServiceAbstract;
import dao.emeraldcity.flow.implementation.FlowServiceImpl;
import dao.emeraldcity.flow.model.User;
import dao.emeraldcity.flow.model.enums.NetType;
import dao.emeraldcity.flow.reader.ReusableBufferedReader;


public class Main {

    public static void main(String[] args) {
        User user = new User("0xd34e6d685806bcd1", "7d5fd2648ff31d16826774fe95b026e32309642679f84ca6606ffe308ec43f93");

        System.out.println(user.getUserPrivateKey().getHex());
        System.out.println(user.getUserFlowAddress().getBase16Value());
        System.out.println(user.getUserFlowAddress().getFormatted());

        FlowServiceAbstract flowService = new FlowServiceImpl("b7bedcc776d6e7d5ae3162dfe988fa91ec170db33f436fa306aa19315899c75f", "0x0042e6f28d52f7d7", new ReusableBufferedReader(), NetType.MAINNET);

       /* FlowTransactionResult transactionResult = flowService.getTransactionResult(new FlowId("3a18d2d68df91f1fd21a43ec26dffce0c2b3a24629fc53c9ccace91d935c8c49"));

        var a = flowService.returnTransactionValues(transactionResult);*/
        FlowAddress addressMainNet = new FlowAddress("bd434b9547d84aaa");

        FlowAccount account = flowService.getAccount(addressMainNet);

        account.getKeys().forEach(key -> System.out.println(key.getId() + " " + key.getSequenceNumber() + " " + key.getWeight() + " " + key.getPublicKey().getBase16Value()));
        /*a.forEach(mp -> {
            System.out.println("********");
            mp.entrySet().stream().forEach(es -> {
                System.out.println(es.getKey() + "  " + es.getValue().getValue().getValue());
                System.out.println();
            });
        });*/


    }
}
