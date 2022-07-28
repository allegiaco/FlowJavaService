package dao.emeraldcity.flow.builders;

import com.nftco.flow.sdk.FlowArgument;
import com.nftco.flow.sdk.cadence.*;
import dao.emeraldcity.flow.exceptions.ArgumentNotFoundException;
import dao.emeraldcity.flow.exceptions.NotANumberFieldClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ArgumentsBuilder {

    private List<FlowArgument> listArguments;

    public ArgumentsBuilder() {
        this.listArguments = new ArrayList<>();
    }

    public ArgumentsBuilder voidField() {
        listArguments.add(new FlowArgument(new VoidField()));
        return this;
    }

    public ArgumentsBuilder optionalField(Field<?> field) {
        this.listArguments.add(new FlowArgument(new OptionalField(field)));
        return this;
    }

    public ArgumentsBuilder booleanField (boolean bool) {
        this.listArguments.add(new FlowArgument(new BooleanField(bool)));
        return this;
    }

    public ArgumentsBuilder stringField (String str) {
        this.listArguments.add(new FlowArgument(new StringField(str)));
        return this;
    }

    public ArgumentsBuilder addressField (String addressField) {
        this.listArguments.add(new FlowArgument(new AddressField(addressField)));
        return this;
    }

    public ArgumentsBuilder numberField (String numberField, String numberValue) throws ArgumentNotFoundException {

        try {

            if(!numberField.contains("Number") || numberField.equals("NumberField")) {
                throw new NotANumberFieldClassException("Class inserted is not a NumberField");
            }

            Class<?> clazz = Class.forName("com.nftco.flow.sdk.cadence." + numberField);
            var constructor = clazz.getConstructor(String.class);
            var number = (Field<?>) constructor.newInstance(numberValue);
            this.listArguments.add(new FlowArgument(number));
            return this;

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | NotANumberFieldClassException e) {
            throw new ArgumentNotFoundException(e.getMessage());
        }
        }

    public ArgumentsBuilder arrayField(Field[] fields) {
        listArguments.add(new FlowArgument(new ArrayField(fields)));
        return this;
    }

    public ArgumentsBuilder dictionaryField(DictionaryFieldEntry[] dictionaryFieldEntries) {
        this.listArguments.add(new FlowArgument(new DictionaryField(dictionaryFieldEntries)));
        return this;
    }

    public ArgumentsBuilder pathField (PathValue pathValue) {
        this.listArguments.add(new FlowArgument(new PathField(pathValue)));
        return this;
    }

    public ArgumentsBuilder capabilityField (CapabilityValue capabilityValue) {
        this.listArguments.add(new FlowArgument(new CapabilityField(capabilityValue)));
        return this;
    }

    public ArgumentsBuilder compositeField (String type, CompositeValue compositeValue) {
        this.listArguments.add(new FlowArgument(new CompositeField(type, compositeValue)));
        return this;
    }

    public ArgumentsBuilder structField (CompositeValue compositeValue) {
        this.listArguments.add(new FlowArgument(new StructField(compositeValue)));
        return this;
    }

    public ArgumentsBuilder resourceField (CompositeValue compositeValue) {
        this.listArguments.add(new FlowArgument(new ResourceField(compositeValue)));
        return this;
    }

    public ArgumentsBuilder eventField (CompositeValue compositeValue) {
        this.listArguments.add(new FlowArgument(new EventField(compositeValue)));
        return this;
    }

    public ArgumentsBuilder contractField (CompositeValue compositeValue) {
        this.listArguments.add(new FlowArgument(new ContractField(compositeValue)));
        return this;
    }

    public ArgumentsBuilder enumField (CompositeValue compositeValue) {
        this.listArguments.add(new FlowArgument(new EnumField(compositeValue)));
        return this;
    }

    public ArgumentsBuilder typeField (TypeValue typeValue) {
        this.listArguments.add(new FlowArgument(new TypeField(typeValue)));
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
