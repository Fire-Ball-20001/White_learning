

public class StringParametr extends ParametrBasic{
    private List<String> potentialValues = new ArrayList<>();

    public StringParametr(CommandBuilder build)
    {
        super(build);
    }
    public StringParametr(CommandBuilder build, String name_value)
    {
        super(build,name_value);

    }

    @Override
    public boolean init(ICommandSender sender, String value) {
        if(!super.init(sender,value)) return false;
        if(potentialValues.isEmpty())
        {
            this.value = value;
        }
        else
        {
            boolean check = false;
            for(String word : potentialValues)
            {
                if(value.equals(word))
                {
                    check = true;
                    this.value = value;
                    break;
                }
            }
            if(!check)
            {
                printError(sender,value);
                return false;
            }
        }
        return true;
    }

    public StringParametr setPotentialValues(String[] values)
    {
        potentialValues = Arrays.asList(values);
        return this;
    }
    public List<String> getPotentialValues()
    {
        return potentialValues;
    }

    public StringParametr setOptional(boolean new_stat)
    {
        super.setOptional(new_stat);
        return this;
    }

    public StringParametr setDependent(boolean new_stat)
    {
        super.setDependent(new_stat);
        return this;
    }

    public StringParametr setErrorKeyMessage(String key)
    {
        super.setErrorKeyMessage(key);
        return this;
    }

    public StringParametr setName(String name_parametr)
    {
        super.setName(name_parametr);
        return this;
    }

    public StringParametr setPerm(String perm)
    {
        super.setPerm(perm);
        return this;
    }

}
