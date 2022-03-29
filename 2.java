public class VindexCommand extends CommandBase {

    private final String[][] commands = new String[][]{{
            "/vi item list",
            "/vi items",
            "/vi worth",
            "/vi help [command]"},{
            "/vi item <add/remove/list>",
            "/vi items",
            "/vi worth",
            "/vi help [command]"},{
            "/vi config <load/save>",
            "/vi item <add/remove/list>",
            "/vi items",
            "/vi worth",
            "/vi help [command]"}
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
        Map<String, Double> costs = Vindex.config.costs;
        if (args.length == 0) {
            sender.sendMessage(CommandsOutput.commandsList(commands[permissionLevel(sender)]));
            return;
        }
        switch (args[0]) {
            case ("config"):
                if (args.length != 2) {
                    sender.sendMessage(CommandsOutput.configArgs(permissionLevel(sender)==2));
                    break;
                }

                configCommand(sender, Arrays.copyOfRange(args,1, args.length));
                break;
            case ("item"): {

                if (args.length < 2) {
                    if(permissionLevel(sender)<1)
                    {
                        sender.sendMessage(CommandsOutput.itemsArgs(new String[]{"list"}));
                    }
                    else
                    {
                        sender.sendMessage(CommandsOutput.itemsArgs(new String[]{"add <reward> [item]","remove [item]","list"}));
                    }
                    break;
                }
                itemCommand(sender,Arrays.copyOfRange(args,1,args.length));
                break;
            }
            case ("items"): {
                itemCommand(sender,new String[]{"list"});
                break;
            }
            case ("worth"): {
                worthCommand(sender);
                break;
            }
            case ("help"):
            {
                node = MAIN_PERM+".help";
                if(!checkPermission(sender,node))
                {
                    return;
                }
                helpCommand(sender,Arrays.copyOfRange(args,1,args.length));
                break;
            }
            default:
                sender.sendMessage(CommandsOutput.argsError());
                sender.sendMessage(CommandsOutput.commandsList(commands[permissionLevel(sender)]));
                break;
        }
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
                sender.sendMessage(CommandsOutput.configsReloaded());
                break;
            case ("save"):
                node += "save";
                if (!checkPermission(sender, node)) return;
                Vindex.config.save();
                sender.sendMessage(CommandsOutput.configsSaved());
                break;
            default:
                sender.sendMessage(CommandsOutput.configArgs(permissionLevel(sender)==2));
                break;
        }

    }

    protected void itemCommand(ICommandSender sender, String[] args)
    {
        node = MAIN_PERM+".item";
        if (!checkPermission(sender, node)) return;
        node+=".";
        Map<String, Double> costs = Vindex.config.costs;

        ItemStack stack = getHeldItem(sender);
        String name = "";
        switch (args[0]) {
            case ("add"):
                node+="add";
                if (!checkPermission(sender, node)) return;
                if (args.length < 2) {
                    if(permissionLevel(sender)>=1)
                    {
                        sender.sendMessage(CommandsOutput.itemsArgs(new String[]{"add <reward> [item]","remove [item]","list"}));
                    }
                    else
                    {
                        sender.sendMessage(CommandsOutput.itemsArgs(new String[]{"list"}));
                    }
                    break;
                }
                if (stack.isEmpty() && args.length < 3) {
                    sender.sendMessage(CommandsOutput.needItemInHand());
                    return;
                }
                name = args.length > 2 ? args[2] : stack.getItem().getRegistryName().toString();
                if (costs.containsKey(name)) {
                    sender.sendMessage(CommandsOutput.alreadyInList());
                    break;
                }
                sender.sendMessage(CommandsOutput.addedInList());
                Vindex.config.costs.put(name, Double.parseDouble(args[1]));
                Vindex.config.save();
                break;
            case ("remove"):
                node+="remove";
                if (!checkPermission(sender, node)) return;
                if (args.length >2) {
                    if(permissionLevel(sender)>=1)
                    {
                        sender.sendMessage(CommandsOutput.itemsArgs(new String[]{"add <reward> [item]","remove [item]","list"}));
                    }
                    else
                    {
                        sender.sendMessage(CommandsOutput.itemsArgs(new String[]{"list"}));
                    }
                }
                if (stack.isEmpty() && args.length < 2) {
                    sender.sendMessage(CommandsOutput.needItemInHand());
                    return;
                }
                name = args.length > 1 ? args[1] : stack.getItem().getRegistryName().toString();
                if (!costs.containsKey(name)) {
                    sender.sendMessage(CommandsOutput.noopInList());
                    break;
                }
                sender.sendMessage(CommandsOutput.deletedFromList());
                Vindex.config.costs.remove(name);
                Vindex.config.save();
                break;
            case ("list"):
                node+="list";
                if (!checkPermission(sender, node)) return;
                Comparator<Map.Entry<String, Double>> comparator = Map.Entry.comparingByValue();
                Collection<Map.Entry<String,Double>> list =
                        costs.entrySet().stream().sorted(comparator.reversed()).collect(Collectors.toList());
                sender.sendMessage(CommandsOutput.itemsList(list,permissionLevel(sender)>=1));
                break;
        }
    }

    protected void worthCommand(ICommandSender sender){
        node = MAIN_PERM+".worth";
        Map<String, Double> costs = Vindex.config.costs;
        if (!checkPermission(sender, node)) return;
        ItemStack stack = getHeldItem(sender);
        if (stack.isEmpty()) return;
        String name = stack.getItem().getRegistryName().toString();
        sender.sendMessage(CommandsOutput.itemPrice(costs.containsKey(name) ? costs.get(name) : 0));

    }

    protected void helpCommand(ICommandSender sender, String[] args){

        if(args.length==0)
        {
            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.help"));
            sender.sendMessage(CommandsOutput.commandsList(commands[permissionLevel(sender)]));
            return;
        }
        else if(args.length != 1)
        {
            sender.sendMessage(CommandsOutput.helpArgs());
            return;
        }
        switch (args[0]){
            case ("item"): {
                List<String> messages = new ArrayList<String>();
                List<String> commands = new ArrayList<String>();
                messages.add((new TextComponentTranslation("command.vindex.vi.item.help")).getFormattedText());
                if(checkPermission(sender,"vindex.command.item.add",false))
                {
                    commands.add("item add <reward> [item]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.item.add.help")).getFormattedText());
                }
                if(checkPermission(sender,"vindex.command.item.remove",false))
                {
                    commands.add("item remove [item]");
                    messages.add((new TextComponentTranslation("command.vindex.vi.item.remove.help")).getFormattedText());
                }
                if(checkPermission(sender,"vindex.command.item.list",false))
                {
                    commands.add("item list");
                    messages.add((new TextComponentTranslation("command.vindex.vi.items.help")).getFormattedText());
                }

                sender.sendMessage(CommandsOutput.helpMessage(messages.toArray(),commands.toArray()));
                break;
            }
            case("items"): {
                if(checkPermission(sender,"vindex.command.item.list"))
                {
                    sender.sendMessage(CommandsOutput.helpMessage(
                            (new TextComponentTranslation("command.vindex.vi.items.help")).getFormattedText()));
                }
                break;
            }
            case("worth"): {
                if(checkPermission(sender,"vindex.command.worth"))
                {
                    sender.sendMessage(CommandsOutput.helpMessage(
                            (new TextComponentTranslation("command.vindex.vi.worth.help")).getFormattedText()));
                }
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
                    sender.sendMessage(CommandsOutput.helpMessage(messages.toArray(),commands.toArray()));
                }
                break;
            }
            case("help"):
            {
                sender.sendMessage(CommandsOutput.helpMessage(
                        (new TextComponentTranslation("command.vindex.vi.help.help")).getFormattedText()));
                break;
            }
            default: {
                sender.sendMessage(CommandsOutput.helpArgs());
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

    private boolean checkPermission(ICommandSender sender, String permission, boolean is_output) {
        Entity entity = sender.getCommandSenderEntity();
        if (entity instanceof EntityPlayer && PermissionAPI.hasPermission(((EntityPlayer) entity), permission)
        || sender.getServer().getPlayerList().canSendCommands(((EntityPlayer) sender).getGameProfile())) return true;
        if(is_output) {
            sender.sendMessage(CommandsOutput.accessDenied());
        }
        return false;
    }

    private int permissionLevel(ICommandSender sender)
    {
        Entity entity = sender.getCommandSenderEntity();
        if(entity instanceof EntityPlayer)
        {
            if(checkPermission(entity,"vindex.command.add",false))
                return 1;
            else if(checkPermission( entity,"vindex.command.config",false))
                return 2;
            else
                return 0;
        }
        else return 0;

    }
}
