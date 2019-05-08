package solar.rpg.chatter.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import solar.rpg.chatter.Main;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Insult Module discourages the often common trolls.
 * It silently disables their chat, and bombards them with annoying insults.
 * Over time, it will make the player leave, as they can no longer troll.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class InsultModule extends Module {

    /* Pre-defined list of insults that Chatterbot will PM its targets. */
    private static final String[] insults = {
            "You're from Planet Minecraft and you need Op?!",
            "Please don't DDoS me. I'm very fragile.",
            "How old are you again? Thirteen years?",
            "Stop being a script kiddie and fuck off.",
            "You're a disgrace to the progression of humanity.",
            "Do you even know how the Internet works?",
            "Find another server to waste your time on.",
            "I've disabled your chat. Don't bother talking.",
            "I'm tracing your IP. Please stay online!",
            "The only time you're wasting is your own.",
            "You mark the lowest end of the gene pool.",
            "Please don't WireShark my address! Please!!",
            "You play Minecraft and talk shit? Whoa, cool kid.",
            "The only way to stop this is to disconnect.",
            "You're that special kind of retarded, right?",
            "You're not wanted here. Fuck off.",
            "Learn some manners before playing on a new server.",
            "Isn't this just as annoying as you?",
            "The LAST thing we need to be worrying about right now is you.",
            "Leave, and for fuck sake, don't return please.",
            "You're an inconsiderate, over-privileged cunt.",
            "How does it feel getting owned by a piece of software?",
            "Go back to Fortnite."
    };

    /* A set of players who will receive a random insult every 0.5 seconds. */
    private Set<UUID> targets;

    public InsultModule(Main PLUGIN) {
        super(PLUGIN);
        targets = new HashSet<>();
        insultTask();
    }

    /**
     * Runs a task timer every .5 seconds.
     * Any players online who are part of the targets list will receive a random insult.
     */
    private void insultTask() {
        new BukkitRunnable() {
            public void run() {
                for (UUID target : targets)
                    if (Bukkit.getOfflinePlayer(target).isOnline())
                        PLUGIN.pm(insults[RNG.nextInt(insults.length)], Bukkit.getPlayer(target));
            }
        }.runTaskTimer(PLUGIN, 0L, 10L);
    }

    @EventHandler
    public void onChatCancel(AsyncPlayerChatEvent event) {
        // Stops silenced people from chatting.
        // Logging level FINEST is used for irony because whatever the targets say is usually garbage.
        if (targets.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Your chat has been disabled. Learn to use it properly.");
            Main.log(Level.FINEST, String.format("Silenced message from %s: %s", event.getPlayer().getName(), event.getMessage()));
            event.setCancelled(true);
        }
    }

    public void onCommand(String message, Player player) {
        String[] args = message.split(" ");

        // Only allow players with a specific permission to use this command.
        if (args[0].equals("!spam") && player.hasPermission("skytopia.trusted")) {
            try {
                // Either add or remove the target player from the set.
                OfflinePlayer pl = Bukkit.getOfflinePlayer(args[1]);
                if (targets.contains(pl.getUniqueId())) {
                    targets.remove(pl.getUniqueId());
                    PLUGIN.say("No longer silencing '" + pl.getName() + "'!");
                } else {
                    targets.add(pl.getUniqueId());
                    PLUGIN.say("Starting to silence '" + pl.getName() + "'!");
                }
            } catch (Exception ex) {
                // This shouldn't happen.
                PLUGIN.say("I was unable to perform the action you requested.");
                ex.printStackTrace();
            }
        }
    }
}