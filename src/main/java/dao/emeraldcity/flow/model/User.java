package dao.emeraldcity.flow.model;

import com.nftco.flow.sdk.FlowAddress;
import com.nftco.flow.sdk.crypto.Crypto;
import com.nftco.flow.sdk.crypto.PrivateKey;

import java.util.Objects;

public class User {

    private String description;
    private FlowAddress userFlowAddress;
    private PrivateKey userPrivateKey;


    public User(String userFlowAddress, String userPrivateKey) {
        this.userFlowAddress = new FlowAddress(userFlowAddress);
        this.userPrivateKey = Crypto.decodePrivateKey(userPrivateKey);
    }

    public User(String description, FlowAddress userFlowAddress, PrivateKey userPrivateKey) {
        this.description = description;
        this.userFlowAddress = userFlowAddress;
        this.userPrivateKey = userPrivateKey;
    }

    public FlowAddress getUserFlowAddress() {
        return this.userFlowAddress;
    }

    public void setUserFlowAddress(String userFlowAddress) {
        this.userFlowAddress = new FlowAddress(userFlowAddress);
    }

    public PrivateKey getUserPrivateKey() {
        return userPrivateKey;
    }

    public void setUserPrivateKey(PrivateKey userPrivateKey) {
        this.userPrivateKey = userPrivateKey;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.userFlowAddress, user.userFlowAddress) && Objects.equals(this.userPrivateKey, user.userPrivateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userFlowAddress, userPrivateKey);
    }
}
