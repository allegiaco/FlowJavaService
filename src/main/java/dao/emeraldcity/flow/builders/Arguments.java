package dao.emeraldcity.flow.builders;

import com.nftco.flow.sdk.FlowArgument;
import com.nftco.flow.sdk.cadence.Field;
import dao.emeraldcity.flow.exceptions.ArgumentNotFoundException;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Arguments {

    private final List<FlowArgument> listArguments;

    public List<FlowArgument> getArguments() {
        return listArguments;
    }

    public static class Builder {

        private List<FlowArgument> listArguments;

        public List<FlowArgument> getListArguments() {
            return listArguments;
        }

        public Builder() {
            this.listArguments = new ArrayList<>();
        }

        private <T> T castObject(Class<T> clazz, Object object) {
            return (T) object;
        }

        public Builder argumentField(String className, Object argument) {

            try {
                Class<?> clazz = Class.forName("com.nftco.flow.sdk.cadence." + className);
                Constructor<?> cnstr = ConstructorUtils
                        .getMatchingAccessibleConstructor(clazz, argument.getClass());
                if(cnstr == null) {
                    throw new ArgumentNotFoundException("Not possible to create " + className);
                } else {
                    var obj = cnstr.newInstance(argument);
                    var castedArgument = (Field<?>) castObject(clazz, obj);
                    listArguments.add(new FlowArgument(castedArgument));
                }

            } catch (ClassNotFoundException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ArgumentNotFoundException e) {
                e.printStackTrace();
            }

            return this;
        }

        public Arguments build() {
            return new Arguments(this);
        }

    }

    private Arguments(Builder builder) {
        this.listArguments = builder.listArguments;
    }

}
