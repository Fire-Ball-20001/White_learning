



public class ParametrBasic implements IParametr {
    private boolean optional =false;
    protected Object value = null;
    private String name_value = "";
    private CommandBuilder builder = null;
    protected String errorKeyMessage = "";
    private boolean dependentParam = false;
    private String perm = "";


    public ParametrBasic(CommandBuilder build)
    {
        builder = build;
    }

    public ParametrBasic(CommandBuilder build, String name_value)
    {
        builder = build;
        this.name_value = name_value;
    }

    @Override
    public boolean init(ICommandSender sender, String value) {
        if(perm.equals(""))
        {
            return true;
        }
        if(!CheckPermissions.checkPermission(sender,perm)) return false;
        return true;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    protected ParametrBasic setOptional(boolean new_stat)
    {
        optional = new_stat;
        return this;
    }

    protected ParametrBasic setErrorKeyMessage(String key)
    {
        errorKeyMessage = key;
        return this;
    }

    protected ParametrBasic setDependent(boolean value)
    {
        dependentParam = true;
        return this;
    }
    @Override
    public boolean isDependent()
    {
        return dependentParam;
    }

    protected ParametrBasic setName(String name_parametr)
    {
        name_value = name_parametr;
        return this;
    }
    @Override
    public String getName()
    {
        return name_value;
    }

    @Override
    public CommandBuilder end() {
        builder.addParametr(this);
        return builder;
    }

    public ParametrBasic setPerm(String perm)
    {
        this.perm = perm;
        return this;
    }

    protected void printError(ICommandSender sender,String value)
    {

        sender.sendMessage(CommandsOutput.Common.argsError("command.vindex.vi.err_arg",value));
        if(!errorKeyMessage.equals(""))
        {
            sender.sendMessage(CommandsOutput.Common.argsError(errorKeyMessage));
        }
    }

    @Override
    public String getPerm()
    {
        return perm;
    }
}
