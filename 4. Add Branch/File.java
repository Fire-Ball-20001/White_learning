
public class SeasonDaemon {
    private Long old_notify = null;
    List<Map.Entry<Long, String>> notifications;
    private void broadcast(ITextComponent text) {
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(text);
    }

    public long getLastTime()
    {
        Instant next = getNextAward();
        long lastMilis =  Date.from(next).getTime() - Date.from(Instant.now()).getTime();
        if(lastMilis>0)
        {
            return TimeUnit.MILLISECONDS.toSeconds(lastMilis);
        }
        return -1;
    }
    public Instant getNextAward()
    {
        return  Vindex.database.previousAward.plus(
                Vindex.config.rewardPeriod.value,
                Vindex.config.rewardPeriod.asTemporal());
    }
    public void updateNotifications()
    {
        List<Map.Entry<Long, String>> all_notifications = Vindex.config.NOTIFICATIONS.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
        Collections.reverse(all_notifications);
        notifications = new ArrayList<>();
        long last_time = getLastTime();
        for(Map.Entry<Long, String> notify : all_notifications)
        {
            if(notify.getKey()<=last_time)
            {
                notifications.add(notify);
            }
        }
    }


    public void tick() {

        Instant now = Instant.now();
        Instant next = getNextAward();
        long lastTime = getLastTime();
        if (lastTime>=0 && notifications.size()!=0) {
            if (lastTime < notifications.get(0).getKey()) {
                broadcast(CommandsOutput.Season.seasonMessage(notifications.get(0).getValue()));
                notifications.remove(0);
            }
        }
        if (next.isAfter(now)) return;
        try {
            List<GVindexDatabase.PlayerStatistics> stats = Vindex.database.stats();
            stats.removeIf(x -> x.current < Vindex.config.minPoints);
            Vindex.scoreboard.clear();
            if (stats.isEmpty() || stats.size() < Vindex.config.minPlayers) {
                broadcast(CommandsOutput.Season.failedSeason(stats.size(), Vindex.config.minPlayers));
                return;
            }

            stats.sort(ScoreboardDaemon.statCompare);
            broadcast(CommandsOutput.Season.successSeason(stats.size()));

            try {
                for (int i = 0; i < stats.size(); i++) {
                    GVindexDatabase.PlayerStatistics cur = stats.get(i);
                    double quantile = (i + 1) * 1. / stats.size();

                    Reward best = getBestReward(quantile);

                    EntityPlayerMP p = Vindex.instance.server.getPlayerList().getPlayerByUUID(UUID.fromString(cur.UUID));
                    if (p != null) {

                        if(best == Reward.NO)
                        {
                            p.sendMessage(CommandsOutput.Season.winnerSeason(i+1,true));
                        }
                        else {
                            String uuid = p.getUniqueID().toString();
                            if(Vindex.config.isSumRewards)
                            {
                                Vindex.save_rewards.set(uuid, sumRewards(Vindex.save_rewards.get(uuid), best));
                            }
                            else
                            {
                                Vindex.save_rewards.set(uuid, best);
                            }

                            Vindex.save_rewards.save();
                            p.sendMessage(CommandsOutput.Season.winnerSeason(i+1,false));
                        }
                    }
                    else
                    {
                        Vindex.save_rewards.set(cur.UUID, sumRewards(Vindex.save_rewards.get(cur.UUID), best));
                    }
                }
            } finally {
                for (GVindexDatabase.PlayerStatistics c : stats) {
                    c.total += c.current;
                    c.current = 0.0;
                }
            }
            Vindex.costs.SwapCost();
            Vindex.rewards.SwapReward();
        } finally {
            Vindex.database.previousAward = now;
            updateNotifications();
        }
    }


    private Reward getBestReward(double quantile) {

        for (Map.Entry<Double, Reward> e : Vindex.rewards.getRewards(true).entrySet())
            if (e.getKey() < quantile) return e.getValue();
        return Reward.NO;
    }

    private Reward sumRewards(Reward first, Reward second) {
        double money = first.money + second.money;
        List<Reward.ItemReward> items = sumItems(first.items,second.items);
        return new Reward(money, items);
    }

    private List<Reward.ItemReward> sumItems(List<Reward.ItemReward> temp_1, List<Reward.ItemReward> temp_2)
    {
        List<ItemStack> stacks = new ArrayList<>();

        for(Reward.ItemReward i : temp_1)
        {
            stacks.add(new ItemStack(i.item));
        }
        for (Reward.ItemReward i: temp_2)
        {
            ItemStack item = new ItemStack(i.item);
            int j = 0;
            boolean check = false;
            for(j = 0;j<stacks.size();j++)
            {
                if(stacks.get(j).isItemEqual(item))
                {
                    if(stacks.get(j).getCount()+item.getCount()>item.getMaxStackSize())
                    {
                        stacks.add(stacks.get(j).copy());
                        stacks.get(stacks.size()-1).setCount(stacks.get(j).getCount()+item.getCount()-item.getMaxStackSize());
                        stacks.get(j).setCount(item.getMaxStackSize());
                    }
                    else
                    {
                        stacks.get(j).setCount(stacks.get(j).getCount()+item.getCount());
                    }
                    check = true;
                    break;
                }

            }

            if(!check)
            {
                stacks.add(item);
            }
        }
        List<Reward.ItemReward> res = new ArrayList<>();
        for(ItemStack i : stacks)
        {
            res.add(new Reward.ItemReward(i.serializeNBT()));
        }
        return res;
    }

}
