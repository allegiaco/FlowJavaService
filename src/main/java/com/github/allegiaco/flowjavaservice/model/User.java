package com.github.allegiaco.flowjavaservice.model;

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

    public User (Builder builder) {
        this.description = builder.description;
        this.userFlowAddress = builder.userFlowAddress;
        this.userPrivateKey = builder.userPrivateKey;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public static class Builder {

        private String description;
        private FlowAddress userFlowAddress;
        private PrivateKey userPrivateKey;

        public Builder() {

        }

        public Builder userFlowAddress(String userFlowAddress) {
            this.userFlowAddress = new FlowAddress(userFlowAddress);
            return this;
        }

        public Builder userPrivateKey(String userPrivateKey) {
            this.userPrivateKey = Crypto.decodePrivateKey(userPrivateKey);
            return this;
        }

        public Builder userDescription(String userDescription) {
            this.description = userDescription;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}

