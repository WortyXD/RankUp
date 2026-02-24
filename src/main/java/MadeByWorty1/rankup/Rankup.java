package MadeByWorty1.rankup;

import net.milkbowl.vault.economy.Economy;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Rankup extends JavaPlugin {

    private Economy economy;
    private LuckPerms luckPerms;
    private RankManager rankManager;

    private final Map<UUID, Long> confirmMap = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!setupVault()) {
            getLogger().severe("Vault bulunamadı!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        luckPerms = LuckPermsProvider.get();
        rankManager = new RankManager(this);
    }

    private boolean setupVault() {
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(color(getConfig().getString("messages.only-player")));
            return true;
        }

        String prefix = color(getConfig().getString("prefix"));
        int confirmTime = getConfig().getInt("settings.confirm-time", 60);

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            player.sendMessage(prefix + "§cVerilerin yüklenirken bir hata oluştu!");
            return true;
        }

        RankManager.RankData current = null;

        for (var node : user.getNodes()) {
            if (node instanceof net.luckperms.api.node.types.InheritanceNode inheritanceNode) {
                String groupName = inheritanceNode.getGroupName();
                RankManager.RankData rankData = rankManager.getRank(groupName);

                if (rankData != null) {
                    if (current == null || rankData.price() > current.price()) {
                        current = rankData;
                    }
                }
            }
        }

        if (current == null) {
            String def = getConfig().getString("settings.default-rank");
            current = rankManager.getRank(def);

            if (current == null) {
                player.sendMessage(prefix + "§cDefault rütbe config'te bulunamadı!");
                return true;
            }
        }

        if (current.last()) {
            player.sendMessage(prefix + color(getConfig().getString("messages.max-rank")));
            return true;
        }

        RankManager.RankData nextRank = rankManager.getRank(current.next());
        if (nextRank == null) {
            player.sendMessage(prefix + "§cSonraki rütbe bulunamadı!");
            return true;
        }

        double balance = economy.getBalance(player);
        String priceEmoji = balance >= nextRank.price() ? "§a✔" : "§c✘";

        if (confirmMap.containsKey(player.getUniqueId())) {
            long confirmTimeLeft = (confirmMap.get(player.getUniqueId()) + (confirmTime * 1000L)) - System.currentTimeMillis();

            if (confirmTimeLeft <= 0) {
                confirmMap.remove(player.getUniqueId());
                player.sendMessage(prefix + color("&cOnay süresi doldu! Tekrar /rankup yazın."));
                return true;
            }

            if (balance < nextRank.price()) {
                player.sendMessage(prefix + color(getConfig().getString("messages.not-enough-money")));
                confirmMap.remove(player.getUniqueId());
                return true;
            }

            economy.withdrawPlayer(player, nextRank.price());

            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "lp user " + player.getName() + " parent add " + current.next()
            );

            String broadcast = getConfig().getString("messages.broadcast")
                    .replace("%player%", player.getName())
                    .replace("%rank%", nextRank.display());

            Bukkit.broadcastMessage(prefix + color(broadcast));
            confirmMap.remove(player.getUniqueId());
            return true;
        }

        player.sendMessage(prefix + color(getConfig().getString("messages.info-title")));
        player.sendMessage(prefix + color(getConfig().getString("messages.next-rank")
                .replace("%rank%", nextRank.display())));

        player.sendMessage(prefix + color(getConfig().getString("messages.price")
                .replace("%emoji%", priceEmoji)
                .replace("%price%", String.valueOf(nextRank.price()))));

        player.sendMessage(prefix + color(getConfig().getString("messages.balance")
                .replace("%balance%", String.valueOf((int) balance))));
        player.sendMessage(prefix + color(getConfig().getString("messages.confirm")
                .replace("%time%", String.valueOf(confirmTime))));

        confirmMap.put(player.getUniqueId(), System.currentTimeMillis());

        Bukkit.getScheduler().runTaskLater(
                this,
                () -> confirmMap.remove(player.getUniqueId()),
                20L * confirmTime
        );

        return true;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}