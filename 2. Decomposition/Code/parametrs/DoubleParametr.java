

public class DoubleParametr extends ParametrBasic{

    private Double maxValue = Double.MAX_VALUE;
    private Double minValue = Double.MIN_VALUE;

    public DoubleParametr(CommandBuilder builder)
    {
        super(builder);
    }

    public DoubleParametr(CommandBuilder builder, String name_value)
    {
        super(builder,name_value);
    }

    @Override
    public boolean init(ICommandSender sender, String value) {
        if(!super.init(sender,value)) return false;
        double number = 0;
        try
        {
            number = Double.parseDouble(value);
        }
        catch (Exception e)
        {
            if(!isOptional()) {
                printError(sender, value);
            }
            return false;
        }
        if(number>maxValue || number<minValue)
        {
            printError(sender,value);
            return false;
        }
        super.value = number;
        return true;
    }

    public DoubleParametr setMaxValue(Double value)
    {
        maxValue = value;
        return this;
    }

    public DoubleParametr setMinValue(Double value)
    {
        minValue = value;
        return this;
    }

    public DoubleParametr setOptional(boolean new_stat)
    {
        super.setOptional(new_stat);
        return this;
    }

    public DoubleParametr setDependent(boolean new_stat)
    {
        super.setDependent(new_stat);
        return this;
    }
    public DoubleParametr setErrorKeyMessage(String key)
    {
        super.setErrorKeyMessage(key);
        return this;
    }

    public DoubleParametr setName(String name_parametr)
    {
        super.setName(name_parametr);
        return this;
    }
    public DoubleParametr setPerm(String perm)
    {
        super.setPerm(perm);
        return this;
    }
}
