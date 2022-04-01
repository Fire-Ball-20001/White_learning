

public class BoolParametr extends ParametrBasic{
    private boolean useLogicConst = true;
    private List<String> trueAliases = new ArrayList<>();
    private List<String> falseAliases = new ArrayList<>();


    public BoolParametr(CommandBuilder builder)
    {
        super(builder);
    }

    public BoolParametr(CommandBuilder builder, String name_value)
    {
        super(builder,name_value);
    }

    @Override
    public boolean init(ICommandSender sender, String value) {
        if(!super.init(sender,value)) return false;
        if(trueAliases.isEmpty() && !useLogicConst && falseAliases.isEmpty())
        {
            throw new NoSuchElementException("There is no argument alias!");
        }
        if(useLogicConst)
        {
            trueAliases.add("true");
            falseAliases.add("false");
        }
        for(String alias : trueAliases)
        {
            if(alias.equals(value))
            {
                super.value = true;
                return true;
            }
        }
        for(String alias : falseAliases)
        {
            if(alias.equals(value))
            {
                super.value = false;
                return true;
            }
        }
        if(!isOptional()) {
            printError(sender, value);
        }
        return false;
    }

    public BoolParametr setTrueAliases(String[] values)
    {
        trueAliases = Arrays.asList(values);
        return this;
    }

    public BoolParametr setFalseAliases(String[] values)
    {
        falseAliases = Arrays.asList(values);
        return this;
    }
    public List<String> getTrueAliases()
    {
        return trueAliases;
    }

    public List<String> getFalseAliases()
    {
        return falseAliases;
    }

    public BoolParametr setUseLogicConst(boolean new_state)
    {
        useLogicConst = new_state;
        return this;
    }

    public BoolParametr setOptional(boolean new_stat)
    {
        super.setOptional(new_stat);
        return this;
    }

    public BoolParametr setDependent(boolean new_stat)
    {
        super.setDependent(new_stat);
        return this;
    }
    public BoolParametr setErrorKeyMessage(String key)
    {
        super.setErrorKeyMessage(key);
        return this;
    }

    public BoolParametr setName(String name_parametr)
    {
        super.setName(name_parametr);
        return this;
    }

    public BoolParametr setPerm(String perm)
    {
        super.setPerm(perm);
        return this;
    }
}
