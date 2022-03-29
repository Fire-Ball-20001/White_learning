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

    private String node = "vindex.command";

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
        if (!checkPermission(sender, node)) return;
        node += ".";
        Map<String, Double> costs = Vindex.config.costs;
        if (args.length == 0) {
            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.list_commands"));
            for (String row : commands[permissionLevel(sender)]) sender.sendMessage(new TextComponentString(row));
            return;
        }
        switch (args[0]) {
            case ("config"):
                node += "config";
                if (!checkPermission(sender, node)) return;
                if (args.length != 2) {
                    sender.sendMessage(error_Args("/vi config load/save"));
                    break;
                }
                node += ".";
                switch (args[1]) {
                    case ("load"):
                        node += "load";
                        if (!checkPermission(sender, node)) return;
                        Vindex.config.load();
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.config.load"));
                        break;
                    case ("save"):
                        node += "save";
                        if (!checkPermission(sender, node)) return;
                        Vindex.config.save();
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.config.save"));
                        break;
                    default:
                        sender.sendMessage(error_Args("/vi config load/save"));
                        break;
                }
                break;
            case ("item"): {
                node += "item";
                if (!checkPermission(sender, node)) return;
                if (args.length < 2) {
                    sender.sendMessage(error_Args("/vi item <add[<reward>]/remove/list>"));
                    break;
                }
                ItemStack stack = getHeldItem(sender);
                if (!args[1].equals("list") && stack.isEmpty()) {
                    sender.sendMessage(new TextComponentTranslation("command.vindex.vi.item.error.no_item"));
                    break;
                }
                String name = stack.getItem().getRegistryName().toString();
                node += ".";
                switch (args[1]) {
                    case ("add"):
                        node += "add";
                        if (!checkPermission(sender, node)) return;
                        if (args.length != 3) {
                            if(permissionLevel(sender)>=1)
                            {
                                sender.sendMessage(error_Args("/vi item <add[<reward>]/remove/list> "));
                            }
                            else
                            {
                                sender.sendMessage(error_Args("/vi item list"));
                            }
                            break;
                        }
                        if (costs.containsKey(name)) {
                            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.item.add.error"));
                            break;
                        }
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.item.add.success"));
                        Vindex.config.costs.put(name, Double.parseDouble(args[2]));
                        Vindex.config.save();
                        break;
                    case ("remove"):
                        node += "remove";
                        if (!checkPermission(sender, node)) return;
                        if (args.length != 2 && args.length != 3) {
                            sender.sendMessage(error_Args("/vi item <add[<reward>]/remove/list>"));
                            break;
                        }
                        if (!costs.containsKey(name)) {
                            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.item.remove.error"));
                            break;
                        }
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.item.remove.success"));
                        Vindex.config.costs.remove(name);
                        Vindex.config.save();
                        break;
                    case ("list"):
                        node += "list";
                        if (!checkPermission(sender, node)) return;
                        Comparator<Map.Entry<String, Double>> comparator = Map.Entry.comparingByValue();
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.items.use"));
                        for (Map.Entry<String, Double> e : Vindex.config.costs.entrySet().stream().sorted(comparator.reversed()).collect(Collectors.toList()))
                            sender.sendMessage(new TextComponentString(String.format("%s -> %s", e.getKey(), e.getValue())));
                        break;
                }
                break;
            }
            case ("items"): {
                node += "item.list";
                if (!checkPermission(sender, node)) return;
                Comparator<Map.Entry<String, Double>> comparator = Map.Entry.comparingByValue();
                sender.sendMessage(new TextComponentTranslation("command.vindex.vi.items.use"));
                for (Map.Entry<String, Double> e : Vindex.config.costs.entrySet().stream().sorted(comparator.reversed()).collect(Collectors.toList()))
                    sender.sendMessage(new TextComponentString(String.format("%s -> %s", e.getKey(), e.getValue())));
                break;
            }
            case ("worth"): {
                node += "worth";
                if (!checkPermission(sender, node)) return;
                ItemStack stack = getHeldItem(sender);
                if (stack.isEmpty()) return;
                String name = stack.getItem().getRegistryName().toString();
                if (costs.containsKey(name)) sender.sendMessage(new TextComponentTranslation("command.vindex.vi.worth.good", "§b§l"+costs.get(name)));
                else sender.sendMessage(new TextComponentTranslation("command.vindex.vi.worth.no_cost"));
                break;
            }
            case ("help"):
            {
                node+="help";
                if(!checkPermission(sender,node))
                {
                    return;
                }
                if(args.length==1)
                {
                    sender.sendMessage(new TextComponentTranslation("command.vindex.vi.help"));
                    sender.sendMessage(new TextComponentTranslation("command.vindex.vi.list_commands"));
                    for (String row : commands[permissionLevel(sender)]) sender.sendMessage(new TextComponentString(row));
                    break;
                }
                else if(args.length != 2)
                {
                    sender.sendMessage(error_Args("/vi help [command]"));
                    break;
                }
                switch (args[1]){
                    case ("item"): {
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.item.help"));
                        if(checkPermission(sender,"vindex.command.item.add",false))
                        {
                            sender.sendMessage(new TextComponentString("§e|     \"item add <reward>\" - "+new TextComponentTranslation("command.vindex.vi.item.add.help").getFormattedText()));
                        }
                        if(checkPermission(sender,"vindex.command.item.remove",false))
                        {
                            sender.sendMessage(new TextComponentString("§e|    \"item remove\" - "+new TextComponentTranslation("command.vindex.vi.item.remove.help").getFormattedText()));
                        }
                        if(checkPermission(sender,"vindex.command.item.list",false))
                        {
                            sender.sendMessage(new TextComponentString("§e|    \"item list\" - "+new TextComponentTranslation("command.vindex.vi.items.help").getFormattedText()));
                        }
                        break;
                    }
                    case("items"): {
                        if(checkPermission(sender,"vindex.command.item.list"))
                        {
                            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.items.help"));
                        }
                        break;
                    }
                    case("worth"): {
                        if(checkPermission(sender,"vindex.command.worth"))
                        {
                            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.worth.help"));
                        }
                        break;
                    }
                    case("config"):
                    {
                        if(checkPermission(sender,"vindex.command.config"))
                        {
                            sender.sendMessage(new TextComponentTranslation("command.vindex.vi.config.help"));
                            if(checkPermission(sender,"vindex.command.config.load",false))
                            {
                                sender.sendMessage(new TextComponentString("§e|    \"config load\" - " + new TextComponentTranslation("command.vindex.vi.config.load.help").getFormattedText()));
                            }
                            if(checkPermission(sender,"vindex.command.config.save",false))
                            {
                                sender.sendMessage(new TextComponentString("§e|    \"config save\" - " + new TextComponentTranslation("command.vindex.vi.config.save.help").getFormattedText()));
                            }
                        }
                        break;
                    }
                    case("help"):
                    {
                        sender.sendMessage(new TextComponentTranslation("command.vindex.vi.help.help"));
                        break;
                    }
                    default: {
                        sender.sendMessage(error_Args("/vi help [command]"));
                        break;
                    }
                }
                break;
            }
            default:
                sender.sendMessage(new TextComponentTranslation("command.vindex.vi.error_arg",""));
                for (String row : commands[permissionLevel(sender)]) sender.sendMessage(new TextComponentString("§e"+row));
                break;
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
            sender.sendMessage(new TextComponentTranslation("command.vindex.no_perm"));
        }
        return false;
    }

    private int permissionLevel(ICommandSender sender)
    {
        Entity entity = sender.getCommandSenderEntity();
        if(entity instanceof EntityPlayer)
        {
            if(PermissionAPI.hasPermission((EntityPlayer) entity,"vindex.command.add"))
                return 1;
            else if(PermissionAPI.hasPermission((EntityPlayer) entity,"vindex.command.config"))
                return 2;
            else
                return 0;
        }
        else return 0;

    }
    private TextComponentTranslation error_Args(String args)
    {
        return new TextComponentTranslation("command.vindex.vi.error_arg",
                new TextComponentString("§e"+args));
    }
}
