package solar.rpg.chatter.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import solar.rpg.chatter.Main;

import java.util.*;

/**
 * Welcome Module notifies online players of any first-time players joining.
 * It also hands out rewards for online players that welcome the new player.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class WelcomeModule extends Module implements Listener {

    /* Keeps track of players who are new, and who have welcomed them. */
    private HashMap<String, Set<UUID>> welcomable;

    public WelcomeModule(Main PLUGIN) {
        super(PLUGIN);
        welcomable = new HashMap<>();
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        if ((event.getLoginResult() == org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED) &&
                (!Bukkit.getOfflinePlayer(event.getName()).hasPlayedBefore()))
            // PM everyone online (except console), prompting them to welcome the player.
            for (Player online : Bukkit.getOnlinePlayers())
                if (!online.getName().equals(event.getName()))
                    PLUGIN.pm("Hey, this player is new! Welcome them for a reward.", online);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Give players 15 seconds after login to welcome the player.
        if (!event.getPlayer().hasPlayedBefore()) {
            welcomable.put(event.getPlayer().getName().toLowerCase(), new HashSet<>());
            Bukkit.getScheduler().runTaskLater(PLUGIN, () -> welcomable.remove(event.getPlayer().getName()), 300L);
        }
    }

    public void onCommand(String command, Player player) {
        // Check if there are players to welcome.
        if (welcomable.size() >= 1)
            for (Map.Entry<String, Set<UUID>> entry : welcomable.entrySet())
                // Check if the command contains the new user's name and the phrase 'welcome'.
                if (command.contains(entry.getKey()) && command.toLowerCase().contains("welcome")) {
                    // Check that they haven't already welcomed this user.
                    if (!entry.getValue().contains(player.getUniqueId())) {
                        entry.getValue().add(player.getUniqueId());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " 5000");
                        PLUGIN.pm("Thanks for welcoming this new player!", player);
                    }
                }
    }
}