package bot.rat.messages;

import bot.rat.Bot;
import bot.rat.Jokes;
import bot.rat.entities.UserEntity;
import bot.rat.services.UserService;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MessageHandler {

    List<String> firstHalfUni = Arrays.asList("U+0078", "U+0058", "U+0425","U+0445","U+04FC", "U+04FD", "U+04FE", "U+04FF");
    List<String> secondHalfUni = Arrays.asList("U+0044", "U+0064", "U+00D0", "U+00FE", "U+010E", "U+010F",
            "U+0110", "U+0111", "U+0189", "U+018A", "U+01A2", "U+01F3", "U+01F2", "U+01F1", "U+01F7",
            "U+024A", "U+024B", "U+0071", "U+1E0A", "U+1E0B", "U+03F7", "U+03F8", "U+044C");
    String illegalStringSeperators = "[ !,.?]";

    List<String> admins = Arrays.asList("234042381249413130");
    HashSet<String> muteds = new HashSet<>();
    HashSet<String> cheeseList = new HashSet<>();
    Boolean xdIllegal = false;
    Boolean cheese = false;
    Jokes jokes = new Jokes();
    Boolean commandsDisabled = false;
    Boolean replyXd = false;
    Commands commands = new Commands();

    @Autowired
    UserService userService;

    public MessageHandler() throws IOException {
    }

    public boolean isAuthorAdmin(@Nonnull GuildMessageReceivedEvent event) {
        return (admins.contains(event.getAuthor().getId()));
    }

    public boolean isAuthorMuted(@Nonnull GuildMessageReceivedEvent event) {
        return (muteds.contains(event.getAuthor().getId()));
    }

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        UserEntity user = userService.getUserById(event.getAuthor().getId());
        String msg = event.getMessage().getContentRaw();
        try {
            if (!event.getAuthor().isBot()) {
                if (muteHandler(event)) {
                    return;
                }
                if (msg.length() > 4 && msg.substring(0, 5).equals("!rat ")) {
                    commandHandler(msg = msg.substring(5), event);
                } else {
                    if (cheese) {
                        cheeseHandler(event);
                    }
                    manageIllegalMessage(event);
                }
            }
        } catch (Exception e) {
            System.out.println("GuildMessageReceived error with message: " + msg);
        }
    }

    private void manageIllegalMessage(@Nonnull GuildMessageReceivedEvent event) {
//        char[] chars = event.getMessage().getContentRaw().replaceAll(illegalStringSeperators, "").toCharArray();
//        boolean xFound = false;
//        for (char c : chars) {
//            if (xFound) {
//                if (secondHalfUni.contains(String.format("U+%04X", (int) c))) {
//                    if (xdIllegal && !isAuthorAdmin(event)) {
//                        event.getMessage().delete().queue();
//                    } else {
//                        if (replyXd) {
//                            event.getMessage().getChannel().sendMessage("xd").queue();
//                        }
//                    }
//                    return;
//                }
//            }
//            if (firstHalfUni.contains(String.format("U+%04X", (int) c))) {
//                xFound = true;
//            } else {
//                xFound = false;
//            }
//        }
        if (event.getMessage().getContentRaw().toLowerCase().contains("xd")) {
            if (xdIllegal && !isAuthorAdmin(event)) {
                event.getMessage().delete().queue();
            } else if (replyXd) {
                event.getMessage().getChannel().sendMessage("xd").queue();
            }
        }
    }

    private void commandHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        if (!commandsDisabled) {
            if (isAuthorAdmin(event)) {
                adminCommandHandler(message, event);
            }
            userCommandHandler(message, event);
        } else {
            commandEnableHandler(message, event);
        }
    }

    private void adminCommandHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        commands.adminCommandHandler(message, event, this);
    }

    private void userCommandHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        commands.userCommandHandler(message, event, this);
    }

    private void commandEnableHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        if (message.equals("ec") || message.equals("enable commands")) {
            commandsDisabled = commands.enableCommands(event);
        }
    }

    private boolean muteHandler(@Nonnull GuildMessageReceivedEvent event) throws RateLimitedException {
        if (isAuthorMuted(event)) {
            event.getMessage().delete().complete(true);
            return true;
        }
        return false;
    }

    private void cheeseHandler(@Nonnull GuildMessageReceivedEvent event) {
        event.getMessage().addReaction("U+1F9C0").queue();
        if (event.getMessage().getContentRaw().toLowerCase().contains("cheese")) {
            String id = event.getAuthor().getId();
            if (cheeseList.add(id)) {
                event.getMessage().getChannel().sendMessage(
                        "<@" + id + "> has been added to the cheese list.").queue();
            }
        }
    }

    /**
     *  Auto generated generic code below
     */

    public Boolean getXdIllegal() {
        return xdIllegal;
    }

    public void setXdIllegal(Boolean xdIllegal) {
        this.xdIllegal = xdIllegal;
    }

    public Boolean getCheese() {
        return cheese;
    }

    public void setCheese(Boolean cheese) {
        this.cheese = cheese;
    }

    public Boolean getCommandsDisabled() {
        return commandsDisabled;
    }

    public void setCommandsDisabled(Boolean commandsDisabled) {
        this.commandsDisabled = commandsDisabled;
    }

    public Boolean getReplyXd() {
        return replyXd;
    }

    public void setReplyXd(Boolean replyXd) {
        this.replyXd = replyXd;
    }

    public HashSet<String> getMuteds() {
        return muteds;
    }

    public Jokes getJokes() {
        return jokes;
    }
}
