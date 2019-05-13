package solar.rpg.chatter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import solar.rpg.chatter.modules.GithubModule;
import solar.rpg.chatter.modules.InsultModule;
import solar.rpg.chatter.modules.Module;
import solar.rpg.chatter.modules.WelcomeModule;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main plugin class. Acts as entry point and central point of plugin.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class Main extends JavaPlugin implements Listener {

    /* How Chatterbot will appear in the chat. */
    private static final String NAME = ChatColor.GRAY + "SkyBot";
    private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Islander" + ChatColor.DARK_GRAY + "]";
    private static final String SUFFIX = ChatColor.DARK_GRAY + " » " + ChatColor.GRAY;

    /* Keep a static instance of the logger after enabling so all classes can log. */
    private static Logger logger;

    /* Active Chatterbox modules. */
    private Set<Module> modules;

    /* Random number generator. */
    private Random RNG;

    /**
     * Global logging method. Prints out to console with Shoptopia prefix.
     *
     * @param level Logging level.
     * @param msg   Message to log.
     */
    public static void log(Level level, String msg) {
        logger.log(level, String.format("[Chatterbot] %s", msg));
    }

    public void onEnable() {
        logger = getLogger();
        log(Level.FINE, String.format("Enabling Chatterbot v%s!", getDescription().getVersion()));
        RNG = new Random();
        modules = new HashSet<>();

        // Register chat events so commands can be passed off to the modules.
        getServer().getPluginManager().registerEvents(this, this);

        modules.add(new InsultModule(this));
        modules.add(new WelcomeModule(this));
        modules.add(new GithubModule(this));
    }

    /**
     * @return Instance of the random number generator.
     */
    public Random getRNG() {
        return RNG;
    }

    /**
     * Makes the Chatterbot send a message to the global chat.
     *
     * @param msg Message to say.
     */
    public void say(String msg) {
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.broadcastMessage(PREFIX + NAME + SUFFIX + msg), 1L);
    }

    /**
     * Individually private messages a player with the CommandBook PM format.
     *
     * @param msg    Message to send.
     * @param player Player to PM.
     */
    public void pm(String msg, Player player) {
        Bukkit.getScheduler().runTaskLater(this, () -> player.sendMessage(ChatColor.GRAY + "(From " + PREFIX + NAME + ChatColor.GRAY + "): " + ChatColor.WHITE + msg), 1L);
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        for (Module module : this.modules)
            module.onCommand(event.getMessage(), event.getPlayer());
    }
}