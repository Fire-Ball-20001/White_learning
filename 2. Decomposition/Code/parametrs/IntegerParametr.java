

public class IntegerParametr extends ParametrBasic{


    private Integer maxValue = Integer.MAX_VALUE;
    private Integer minValue = Integer.MIN_VALUE;

    public IntegerParametr(CommandBuilder builder)
    {
        super(builder);
    }
    public IntegerParametr(CommandBuilder builder, String name_value)
    {
        super(builder,name_value);
    }

    @Override
    public boolean init(ICommandSender sender, String value) {
        if(!super.init(sender,value)) return false;
        int number = 0;
        try
        {
            number = Integer.parseInt(value);
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

    public IntegerParametr setMaxValue(Integer value)
    {
        maxValue = value;
        return this;
    }

    public IntegerParametr setMinValue(Integer value)
    {
        minValue = value;
        return this;
    }

    public IntegerParametr setOptional(boolean new_stat)
    {
        super.setOptional(new_stat);
        return this;
    }

    public IntegerParametr setDependent(boolean new_stat)
    {
        super.setDependent(new_stat);
        return this;
    }
    public IntegerParametr setErrorKeyMessage(String key)
    {
        super.setErrorKeyMessage(key);
        return this;
    }

    public IntegerParametr setName(String name_parametr)
    {
        super.setName(name_parametr);
        return this;
    }

    public IntegerParametr setPerm(String perm)
    {
        super.setPerm(perm);
        return this;
    }


}
