package com.technovision.technobot.commands.economy;

import com.google.common.collect.Sets;
import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.EconManager;
import com.technovision.technobot.util.exceptions.InvalidBalanceException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CommandDeposit extends Command {

    public CommandDeposit() {
        super("deposit", "Deposit cash into the bank", "{prefix}deposit <amount>", Category.ECONOMY);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        if (args.length > 0) {
            try {
                long amt = Long.parseLong(args[0]);
                try {
                    TechnoBot.getInstance().getEconomy().deposit(event.getAuthor(), amt);
                    embed.setColor(EconManager.SUCCESS_COLOR);
                    String money = EconManager.FORMATTER.format(amt);
                    embed.setDescription(":white_check_mark: Deposited " + EconManager.SYMBOL + money + " to your bank!");
                    event.getChannel().sendMessage(embed.build()).queue();
                    return true;
                } catch (InvalidBalanceException e) {
                    embed.setColor(ERROR_EMBED_COLOR);
                    long bal = TechnoBot.getInstance().getEconomy().getBalance(event.getAuthor()).getLeft();
                    String balFormat = EconManager.FORMATTER.format(bal);
                    embed.setDescription(String.format(":x: You don't have that much money to deposit! You currently have %s%s on hand", EconManager.SYMBOL, balFormat));
                    event.getChannel().sendMessage(embed.build()).queue();
                    return true;
                }
            } catch (NumberFormatException e) {
                embed.setColor(ERROR_EMBED_COLOR);
                embed.setDescription(":x: Invalid `<amount>` argument given\n\nUsage:\n`deposit <amount>`");
                event.getChannel().sendMessage(embed.build()).queue();
                return true;
            }
        }
        embed.setColor(ERROR_EMBED_COLOR);
        embed.setDescription(":x: Too few arguments given.\n\nUsage:\n`deposit <amount>`");
        event.getChannel().sendMessage(embed.build()).queue();
        return true;
    }

    @Override
    public @NotNull Set<String> getAliases() {
        return Sets.newHashSet("dep");
    }
}
