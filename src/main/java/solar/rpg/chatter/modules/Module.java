package solar.rpg.chatter.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import solar.rpg.chatter.Main;

import java.util.Random;

/**
 * Each chatterbot module has a different purpose.
 * If it implements Listener, it will register events for it.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public abstract class Module {

    /* Reference to JavaPlugin. */
    protected final Main PLUGIN;

    /* Reference to random number generator. */
    protected final Random RNG;

    protected Module(Main PLUGIN) {
        this.PLUGIN = PLUGIN;
        RNG = PLUGIN.getRNG();
        if (this instanceof Listener)
            PLUGIN.getServer().getPluginManager().registerEvents((Listener) this, PLUGIN);
    }

    /**
     * Passed in by Main from an AsyncPlayerChatEvent.
     *
     * @param message What was said in chat.
     * @param player Who said it.
     */
    public abstract void onCommand(String message, Player player);
}