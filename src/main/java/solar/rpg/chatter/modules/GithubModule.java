package solar.rpg.chatter.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import solar.rpg.chatter.Main;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Github Module listens to a list of watched repositories for new commits.
 * If a new commit is detected, everyone on the server is notified of it.
 *
 * @author lavuh
 * @version 1.1
 * @since 1.0
 */
public class GithubModule extends Module {

    /* The message that Chatterbot says when it detects a new commit.*/
    private static final String COMMIT_MESSAGE = ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE + "%s" + ChatColor.GRAY + " committed to " + "%s on GitHub:\n" + ChatColor.GRAY + ChatColor.ITALIC + "%s";

    /* Array of repositories that will have their commits listened to.*/
    private static final String[] WATCHED_REPOSITORIES = {
            "lavuh/Floating-Anvil",
            "skytopia/Casino",
            "skytopia/Chatterbot",
            "skytopia/Shoptopia",
            "skytopia/Skyblock-Public",
            "skytopia/Skytopia"
    };

    /* Keep the latest SHA */
    private final ConcurrentHashMap<String, String> LATEST_SHA;

    /* GitHub API stuff. */
    private final LinkedList<GHRepository> REPOSITORIES;
    private GitHub GITHUB;

    /* The github task instance. */
    private BukkitTask githubTask;

    public GithubModule(Main PLUGIN) {
        super(PLUGIN);
        this.REPOSITORIES = new LinkedList<>();
        this.LATEST_SHA = new ConcurrentHashMap<>();

        try {
            GITHUB = GitHub.connect();
        } catch (IOException e) {
            Main.log(Level.WARNING, "Unable to connect to GitHub. See stack trace");
            e.printStackTrace();
            return;
        }

        githubTask();
    }

    /**
     * Runs a task that regularly checks for commits from the list of watched repositories.
     */
    private void githubTask() {
        // Clear any existing GitHub repositories.
        REPOSITORIES.clear();

        Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, () -> {
            Main.log(Level.INFO, "Attempting to load in watched repositories...");

            // Attempt to retrieve all watched repositories from GitHub via API.
            for (String repo : WATCHED_REPOSITORIES) {
                try {
                    // Detect and add repository.
                    GHRepository gitRepo = GITHUB.getRepository(repo);
                    REPOSITORIES.add(gitRepo);

                    // Make the latest commit the first one in the list.
                    if (!LATEST_SHA.contains(gitRepo.getName()))
                        LATEST_SHA.put(gitRepo.getName(), gitRepo.listCommits().asList().get(0).getSHA1());
                } catch (Exception e) {
                    Main.log(Level.WARNING, String.format("Unable to watch repository '%s'. Tracker will not operate. See stack trace", repo));
                    e.printStackTrace();
                    return;
                }
            }
            PLUGIN.say("I'm listening to GitHub repositories again!");
        });

        // Watch for new commits every 30 seconds on an async thread.
        githubTask = Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, () -> {
            for (GHRepository repo : REPOSITORIES) {
                try {
                    List<GHCommit> commits = repo.listCommits().asList();

                    // Don't go through new commits if the last known newest commit is still the newest.
                    if (commits.get(0).getSHA1().equals(LATEST_SHA.get(repo.getName()))) continue;

                    List<GHCommit> newCommits = new LinkedList<>();

                    // Go through commits, from oldest to newest, until we reach the latest.
                    for (GHCommit commit : commits)
                        if (!LATEST_SHA.get(repo.getName()).equals(commit.getSHA1()))
                            newCommits.add(commit);
                        else break;

                    LATEST_SHA.put(repo.getName(), commits.get(0).getSHA1());

                    // Notify players of any new commits.
                    //FIXME: Newer commits are displayed first. (minor)
                    for (GHCommit commit : newCommits) {
                        Bukkit.getScheduler().runTask(PLUGIN, () -> {
                            try {
                                PLUGIN.say(newCommitMessage(repo, commit));
                            } catch (IOException e) {
                                Main.log(Level.WARNING, String.format("Unable to show commit for %s. Check stack trace", repo.getName()));
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (GHException e) {
                    Main.log(Level.WARNING, String.format("Unable to show commit for %s. Check stack trace", repo.getName()));
                    e.printStackTrace();
                }
            }
        }, 1200L, 600L);
    }

    /**
     * Takes commit and repo information and formats the commit message that will be displayed.
     *
     * @param repo   GitHub repository.
     * @param commit Commit from repository.
     * @return Formatted message.
     * @throws IOException May encounter an I/O error.
     */
    private String newCommitMessage(GHRepository repo, GHCommit commit) throws IOException {
        return String.format(COMMIT_MESSAGE, commit.getAuthor().getName(), repo.getName(), commit.getCommitShortInfo().getMessage());
    }

    public void onCommand(String command, org.bukkit.entity.Player player) {
        String[] args = command.split(" ");

        // Only allow players with a specific permission to use this command.
        if (args[0].equals("!reloadgit") && player.hasPermission("skytopia.staff")) {
            if (githubTask != null)
                githubTask.cancel();
            PLUGIN.say("I've cancelled the task and will try again.");
            githubTask();
        }
    }
}