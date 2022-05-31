package dao.emeraldcity.flow.builders;

import com.nftco.flow.sdk.FlowArgument;
import com.nftco.flow.sdk.cadence.Field;
import dao.emeraldcity.flow.exceptions.ArgumentNotFoundException;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ArgumentsBuilder {

    private List<FlowArgument> listArguments;

    public ArgumentsBuilder() {
        this.listArguments = new ArrayList<>();
    }

    private <T> T castObject(Class<T> clazz, Object object) {
        return (T) object;
    }

    public ArgumentsBuilder argumentField(String className, Object argument) {

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
            e.printStackTrace();;
        }

        return this;
    }

    public List<FlowArgument> build() {
        return this.listArguments;
    }

    public ArgumentsBuilder clear() {
        this.listArguments.clear();
        return this;
    }
}
