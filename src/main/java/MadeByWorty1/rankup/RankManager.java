package MadeByWorty1.rankup;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class RankManager {

    private final Map<String, RankData> ranks = new HashMap<>();

    public RankManager(JavaPlugin plugin) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ranks");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String next = section.getString(key + ".next");
            int price = section.getInt(key + ".price", 0);
            String display = section.getString(key + ".display", key);
            boolean last = section.getBoolean(key + ".last", false);

            ranks.put(
                    key.toLowerCase(),
                    new RankData(next, price, display, last)
            );
        }
    }

    public RankData getRank(String group) {
        return ranks.get(group.toLowerCase());
    }

    public record RankData(
            String next,
            int price,
            String display,
            boolean last
    ) {}
}