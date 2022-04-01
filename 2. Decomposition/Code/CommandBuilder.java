public class CommandBuilder {
    private String baseArgument = "";
    private List<IParametr> parametrs = new ArrayList<>();
    private List<CommandBuilder> lines = new ArrayList<>();
    private int countArguments = 0;
    private int maxCountArguments = -1;
    private CommandBuilder parent = null;
    private String[] commandArgs = new String[]{""};
    private String perm = "";
    private boolean noArgs = false;
    private boolean onlyPlayer = false;
    private HashMap<String,Object> result = new HashMap<>();
    private BiFunction<ICommandSender, HashMap<String,Object>,Void> func = null;


    private CommandBuilder(String name)
    {
        baseArgument = name;
    }
    private CommandBuilder(CommandBuilder parent, String name)
    {
        this.parent = parent;
        baseArgument = name;
    }

    public static CommandBuilder create(String baseArgument)
    {
        return new CommandBuilder(baseArgument);
    }

    public String getBaseArgument()
    {
        return baseArgument;
    }

    public CommandBuilder setBaseArgument(String baseArgument)
    {
        this.baseArgument = baseArgument;
        return this;
    }

    public CommandBuilder setCommandArgs(String[] args)
    {
        commandArgs = args;
        return this;
    }
    public CommandBuilder setOnlyPlayer()
    {
        onlyPlayer = true;
        return this;
    }

    public CommandBuilder setCountArgs(int number)
    {
        countArguments = number;
        return this;
    }

    public CommandBuilder setMaxCountArgs(int number)
    {
        maxCountArguments = number;
        return this;
    }

    public CommandBuilder setPerm(String perm)
    {
        this.perm = perm;
        return this;
    }
    public CommandBuilder setNoArgs()
    {
        noArgs = true;
        return this;
    }

    public CommandBuilder setExecuteFunc(BiFunction<ICommandSender, HashMap<String,Object>,Void> func)
    {
        this.func = func;
        return this;
    }



    public BoolParametr AddBoolParametr(String name)
    {
        return new BoolParametr(this, name);
    }
    public IntegerParametr AddIntegerParametr(String name)
    {
        return new IntegerParametr(this, name);
    }
    public DoubleParametr AddDoubleParametr(String name)
    {
        return new DoubleParametr(this, name);
    }
    public StringParametr AddStringParametr(String name)
    {
        return new StringParametr(this, name);
    }

    public void addParametr (IParametr parametr)
    {
        parametrs.add(parametr);
    }
    public CommandBuilder child(String baseArgument)
    {
        return new CommandBuilder(this,baseArgument);
    }

    public CommandBuilder parent()
    {
        parent.lines.add(this);
        return parent;
    }

    private String getName()
    {
        String name = "";
        if(parent != null)
        {
            name = parent.getName()+"."+baseArgument;
        }
        else
        {
            name = baseArgument;
        }
        return name;
    }
    private String getParentName()
    {
        String name = getName();
        if(name.contains("."+baseArgument))
        {
            name = name.replace("."+baseArgument,"");
        }
        else
        {
            name = "";
        }


        return name;
    }


    public boolean check(String start_arg)
    {
        return start_arg.equals(baseArgument);
    }

    public HashMap<String,Object> execute(ICommandSender sender, String[] args)
    {
        result = new HashMap<>();
        if(onlyPlayer)
        {
            if(!CheckPermissions.checkPlayer(sender))
            {
                result.put("status",false);
            }
        }
        if(!perm.equals("")) {
            if (!CheckPermissions.checkPermission(sender, perm)) {
                result.put("status", false);
                return result;
            }
        }
        if(maxCountArguments == -1) maxCountArguments = countArguments;
        if (args.length < countArguments || args.length > maxCountArguments) {
            if(commandArgs[0].equals(""))
                commandArgs = getHelpErr(sender).toArray(commandArgs);
            sender.sendMessage(CommandsOutput.Common.commandArgs(commandArgs, getParentName().replaceAll("\\."," ")));
            result.put("status", false);
            return result;
        }
        if(!check(args[0]))
        {
            sender.sendMessage(CommandsOutput.Common.argsError());
            result.put("status", false);
            return result;
        }

        if(!noArgs) {
            int i = 1;
            boolean check_old = true;
            for (IParametr param : parametrs) {
                if (param.isDependent()) {
                    if (!check_old) {
                        continue;
                    }
                }
                boolean check;
                if(args.length <= i)
                {
                    check = false;
                }
                else
                {
                    check = param.init(sender, args[i]);
                }
                if (!check) {
                    if (param.isOptional()) {
                        check_old = check;
                        continue;
                    } else {
                        result.put("status", false);
                        return result;
                    }
                } else {
                    i++;
                }
                check_old = check;
            }
            boolean check = true;
            for (CommandBuilder line : lines) {
                if(line.check(args[1]))
                {
                    HashMap<String, Object> temp = line.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                    if (((boolean) temp.get("status"))) {
                        result.putAll(temp);
                        result.put("status", true);
                        String name = getParentName();
                        if(name.equals("")) name = baseArgument;
                        result.put(name+".next", line.baseArgument);

                        check = false;
                        break;
                    }
                }

            }
            if(check && !lines.isEmpty())
            {
                result.put("status", false);
                return result;
            }
        }
        result = getValues();
        if(func !=null)
        {
            func.apply(sender,result);
        }
        else
        {
            result.put("status", true);
        }

        return result;
    }
    public HashMap<String,Object> getValues()
    {
        HashMap<String,Object> results = new HashMap<>();
        for(IParametr parametr : parametrs)
        {
            if(parametr.getValue() !=null) {
                results.put(parametr.getName(), parametr.getValue());
            }
        }
        results.putAll(this.result);
        return results;
    }

    public List<String> getHelpErr(ICommandSender sender)
    {
        List<String> res = new ArrayList<>();
        if(!perm.equals("") && !CheckPermissions.checkPermission(sender,perm,false))
        {
            return res;
        }
        if(noArgs)
        {
            res.add(baseArgument);
            return res;
        }
        for(CommandBuilder line : lines)
        {
            res.addAll(line.getHelpErr(sender));
        }
        String my_param = "";
        String dep_param = "";
        for(int i = parametrs.size()-1;i>=0;i--)
        {
            if(!parametrs.get(i).getPerm().equals(""))
            {
                if(!CheckPermissions.checkPermission(sender,parametrs.get(i).getPerm(),false))
                {
                    continue;
                }
            }
            IParametr parametr = parametrs.get(i);
            String aliases = getAliasesParametr(parametr);

            if(parametrs.get(i).isDependent())
            {

                if(parametrs.get(i).isOptional())
                {
                    dep_param = " ["+aliases+"]" + dep_param;
                }
                else
                {
                    dep_param = " <"+aliases+">" + dep_param;
                }
            }
            else
            {
                if(parametrs.get(i).isOptional())
                {
                    if(!dep_param.equals(""))
                    {
                        dep_param = " <"+aliases+">" + dep_param;
                        my_param =" ["+dep_param.substring(1)+"]" + my_param;
                        dep_param = "";
                    }
                    else
                    {
                        my_param = " ["+aliases+"]" + my_param;
                    }
                }
                else
                {
                    my_param = " <"+aliases+">" + my_param;
                }
            }
        }

        my_param=baseArgument + my_param;
        for(int i = 0;i< res.size(); i++)
        {
            res.set(i,my_param+" "+res.get(i));
        }
        if(res.isEmpty())
        {
            res.add(my_param);
        }


        return res;
    }

    public String getCommandList(ICommandSender sender)
    {
        String res = "";
        if(!perm.equals("") && !CheckPermissions.checkPermission(sender,perm,false))
        {
            return res;
        }

        if(lines.isEmpty())
        {
            res=baseArgument;
            return res;
        }
        res+="<";
        for(CommandBuilder line : lines)
        {
            String line_perm = line.perm;
            if(!line_perm.equals("") && !CheckPermissions.checkPermission(sender,line_perm,false))
            {
                continue;
            }
            res+= line.getBaseArgument() + "/";
        }
        res = res.substring(0,res.length()-1);
        if(res.equals(""))
            return baseArgument;
        res+=">";
        return baseArgument+" "+ res;
    }

    private String getAliasesParametr(IParametr parametr)
    {
        if(parametr instanceof BoolParametr)
        {
            List<String> temp = ((BoolParametr) parametr).getTrueAliases();
            temp.addAll(((BoolParametr) parametr).getFalseAliases());
            String res = "";
            for(String str : temp)
            {
                res+=str+"/";
            }
            res = res.substring(0,res.length()-1);
            return res;
        }
        else if(parametr instanceof StringParametr)
        {
            List<String> temp = ((StringParametr) parametr).getPotentialValues();
            if(temp.isEmpty())
            {
                return parametr.getName();
            }
            String res = "";
            for(String str : temp)
            {
                res+=str+"/";
            }
            res = res.substring(0,res.length()-1);
            return res;
        }
        else
        {
            return parametr.getName();
        }
    }

}
