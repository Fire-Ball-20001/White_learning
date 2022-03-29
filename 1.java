public class VindexCommand extends CommandBase {

    private final String[][] commands = new String[][]{
            {
                    "/vi cost list",
                    "/vi costs",
                    "/vi worth",
                    "/vi top",
                    "/vi help [command]"
            },
            {
                    "/vi cost <add/remove/list>",
                    "/vi costs",
                    "/vi worth",
                    "/vi top",
                    "/vi help [command]"
            },
            {
                    "/vi cost <add/remove/list>",
                    "/vi costs",
                    "/vi reward <add/set/remove/list>",
                    "/vi rewards",
                    "/vi worth",
                    "/vi top",
                    "/vi help [command]"
            },
            {
                    "/vi config <load/save>",
                    "/vi cost <add/remove/list>",
                    "/vi costs",
                    "/vi reward <add/set/remove/list>",
                    "/vi rewards",
                    "/vi worth",
                    "/vi top",
                    "/vi stop",
                    "/vi help [command]"
            }
    };
    private static final String MAIN_PERM = "vindex.command";
    private String node = "";

    @Override
    public String getName() {
        return "vi";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return new TextComponentTranslation("command.vindex.vi.help").getFormattedText();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!checkPermission(sender, MAIN_PERM)) return;
        if (args.length == 0) {
            sender.sendMessage(CommandsOutput.Common.commandsList(commands[permissionLevel(sender)]));
            return;
        }
        switch (args[0]) {
            case ("config"):
                if (args.length != 2) {
                    sender.sendMessage(CommandsOutput.Config.configArgs(permissionLevel(sender)>=2));
                    break;
                }

                configCommand(sender, Arrays.copyOfRange(args,1, args.length));
                break;
            case ("cost"): {

                if (args.length < 2) {
                    sender.sendMessage(CommandsOutput.Cost.costArgs(permissionLevel(sender)>=1));
                    break;
                }
                costCommand(sender,Arrays.copyOfRange(args,1,args.length));
                break;
            }
            case ("costs"): {
                costCommand(sender,new String[]{"list"});
                break;
            }
            case ("reward"):
            {
                if (args.length < 2) {
                    sender.sendMessage(CommandsOutput.Reward.rewardArgs(permissionLevel(sender)>=2));
                    break;
                }
                rewardCommand(sender,Arrays.copyOfRange(args,1,args.length));
                break;
            }
            case ("rewards"): {
                rewardCommand(sender,new String[]{"list"});
                break;
            }
            case ("worth"): {
                worthCommand(sender);
                break;
            }
            case ("help"):
            {
                node = MAIN_PERM+".help";
                if (!checkPermission(sender, node)) return;
                helpCommand(sender,Arrays.copyOfRange(args,1,args.length));
                break;
            }
            case ("top"):
            {
                topCommand(sender,Arrays.copyOfRange(args,1,args.length));
                break;
            }
            case ("stop"): {
                if(permissionLevel(sender)<3)
                {
                    sender.sendMessage(CommandsOutput.Common.accessDenied());
                    break;
                }
                sender.sendMessage(CommandsOutput.Season.forceStopSeason());
                Vindex.config.previousAward = Instant.MIN;
                break;
            }
            default:
                sender.sendMessage(CommandsOutput.Common.argsError());
                sender.sendMessage(CommandsOutput.Common.commandsList(commands[permissionLevel(sender)]));
                break;
        }
    }

    protected void topCommand(ICommandSender sender,String[] args)
    {
        String name = "";
        int pos = -1;
        int score = -1;
        List<GVindexDatabase.PlayerStatistics> stats = Vindex.database.stats();
        stats.sort(ScoreboardDaemon.statTotalCompare);
        if(sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) sender;
            name = player.getName();
            GVindexDatabase.PlayerStatistics stat = Vindex.database.get(player.getUniqueID().toString());
            if(stat.name.equals("Unknown@" + stat.UUID.substring(0, 4)))
            {
                pos = -1;
                score = -1;
            }
            else {
                pos = stats.indexOf(stat);
                score = (int) Math.sqrt(stat.total);
            }
        }
        else
        {
            name="Console";
        }
        stats.removeIf(x -> x.total < Vindex.config.minPoints);
        List<HashMap<String,String>> result = new ArrayList<>();
        int i = 0;
        for(GVindexDatabase.PlayerStatistics player : stats)
        {
            if(i==10)
            {
                break;
            }
            HashMap<String,String> temp = new HashMap<>();
            temp.put("name",player.name);
            temp.put("points",String.valueOf((int) Math.sqrt(player.total)));
            result.add(temp);
            i++;
        }
        sender.sendMessage(CommandsOutput.Common.topPlayers(result,name,pos,score));
    }

    protected void configCommand(ICommandSender sender, String[] args)
    {
        node = MAIN_PERM+".config";
        if (!checkPermission(sender, node)) return;
        node += ".";
        switch (args[0]) {
            case ("load"):
                node += "load";
                if (!checkPermission(sender, node)) return;
                Vindex.config.load();
                sender.sendMessage(CommandsOutput.Config.configsReloaded());
                break;
            case ("save"):
                node += "save";
                if (!checkPermission(sender, node)) return;
                Vindex.config.save();
                sender.sendMessage(CommandsOutput.Config.configsSaved());
                break;
            default: {
                sender.sendMessage(CommandsOutput.Config.configArgs(permissionLevel(sender)>=3));

                break;
            }
        }

    }

    protected void costCommand(ICommandSender sender, String[] args)
    {
        node = MAIN_PERM+".cost";
        if (!checkPermission(sender, node)) return;
        node+=".";

        ItemStack stack;
        String name = "";
        boolean this_cost = false;
        switch (args[0]) {
            case ("add"):
                node+="add";
                if (!checkPermission(sender, node)) return;
                if(!checkPlayer(sender)) return;
                stack = getHeldItem(sender);
                if (args.length < 2) {
                    sender.sendMessage(CommandsOutput.Cost.costArgs(permissionLevel(sender)>=1));
                    break;
                }
                if (stack.isEmpty() && args.length < 3) {
                    sender.sendMessage(CommandsOutput.Cost.needItemInHand());
                    return;
                }
                name = args.length > 2 ? args[2] : "";
                if(!name.equals(""))
                {
                    ItemStack temp = GameRegistry.makeItemStack(name,0,1,"");
                    if(!temp.isEmpty())
                    {
                        stack=temp;
                    }
                }
                stack.setCount(1);
                if(args[args.length-1].equals("-t"))
                {
                    this_cost = true;
                }
                if (Vindex.costs.Find(stack.serializeNBT(),this_cost)!=null) {
                    sender.sendMessage(CommandsOutput.Cost.alreadyInList());
                    break;
                }
                sender.sendMessage(CommandsOutput.Cost.addedInList());
                Vindex.costs.Add(stack.serializeNBT(), Integer.parseInt(args[1]),this_cost);
                Vindex.costs.save();
                break;
            case ("remove"):
                node+="remove";
                if (!checkPermission(sender, node)) return;
                if(!checkPlayer(sender)) return;
                stack = getHeldItem(sender);
                if (args.length >2) {
                    sender.sendMessage(CommandsOutput.Cost.costArgs(permissionLevel(sender)>=1));
                    break;
                }
                if (stack.isEmpty() && args.length < 2) {
                    sender.sendMessage(CommandsOutput.Cost.needItemInHand());
                    return;
                }
                name = args.length > 1 ? args[1] : "";
                if(!name.equals(""))
                {
                    ItemStack temp = GameRegistry.makeItemStack(name,0,1,"");
                    if(!temp.isEmpty())
                    {
                        stack=temp;
                    }
                }
                if(args[args.length-1].equals("-t"))
                {
                    this_cost = true;
                }
                if (Vindex.costs.Find(stack.serializeNBT(),this_cost)==null) {
                    sender.sendMessage(CommandsOutput.Cost.noopInList());
                    break;
                }
                sender.sendMessage(CommandsOutput.Cost.deletedFromList());
                Vindex.costs.Remove(stack.serializeNBT(),this_cost);
                Vindex.costs.save();
                break;
            case ("list"):
                node+="list";
                if (!checkPermission(sender, node)) return;
                if(!args[args.length-1].equals("-f"))
                {
                    this_cost = true;
                }
                else
                {
                    if(!checkPermission(sender,node+".future")) return;
                }
                Comparator<Map.Entry<ItemStack, Double>> comparator = Map.Entry.comparingByValue();
                HashMap<ItemStack,Double> costs_name = new HashMap<>();
                for(Cost temp : Vindex.costs.getCosts(this_cost))
                {
                    ItemStack temp_2 = new ItemStack(temp.getItem());
                    costs_name.put(temp_2,(double) temp.getXp());
                }
                Collection<Map.Entry<ItemStack,Double>> list =
                        costs_name.entrySet().stream().sorted(comparator.reversed()).collect(Collectors.toList());
                sender.sendMessage(CommandsOutput.Cost.itemsCostList(list,permissionLevel(sender)>=1,this_cost));
                break;
        }
    }

    protected void rewardCommand(ICommandSender sender, String[] args)
    {
        node = MAIN_PERM+".reward";
        if(!checkPermission(sender,node))
        {
            return;
        }
        double k= 0;
        node +=".";
        boolean this_reward = false;
        switch (args[0])
        {
            case "add":
            {
                node+="add";
                if (!checkPermission(sender, node)) return;
                if(!checkPlayer(sender)) return;
                if(args.length!=3)
                {
                    sender.sendMessage(CommandsOutput.Reward.rewardArgs(permissionLevel(sender)>=2));
                    break;
                }
                try{
                    k = Double.parseDouble(args[1]);
                }
                catch (NumberFormatException err)
                {
                    sender.sendMessage(CommandsOutput.Reward.errorQuantile(args[1]));
                    break;
                }
                int money = 0;
                try{
                    money = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException err)
                {
                    sender.sendMessage(CommandsOutput.Reward.errorMoney(args[2]));
                    break;
                }
                if(k<=0 || k>1)
                {
                    sender.sendMessage(CommandsOutput.Reward.errorQuantile(args[1]));
                    sender.sendMessage(CommandsOutput.Reward.errorQuantile());
                    break;
                }
                if(money<0)
                {
                    sender.sendMessage(CommandsOutput.Reward.errorMoney(args[2]));
                    sender.sendMessage(CommandsOutput.Reward.errorMoney());
                    break;
                }
                if(args[args.length-1].equals("-t"))
                {
                    this_reward = true;
                }
                if(Vindex.rewards.Find(k,this_reward) != null)
                {
                    sender.sendMessage(CommandsOutput.Reward.errExistsReward());
                    break;
                }
                if(!Vindex.rewards.Add(new Reward((double)money),k,this_reward))
                {
                    sender.sendMessage(CommandsOutput.Reward.errorRewAdd());
                    break;
                }
                VindexInventory inv = new VindexInventory();
                inv.setTypeLabel("add")
                        .setReward(k)
                        .setIsThisList(this_reward);
                ((EntityPlayer)sender).displayGUIChest(inv);
                break;
            }
            case "remove":
            {
                node+="remove";
                if (!checkPermission(sender, node)) return;
                if(args.length>2)
                {
                    sender.sendMessage(CommandsOutput.Reward.rewardArgs(permissionLevel(sender)>=2));
                    break;
                }
                try{
                    k = Double.parseDouble(args[1]);
                }
                catch (NumberFormatException err)
                {
                    sender.sendMessage(CommandsOutput.Reward.errorQuantile(args[1]));
                    break;
                }
                if(args[args.length-1].equals("-t"))
                {
                    this_reward = true;
                }
                if(Vindex.rewards.Find(k,this_reward) == null)
                {
                    sender.sendMessage(CommandsOutput.Reward.errRewardNotFound());
                    break;
                }
                if(Vindex.rewards.Remove(k,this_reward))
                {
                    sender.sendMessage(CommandsOutput.Reward.successRewRemove());
                }
                else
                {
                    sender.sendMessage(CommandsOutput.Reward.errorRewRemove());
                }
                Vindex.rewards.save();
                break;
            }
            case "set":
            {
                node+="set";
                if (!checkPermission(sender, node)) return;
                if(!checkPlayer(sender)) return;
                if(args.length>2)
                {
                    sender.sendMessage(CommandsOutput.Reward.rewardArgs(permissionLevel(sender)>=2));
                    break;
                }
                try{
                    k = Double.parseDouble(args[1]);
                }
                catch (NumberFormatException err)
                {
                    sender.sendMessage(CommandsOutput.Reward.errorQuantile(args[1]));
                    break;
                }
                if(args[args.length-1].equals("-t"))
                {
                    this_reward = true;
                }
                if(Vindex.rewards.Find(k,this_reward) == null)
                {
                    sender.sendMessage(CommandsOutput.Reward.errRewardNotFound());
                    break;
                }
                VindexInventory inv = new VindexInventory();
                inv.setTypeLabel("edit")
                        .setReward(k)
                        .setIsThisList(this_reward);
                List<ItemStack> temp = new ArrayList<>();
                for(Reward.ItemReward item : Vindex.rewards.Find(k,this_reward).items)
                {
                    ItemStack itemStack = new ItemStack(item.item);
                    temp.add(itemStack);
                }
                inv.LoadItems(temp);
                ((EntityPlayer)sender).displayGUIChest(inv);

                break;
            }
            case "list":
            {
                Comparator<Map.Entry<Double, List<ItemStack>>> comparator = Map.Entry.comparingByKey();
                Comparator<Map.Entry<Double, Double>> comparator_2 = Map.Entry.comparingByKey();
                HashMap<Double, List<ItemStack>> costs_name = new HashMap<>();
                HashMap<Double, Double> money = new HashMap<>();
                if(!args[args.length-1].equals("-f"))
                {
                    this_reward = true;
                }
                else
                {
                    if(!checkPermission(sender,node+"list.future")) return;
                }
                for(Map.Entry<Double,Reward> temp : Vindex.rewards.getRewards(this_reward).entrySet())
                {
                    List<ItemStack> items = new ArrayList<>();
                    for(Reward.ItemReward item : temp.getValue().items)
                    {
                        ItemStack temp_2 = new ItemStack(item.item);
                        items.add(temp_2);
                    }

                    costs_name.put(temp.getKey(),items);
                    money.put(temp.getKey(),temp.getValue().money);
                }
                Collection<Map.Entry<Double,List<ItemStack>>> list =
                        costs_name.entrySet().stream().sorted(comparator).collect(Collectors.toList());

                sender.sendMessage(CommandsOutput.Reward.itemsRewardList(list,money,permissionLevel(sender)>=2,this_reward));

                break;
            }
            default:
            {
                sender.sendMessage(CommandsOutput.Reward.rewardArgs(permissionLevel(sender)>=2));
                break;
            }
        }

    }

    protected void worthCommand(ICommandSender sender){
        node = MAIN_PERM+".worth";
        if (!checkPermission(sender, node)) return;
        if(!checkPlayer(sender)) return;
        ItemStack stack = getHeldItem(sender);
        if (stack.isEmpty()) return;
        Cost temp = Vindex.costs.Find(stack.serializeNBT());
        sender.sendMessage(CommandsOutput.Worth.itemPrice(temp!=null ? temp.getXp() : 0));

    }

    protected void helpCommand(ICommandSender sender, String[] args){

        if(args.length==0)
        {
            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.help"));
            sender.sendMessage(CommandsOutput.Common.commandsList(commands[permissionLevel(sender)]));
            return;
        }
        else if(args.length != 1)
        {
            sender.sendMessage(CommandsOutput.Help.helpArgs());
            return;
        }
        switch (args[0]){
            case ("cost"): {
                List<String> messages = new ArrayList<String>();
                List<String> commands = new ArrayList<String>();
                messages.add((new TextComponentTranslation("command.vindex.vi.cost.help")).getFormattedText());
                if(checkPermission(sender,"vindex.command.cost.add",false))
                {
                    commands.add("cost add <reward> [item] [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.cost.add.help")).getFormattedText());
                }
                if(checkPermission(sender,"vindex.command.cost.remove",false))
                {
                    commands.add("cost remove [item] [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.cost.remove.help")).getFormattedText());
                }
                if(checkPermission(sender,"vindex.command.cost.list",false))
                {
                    commands.add("cost list [-f]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.costs.help")).getFormattedText());
                }

                sender.sendMessage(CommandsOutput.Help.helpMessage(messages.toArray(),commands.toArray()));
                break;
            }
            case("costs"): {
                if(checkPermission(sender,"vindex.command.cost.list"))
                {
                    sender.sendMessage(CommandsOutput.Help.helpMessage(
                            (new TextComponentTranslation("command.vindex.vi.costs.help")).getFormattedText()));
                }
                break;
            }
            case("reward"): {
                List<String> messages = new ArrayList<String>();
                List<String> commands = new ArrayList<String>();
                messages.add((new TextComponentTranslation("command.vindex.vi.reward.help")).getFormattedText());
                if(checkPermission(sender,"vindex.command.reward.add",false))
                {
                    commands.add("reward add <k> <money> [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.reward.add.help")).getFormattedText());
                }
                if(checkPermission(sender,"vindex.command.reward.remove",false))
                {
                    commands.add("reward remove <k> [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.reward.remove.help")).getFormattedText());
                }
                if(checkPermission(sender,"vindex.command.reward.set",false))
                {
                    commands.add("reward set <k> [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.reward.set.help")).getFormattedText());
                }
                commands.add("reward list [-f]");
                messages.add((new TextComponentTranslation("command.vindex.vi.rewards.help")).getFormattedText());
                sender.sendMessage(CommandsOutput.Help.helpMessage(messages.toArray(),commands.toArray()));
                break;
            }
            case("rewards"): {
                sender.sendMessage(CommandsOutput.Help.helpMessage(
                        new TextComponentTranslation("command.vindex.vi.rewards.help").getFormattedText()
                ));
                break;
            }
            case("worth"): {
                if(checkPermission(sender,"vindex.command.worth"))
                {
                    sender.sendMessage(CommandsOutput.Help.helpMessage(
                            (new TextComponentTranslation("command.vindex.vi.worth.help")).getFormattedText()));
                }
                break;
            }
            case("top"):
            {
                sender.sendMessage(CommandsOutput.Help.helpMessage(
                        (new TextComponentTranslation("command.vindex.vi.top.help")).getFormattedText()));
                break;
            }
            case("config"):
            {
                if(checkPermission(sender,"vindex.command.config"))
                {
                    List<String> messages = new ArrayList<String>();
                    List<String> commands = new ArrayList<String>();
                    messages.add((new TextComponentTranslation("command.vindex.vi.config.help")).getFormattedText());
                    if(checkPermission(sender,"vindex.command.config.load",false))
                    {
                        commands.add("config load");
                        messages.add((new TextComponentTranslation("command.vindex.vi.config.load.help")).getFormattedText());
                    }
                    if(checkPermission(sender,"vindex.command.config.save",false))
                    {
                        commands.add("config save");
                        messages.add((new TextComponentTranslation("command.vindex.vi.config.save.help")).getFormattedText());
                    }
                    sender.sendMessage(CommandsOutput.Help.helpMessage(messages.toArray(),commands.toArray()));
                }
                break;
            }
            case("help"):
            {
                sender.sendMessage(CommandsOutput.Help.helpMessage(
                        (new TextComponentTranslation("command.vindex.vi.help.help")).getFormattedText()));
                break;
            }
            case ("stop"):
            {
                if(checkPermission(sender,"vindex.seasons.stop"))
                {
                    sender.sendMessage(CommandsOutput.Help.helpMessage(
                            new TextComponentTranslation("command.vindex.vi.stop.help").getFormattedText()));
                }
                break;
            }
            default: {
                sender.sendMessage(CommandsOutput.Help.helpArgs());
                break;
            }
        }

    }

    private ItemStack getHeldItem(ICommandSender sender) {
        Entity entity = sender.getCommandSenderEntity();
        ItemStack stack = ItemStack.EMPTY;
        if (entity instanceof EntityPlayer) stack = ((EntityPlayer) entity).getHeldItemMainhand();
        return stack;
    }


    private boolean checkPermission(ICommandSender sender, String permission)
    {
        return checkPermission(sender,permission,true);
    }
    private boolean checkPlayer(ICommandSender sender)
    {
        if(sender instanceof EntityPlayer)
        {
            return true;
        }
        else
        {
            sender.sendMessage(CommandsOutput.Common.onlyPlayer());
            return false;
        }
    }

    private boolean checkPermission(ICommandSender sender, String permission, boolean is_output) {
        if(!(sender instanceof EntityPlayer))
        {
            return true;
        }
        Entity entity = sender.getCommandSenderEntity();

        if (entity instanceof EntityPlayer && PermissionAPI.hasPermission(((EntityPlayer) entity), permission)
        || sender.getServer().getPlayerList().canSendCommands(((EntityPlayer) sender).getGameProfile())) return true;
        if(is_output) {
            sender.sendMessage(CommandsOutput.Common.accessDenied());
        }
        return false;
    }

    private int permissionLevel(ICommandSender sender)
    {
        if(!(sender instanceof EntityPlayer))
        {
            return 3;
        }
        Entity entity = sender.getCommandSenderEntity();
        if(entity instanceof EntityPlayer)
        {
            if(checkPermission( entity,"vindex.command.config",false))
                return 3;
            else if(checkPermission( entity,"vindex.command.reward",false))
                return 2;
            else if(checkPermission(entity,"vindex.command.cost",false))
                return 1;
            else
                return 0;
        }
        else return 3;

    }
}
