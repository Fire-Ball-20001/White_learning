public class VindexCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender instanceof EntityPlayer)
            return PermissionAPI.hasPermission((EntityPlayer) sender, MAIN_PERM);
        return true;
    }

    private static HashMap<String, List<String>> tab_commands =
            Maps.newHashMap(
                    ImmutableMap.of(
                            "cost", Arrays.asList("list", "add", "remove"),
                            "reward", Arrays.asList("list", "add", "set", "remove"),
                            "costs", Collections.emptyList(),
                            "rewards", Collections.emptyList(),
                            "worth", Collections.emptyList()));


    private static final String MAIN_PERM = "vindex.command";
    private HashMap<String, CommandBuilder> commandsParam = new HashMap<>();

    @Override
    public String getName() {
        return "vi";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return new TextComponentTranslation("command.vindex.vi.help").getFormattedText();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        tab_commands.putAll(Maps.newHashMap(ImmutableMap.of(
                "top", Collections.emptyList(),
                "help", Collections.emptyList(),
                "config", Arrays.asList("load", "save"),
                "stop", Collections.emptyList())));

        boolean isSpaceOld = false;
        List<String> results = new ArrayList<>();
        if (args[args.length - 1].equals("")) {
            isSpaceOld = true;
        }
        List<String> existingArgs = new ArrayList<>();
        for (String arg : args) {
            if (!arg.equals("")) {
                existingArgs.add(arg);
            }
        }

        if (existingArgs.isEmpty()) {
            switch (permissionLevel(sender)) {
                case 0:
                case 1: {
                    results.addAll(Arrays.asList("cost", "costs", "worth", "help", "top"));
                }
                case 2: {
                    results.addAll(Arrays.asList("cost", "costs", "reward", "rewards", "worth", "help", "top"));
                }
                case 3: {
                    results.addAll(Arrays.asList("cost", "costs", "reward", "rewards", "worth", "help", "top", "stop", "config"));
                }
            }
        } else {
            if (isSpaceOld) {
                if (existingArgs.size() == 1) {
                    if (!tab_commands.containsKey(args[0])) {
                        return results;
                    }


                    switch (args[0].toLowerCase()) {
                        case "cost": {
                            if (permissionLevel(sender) < 1) {
                                results.add("list");
                            } else {
                                results.addAll(tab_commands.get(args[0]));
                            }
                            break;
                        }
                        case "reward": {
                            if (permissionLevel(sender) < 2) {
                                results.add("list");
                            } else {
                                results.addAll(tab_commands.get(args[0]));
                            }
                            break;
                        }
                        case "config": {
                            if (permissionLevel(sender) >= 3) {
                                results.addAll(tab_commands.get(args[0]));
                            }
                            break;
                        }
                    }

                }
            } else {
                //Error - Player can see OP commands
                if (existingArgs.size() == 1) {
                    for (String comm : tab_commands.keySet()) {
                        if (comm.startsWith(args[0].toLowerCase())) {
                            results.add(comm);
                        }
                    }
                }
                if (existingArgs.size() == 2) {
                    if (!tab_commands.containsKey(args[0])) {
                        return results;
                    }
                    for (String comm : tab_commands.get(args[0])) {
                        if (comm.startsWith(args[1].toLowerCase())) {
                            results.add(comm);
                        }
                    }
                }
            }
        }

        return results;

    }

    private void createParametrs() {
        CommandBuilder top_param;
        CommandBuilder config_param;
        CommandBuilder cost_param;
        CommandBuilder costs_param;
        CommandBuilder reward_param;
        CommandBuilder rewards_param;
        CommandBuilder stop_param;
        CommandBuilder worth_param;
        CommandBuilder help_param;


        top_param = CommandBuilder.create("top")
                .setCountArgs(1)
                .setPerm(MAIN_PERM + ".top")
                .setNoArgs()
                .setExecuteFunc(this::topCommand);
        config_param = CommandBuilder.create("config")
                .setPerm(MAIN_PERM + ".config")
                .setCountArgs(2)
                .AddStringParametr("value")
                .setPotentialValues(new String[]{"save", "load"})
                .end()
                .setExecuteFunc(this::configCommand);
        cost_param = CommandBuilder.create("cost")
                .setCountArgs(2)
                .setMaxCountArgs(5)
                .setPerm(MAIN_PERM + ".cost")
                .setExecuteFunc(this::costCommand)
                .child("add")
                .setOnlyPlayer()
                .setPerm(MAIN_PERM + ".cost.add")
                .setCountArgs(2)
                .setMaxCountArgs(5)
                .AddIntegerParametr("cost")
                .setMinValue(0)
                .end()
                .AddBoolParametr("is_this")
                .setUseLogicConst(false)
                .setTrueAliases(new String[]{"-t"})
                .setOptional(true)
                .setPerm(MAIN_PERM + ".cost.add.this")
                .end()
                .AddStringParametr("item")
                .setOptional(true)
                .end()
                .AddIntegerParametr("meta")
                .setMinValue(0)
                .setOptional(true)
                .setDependent(true)
                .end()
                .parent()
                .child("remove")
                .setPerm(MAIN_PERM + ".cost.remove")
                .setOnlyPlayer()
                .setCountArgs(1)
                .setMaxCountArgs(4)
                .AddBoolParametr("is_this")
                .setUseLogicConst(false)
                .setTrueAliases(new String[]{"-t"})
                .setOptional(true)
                .setPerm(MAIN_PERM + ".cost.add.this")
                .end()
                .AddStringParametr("item")
                .setOptional(true)
                .end()
                .AddIntegerParametr("meta")
                .setMinValue(0)
                .setOptional(true)
                .setDependent(true)
                .end()
                .parent()
                .child("list")
                .setPerm(MAIN_PERM + ".cost.list")
                .setCountArgs(1)
                .setMaxCountArgs(2)
                .AddBoolParametr("is_future")
                .setPerm(MAIN_PERM + ".cost.list.future")
                .setOptional(true)
                .setUseLogicConst(false)
                .setTrueAliases(new String[]{"-f"})
                .end()
                .parent();

        costs_param = CommandBuilder.create("costs")
                .setCountArgs(1)
                .setPerm(MAIN_PERM + ".cost.list")
                .setNoArgs()
                .setExecuteFunc(this::costsCommand);
        worth_param = CommandBuilder.create("worth")
                .setPerm(MAIN_PERM + ".worth")
                .setCountArgs(1)
                .setOnlyPlayer()
                .setNoArgs()
                .setExecuteFunc(this::worthCommand);
        reward_param = CommandBuilder.create("reward")
                .setPerm(MAIN_PERM + ".reward")
                .setCountArgs(2)
                .setMaxCountArgs(5)
                .setExecuteFunc(this::rewardCommand)
                .child("add")
                .setOnlyPlayer()
                .setPerm(MAIN_PERM + ".reward.add")
                .setCountArgs(3)
                .setMaxCountArgs(4)
                .AddDoubleParametr("k")
                .setMinValue(0.0)
                .setMaxValue(1.0)
                .end()
                .AddIntegerParametr("money")
                .setMinValue(0)
                .end()
                .AddBoolParametr("is_this")
                .setUseLogicConst(false)
                .setTrueAliases(new String[]{"-f"})
                .setPerm(MAIN_PERM + ".reward.add.this")
                .setOptional(true)
                .end()
                .parent()
                .child("remove")
                .setPerm(MAIN_PERM + ".reward.remove")
                .setOnlyPlayer()
                .setCountArgs(2)
                .setMaxCountArgs(3)
                .AddDoubleParametr("k")
                .setMinValue(0.0)
                .setMaxValue(1.0)
                .end()
                .AddBoolParametr("is_this")
                .setPerm(MAIN_PERM + ".reward.remove.this")
                .setUseLogicConst(false)
                .setTrueAliases(new String[]{"-t"})
                .setOptional(true)
                .end()
                .parent()
                .child("set")
                .setOnlyPlayer()
                .setCountArgs(2)
                .setMaxCountArgs(3)
                .setPerm(MAIN_PERM + ".reward.set")
                .AddDoubleParametr("k")
                .setMinValue(0.0)
                .setMaxValue(1.0)
                .end()
                .AddBoolParametr("is_this")
                .setPerm(MAIN_PERM + ".reward.set.this")
                .setUseLogicConst(false)
                .setTrueAliases(new String[]{"-t"})
                .setOptional(true)
                .end()
                .parent()
                .child("list")
                .setPerm(MAIN_PERM + ".reward.list")
                .setCountArgs(1)
                .setMaxCountArgs(2)
                .AddBoolParametr("is_future")
                .setOptional(true)
                .setPerm(MAIN_PERM + ".reward.list.future")
                .setTrueAliases(new String[]{"-f"})
                .setUseLogicConst(false)
                .end()
                .parent();
        rewards_param = CommandBuilder.create("rewards")
                .setPerm(MAIN_PERM + ".reward.list")
                .setCountArgs(1)
                .setNoArgs()
                .setExecuteFunc(this::rewardsCommand);
        stop_param = CommandBuilder.create("stop")
                .setCountArgs(1)
                .setPerm(MAIN_PERM + ".stop")
                .setNoArgs()
                .setExecuteFunc(this::stopCommand);
        help_param = CommandBuilder.create("help")
                .setCountArgs(1)
                .setMaxCountArgs(2)
                .setPerm(MAIN_PERM+".help")
                .setExecuteFunc(this::helpCommand)
                .AddStringParametr("command")
                .setOptional(true)
                .end();


        commandsParam = new HashMap<>();

        commandsParam.put("top", top_param);
        commandsParam.put("config", config_param);
        commandsParam.put("cost", cost_param);
        commandsParam.put("costs", costs_param);
        commandsParam.put("reward", reward_param);
        commandsParam.put("rewards", rewards_param);
        commandsParam.put("stop", stop_param);
        commandsParam.put("worth", worth_param);
        commandsParam.put("help",help_param);
    }



    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!CheckPermissions.checkPermission(sender, MAIN_PERM)) return;
        if (args.length == 0) {
            sender.sendMessage(CommandsOutput.Common.commandsList(getAllowedCommands(sender)));
            return;
        }
        HashMap<String, Object> res = new HashMap<>();
        createParametrs();
        if(commandsParam.containsKey(args[0]))
        {
            commandsParam.get(args[0]).execute(sender,args);
        }
        else
        {
            sender.sendMessage(CommandsOutput.Common.argsError());
            sender.sendMessage(CommandsOutput.Common.commandsList(getAllowedCommands(sender)));
        }


    }

    private String[] getAllowedCommands(ICommandSender sender) {
        List<String> res = new ArrayList<>();
        createParametrs();
        for (Map.Entry<String, CommandBuilder> entry : commandsParam.entrySet()) {
            String temp = entry.getValue().getCommandList(sender);
            if (temp.equals("")) {
                continue;
            }
            res.add(temp);
        }
        res.sort(Collator.getInstance());
        for (int i = 0; i < res.size(); i++) {
            res.set(i, "/vi " + res.get(i));
        }
        return res.toArray(new String[]{});
    }

    private Void stopCommand(ICommandSender sender, HashMap<String, Object> stringObjectHashMap) {
        sender.sendMessage(CommandsOutput.Season.forceStopSeason());
        Vindex.config.previousAward = Vindex.config.previousAward
                .minus(Vindex.seasons.getLastTime(), ChronoUnit.SECONDS);
        return null;
    }

    protected Void topCommand(ICommandSender sender, HashMap<String, Object> args) {
        String name = "";
        int pos = -1;
        int score = -1;
        List<GVindexDatabase.PlayerStatistics> stats = Vindex.database.stats();
        stats.sort(ScoreboardDaemon.statTotalCompare);
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            name = player.getName();
            GVindexDatabase.PlayerStatistics stat = Vindex.database.get(player.getUniqueID().toString());
            if (!stat.name.equals("Unknown@" + stat.UUID.substring(0, 4))) {
                pos = stats.indexOf(stat);
                score = (int) Math.sqrt(stat.total);
            }
        } else {
            name = "Console";
        }
        stats.removeIf(x -> x.total < Vindex.config.minPoints);
        List<HashMap<String, String>> result = new ArrayList<>();
        int i = 0;
        for (GVindexDatabase.PlayerStatistics player : stats) {
            if (i == 10) {
                break;
            }
            HashMap<String, String> temp = new HashMap<>();
            temp.put("name", player.name);
            temp.put("points", String.valueOf((int) Math.sqrt(player.total)));
            result.add(temp);
            i++;
        }
        sender.sendMessage(CommandsOutput.Common.topPlayers(result, name, pos, score));
        return null;
    }

    protected Void configCommand(ICommandSender sender, HashMap<String, Object> args) {
        String arg_1 = (String) args.get("value");
        switch (arg_1) {
            case ("load"):
                Vindex.config.load();
                sender.sendMessage(CommandsOutput.Config.configsReloaded());
                break;
            case ("save"):
                Vindex.config.save();
                sender.sendMessage(CommandsOutput.Config.configsSaved());
                break;
            default: {
                sender.sendMessage(CommandsOutput.Config.configArgs(permissionLevel(sender) >= 3));
                break;
            }
        }

        return null;

    }

    protected Void costCommand(ICommandSender sender, HashMap<String, Object> args) {
        ItemStack stack;
        String name = args.containsKey("item") ? (String) args.get("item") : "";
        int damage = args.containsKey("meta") ? (int) args.get("meta") : 0;
        boolean this_cost = args.containsKey("is_this");
        switch ((String) args.get("cost.next")) {
            case ("add"):
                stack = getHeldItem(sender);
                if (stack.isEmpty() && name.equals("")) {
                    sender.sendMessage(CommandsOutput.Cost.needItemInHand());
                    return null;
                }
                if (!name.equals("")) {
                    ItemStack temp = GameRegistry.makeItemStack(name, damage, 1, "");
                    if (!temp.isEmpty()) {
                        stack = temp;
                    } else {
                        sender.sendMessage(CommandsOutput.Common.argsError("command.vindex.vi.err_arg", name));
                        sender.sendMessage(CommandsOutput.Common.argsError("command.vindex.vi.cost.invalid_item"));
                        break;
                    }
                }
                stack.setCount(1);
                if (Vindex.costs.Find(stack.serializeNBT(), this_cost) != null) {
                    sender.sendMessage(CommandsOutput.Cost.alreadyInList());
                    break;
                }
                sender.sendMessage(CommandsOutput.Cost.addedInList(this_cost));
                Vindex.costs.Add(stack.serializeNBT(), (Integer) args.get("cost"), this_cost);
                Vindex.costs.save();
                break;
            case ("remove"):
                stack = getHeldItem(sender);
                if (stack.isEmpty() && name.equals("")) {
                    sender.sendMessage(CommandsOutput.Cost.needItemInHand());
                    return null;
                }
                if (!name.equals("")) {
                    ItemStack temp = GameRegistry.makeItemStack(name, damage, 1, "");
                    if (!temp.isEmpty()) {
                        stack = temp;
                    } else {
                        sender.sendMessage(CommandsOutput.Common.argsError("command.vindex.vi.err_arg", name));
                        sender.sendMessage(CommandsOutput.Common.argsError("command.vindex.vi.cost.invalid_item"));
                        break;
                    }
                }
                stack.setCount(1);
                if (Vindex.costs.Find(stack.serializeNBT(), this_cost) == null) {
                    sender.sendMessage(CommandsOutput.Cost.noopInList());
                    break;
                }
                sender.sendMessage(CommandsOutput.Cost.deletedFromList(this_cost));
                Vindex.costs.Remove(stack.serializeNBT(), this_cost);
                Vindex.costs.save();
                break;
            case ("list"):
                this_cost = !args.containsKey("is_future");
                Comparator<Map.Entry<ItemStack, Double>> comparator = Map.Entry.comparingByValue();
                HashMap<ItemStack, Double> costs_name = new HashMap<>();
                for (Cost temp : Vindex.costs.getCosts(this_cost)) {
                    ItemStack temp_2 = new ItemStack(temp.getItem());
                    costs_name.put(temp_2, (double) temp.getXp());
                }
                Collection<Map.Entry<ItemStack, Double>> list =
                        costs_name.entrySet().stream().sorted(comparator.reversed()).collect(Collectors.toList());
                sender.sendMessage(CommandsOutput.Cost.itemsCostList(list, permissionLevel(sender) >= 1, this_cost));
                break;
        }
        return null;
    }
    protected Void costsCommand(ICommandSender sender, HashMap<String, Object> args) {
        args.put("cost.next", "list");
        costCommand(sender,args);
        return null;
    }

    protected Void rewardCommand(ICommandSender sender, HashMap<String, Object> args) {

        double k = args.containsKey("k") ? (double) args.get("k") : -1;
        boolean this_reward = args.containsKey("is_this");
        switch ((String) args.get("reward.next")) {
            case "add": {
                int money = (Integer) args.get("money");
                if (Vindex.rewards.Find(k, this_reward) != null) {
                    sender.sendMessage(CommandsOutput.Reward.errExistsReward());
                    break;
                }
                if (!Vindex.rewards.Add(new Reward((double) money), k, this_reward)) {
                    sender.sendMessage(CommandsOutput.Reward.errorRewAdd());
                    break;
                }
                VindexInventory inv = new VindexInventory();
                inv.setTypeLabel("add")
                        .setReward(k)
                        .setIsThisList(this_reward);
                ((EntityPlayer) sender).displayGUIChest(inv);
                break;
            }
            case "remove": {
                if (Vindex.rewards.Find(k, this_reward) == null) {
                    sender.sendMessage(CommandsOutput.Reward.errRewardNotFound());
                    break;
                }
                if (Vindex.rewards.Remove(k, this_reward)) {
                    sender.sendMessage(CommandsOutput.Reward.successRewRemove(this_reward));
                } else {
                    sender.sendMessage(CommandsOutput.Reward.errorRewRemove());
                }
                Vindex.rewards.save();
                break;
            }
            case "set": {
                if (Vindex.rewards.Find(k, this_reward) == null) {
                    sender.sendMessage(CommandsOutput.Reward.errRewardNotFound());
                    break;
                }
                VindexInventory inv = new VindexInventory();
                inv.setTypeLabel("edit")
                        .setReward(k)
                        .setIsThisList(this_reward);
                List<ItemStack> temp = new ArrayList<>();
                for (Reward.ItemReward item : Vindex.rewards.Find(k, this_reward).items) {
                    ItemStack itemStack = new ItemStack(item.item);
                    temp.add(itemStack);
                }
                inv.LoadItems(temp);
                ((EntityPlayer) sender).displayGUIChest(inv);
                break;
            }
            case "list": {
                this_reward = !args.containsKey("is_future");
                Comparator<Map.Entry<Double, List<ItemStack>>> comparator = Map.Entry.comparingByKey();
                HashMap<Double, List<ItemStack>> costs_name = new HashMap<>();
                HashMap<Double, Double> money = new HashMap<>();
                for (Map.Entry<Double, Reward> temp : Vindex.rewards.getRewards(this_reward).entrySet()) {
                    List<ItemStack> items = new ArrayList<>();
                    for (Reward.ItemReward item : temp.getValue().items) {
                        ItemStack temp_2 = new ItemStack(item.item);
                        items.add(temp_2);
                    }

                    costs_name.put(temp.getKey(), items);
                    money.put(temp.getKey(), temp.getValue().money);
                }
                Collection<Map.Entry<Double, List<ItemStack>>> list =
                        costs_name.entrySet().stream().sorted(comparator).collect(Collectors.toList());

                sender.sendMessage(CommandsOutput.Reward.itemsRewardList(list, money, permissionLevel(sender) >= 2, this_reward));

                break;
            }
            default: {
                sender.sendMessage(CommandsOutput.Reward.rewardArgs(permissionLevel(sender) >= 2));
                break;
            }
        }
        return null;

    }
    protected Void rewardsCommand(ICommandSender sender, HashMap<String, Object> args) {
        args.put("reward.next", "list");
        rewardCommand(sender,args);
        return null;
    }

    protected Void worthCommand(ICommandSender sender,HashMap<String, Object> args) {
        ItemStack stack = getHeldItem(sender);
        if (stack.isEmpty()) {
            sender.sendMessage(CommandsOutput.Cost.needItemInHand());
            return null;
        }
        Cost temp = Vindex.costs.Find(stack.serializeNBT(), true);
        sender.sendMessage(CommandsOutput.Worth.itemPrice(temp != null ? temp.getXp() : 0));
        return null;
    }

    protected Void helpCommand(ICommandSender sender, HashMap<String, Object> args) {
        String command = (String) args.get("command");

        if (command == null) {
            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.help"));
            sender.sendMessage(CommandsOutput.Common.commandsList(getAllowedCommands(sender)));
            return null;
        }
        switch (command) {
            case ("cost"): {
                List<String> messages = new ArrayList<String>();
                List<String> commands = new ArrayList<String>();
                messages.add((new TextComponentTranslation("command.vindex.vi.cost.help")).getFormattedText());
                if (CheckPermissions.checkPermission(sender, "vindex.command.cost.add", false)) {
                    commands.add("cost add <reward> [item] [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.cost.add.help")).getFormattedText());
                }
                if (CheckPermissions.checkPermission(sender, "vindex.command.cost.remove", false)) {
                    commands.add("cost remove [item] [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.cost.remove.help")).getFormattedText());
                }
                if (CheckPermissions.checkPermission(sender, "vindex.command.cost.list", false)) {
                    commands.add("cost list [-f]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.costs.help")).getFormattedText());
                }

                sender.sendMessage(CommandsOutput.Help.helpMessage(messages.toArray(), commands.toArray()));
                break;
            }
            case ("costs"): {
                if (CheckPermissions.checkPermission(sender, "vindex.command.cost.list")) {
                    sender.sendMessage(CommandsOutput.Help.helpMessage(
                            (new TextComponentTranslation("command.vindex.vi.costs.help")).getFormattedText()));
                }
                break;
            }
            case ("reward"): {
                List<String> messages = new ArrayList<String>();
                List<String> commands = new ArrayList<String>();
                messages.add((new TextComponentTranslation("command.vindex.vi.reward.help")).getFormattedText());
                if (CheckPermissions.checkPermission(sender, "vindex.command.reward.add", false)) {
                    commands.add("reward add <k> <money> [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.reward.add.help")).getFormattedText());
                }
                if (CheckPermissions.checkPermission(sender, "vindex.command.reward.remove", false)) {
                    commands.add("reward remove <k> [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.reward.remove.help")).getFormattedText());
                }
                if (CheckPermissions.checkPermission(sender, "vindex.command.reward.set", false)) {
                    commands.add("reward set <k> [-t]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.reward.set.help")).getFormattedText());
                }
                commands.add("reward list [-f]");
                messages.add((new TextComponentTranslation("command.vindex.vi.rewards.help")).getFormattedText());
                sender.sendMessage(CommandsOutput.Help.helpMessage(messages.toArray(), commands.toArray()));
                break;
            }
            case ("rewards"): {
                sender.sendMessage(CommandsOutput.Help.helpMessage(
                        new TextComponentTranslation("command.vindex.vi.rewards.help").getFormattedText()
                ));
                break;
            }
            case ("worth"): {
                if (CheckPermissions.checkPermission(sender, "vindex.command.worth")) {
                    sender.sendMessage(CommandsOutput.Help.helpMessage(
                            (new TextComponentTranslation("command.vindex.vi.worth.help")).getFormattedText()));
                }
                break;
            }
            case ("top"): {
                sender.sendMessage(CommandsOutput.Help.helpMessage(
                        (new TextComponentTranslation("command.vindex.vi.top.help")).getFormattedText()));
                break;
            }
            case ("config"): {
                if (CheckPermissions.checkPermission(sender, "vindex.command.config")) {
                    List<String> messages = new ArrayList<String>();
                    List<String> commands = new ArrayList<String>();
                    messages.add((new TextComponentTranslation("command.vindex.vi.config.help")).getFormattedText());
                    if (CheckPermissions.checkPermission(sender, "vindex.command.config.load", false)) {
                        commands.add("config load");
                        messages.add((new TextComponentTranslation("command.vindex.vi.config.load.help")).getFormattedText());
                    }
                    if (CheckPermissions.checkPermission(sender, "vindex.command.config.save", false)) {
                        commands.add("config save");
                        messages.add((new TextComponentTranslation("command.vindex.vi.config.save.help")).getFormattedText());
                    }
                    sender.sendMessage(CommandsOutput.Help.helpMessage(messages.toArray(), commands.toArray()));
                }
                break;
            }
            case ("help"): {
                sender.sendMessage(CommandsOutput.Help.helpMessage(
                        (new TextComponentTranslation("command.vindex.vi.help.help")).getFormattedText()));
                break;
            }
            case ("stop"): {
                if (CheckPermissions.checkPermission(sender, "vindex.seasons.stop")) {
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
        return null;
    }

    private ItemStack getHeldItem(ICommandSender sender) {
        Entity entity = sender.getCommandSenderEntity();
        ItemStack stack = ItemStack.EMPTY;
        if (entity instanceof EntityPlayer) stack = ((EntityPlayer) entity).getHeldItemMainhand();
        return stack;
    }
}
