package lol.mayaaa.partysystem.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lol.mayaaa.partysystem.PartySystem;
import lol.mayaaa.partysystem.models.Party;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public class PartyCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        // making sure that if the sender is player
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("Only players can use party commands!").color(NamedTextColor.RED));
            return;
        }

        Player player = (Player) source;
        Party party = PartySystem.getInstance().getPartyManager().getPlayerParty(player);

        if (args.length == 0) {
            helpMenu(player);
            return;
        }

        // arguments cases
        // when the case is this, it will call this function
        switch (args[0].toLowerCase()) {
            case "create":
                createParty(player);
                break;
            case "disband":
                disbandParty(player, party);
                break;
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /party invite <player>").color(NamedTextColor.RED));
                    return;
                }
                invitePlayer(player, party, args[1]);
                break;
            case "accept":
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /party accept <player>").color(NamedTextColor.RED));
                    return;
                }
                acceptInvite(player, args[1]);
                break;
            case "leave":
                leaveParty(player, party);
                break;
            case "kick":
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /party kick <player>").color(NamedTextColor.RED));
                    return;
                }
                kickPlayer(player, party, args[1]);
                break;
            case "list":
                listParty(player, party);
                break;
            case "promote":
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /party promote <player>").color(NamedTextColor.RED));
                    return;
                }
                promotePlayer(player, party, args[1]);
                break;
            default:
                helpMenu(player);
                break;
        }
    }

    // help menu
    private void helpMenu(Player player) {
        player.sendMessage(Component.text("§6§lParty System Help"));
        player.sendMessage(Component.text("§e/party create §7- Create a new party"));
        player.sendMessage(Component.text("§e/party invite <player> §7- Invite a player to your party"));
        player.sendMessage(Component.text("§e/party accept <player> §7- Accept a party invitation"));
        player.sendMessage(Component.text("§e/party leave §7- Leave your current party"));
        player.sendMessage(Component.text("§e/party kick <player> §7- Kick a player from your party"));
        player.sendMessage(Component.text("§e/party promote <player> §7- Promote a player to leader"));
        player.sendMessage(Component.text("§e/party list §7- List party members"));
        player.sendMessage(Component.text("§e/party disband §7- Disband your party"));
    }

    // creating the party
    private void createParty(Player player) {
        if (PartySystem.getInstance().getPartyManager().getPlayerParty(player) != null) {
            player.sendMessage(Component.text("You are already in a party!").color(NamedTextColor.RED));
            return;
        }

        Party party = PartySystem.getInstance().getPartyManager().createParty(player);
        player.sendMessage(Component.text("Party created! You are now the leader.").color(NamedTextColor.GREEN));
    }

    // disbanding it
    private void disbandParty(Player player, Party party) {
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!").color(NamedTextColor.RED));
            return;
        }

        if (!party.getLeader().equals(player)) {
            player.sendMessage(Component.text("Only the party leader can disband the party!").color(NamedTextColor.RED));
            return;
        }

        party.broadcast(Component.text("The party has been disbanded by the leader.").color(NamedTextColor.RED));
        PartySystem.getInstance().getPartyManager().disbandParty(party);
    }

    // inviting players
    private void invitePlayer(Player player, Party party, String targetName) {
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!").color(NamedTextColor.RED));
            return;
        }

        if (!party.getLeader().equals(player)) {
            player.sendMessage(Component.text("Only the party leader can invite players!").color(NamedTextColor.RED));
            return;
        }

        Optional<Player> target = PartySystem.getInstance().getProxyServer().getPlayer(targetName);
        if (!target.isPresent()) {
            player.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
            return;
        }

        Player targetPlayer = target.get();
        if (party.isMember(targetPlayer)) {
            player.sendMessage(Component.text("That player is already in your party!").color(NamedTextColor.RED));
            return;
        }

        if (party.isInvited(targetPlayer)) {
            player.sendMessage(Component.text("That player has already been invited!").color(NamedTextColor.RED));
            return;
        }

        party.invitePlayer(targetPlayer);
        player.sendMessage(Component.text("Invitation sent to " + targetPlayer.getUsername()).color(NamedTextColor.GREEN));

        targetPlayer.sendMessage(Component.text("You have been invited to " + player.getUsername() + "'s party!").color(NamedTextColor.GREEN));
        targetPlayer.sendMessage(Component.text("Type /party accept " + player.getUsername() + " to join.").color(NamedTextColor.YELLOW));
    }

    // accepting inv
    private void acceptInvite(Player player, String inviterName) {
        if (PartySystem.getInstance().getPartyManager().getPlayerParty(player) != null) {
            player.sendMessage(Component.text("You are already in a party!").color(NamedTextColor.RED));
            return;
        }

        Optional<Player> inviter = PartySystem.getInstance().getProxyServer().getPlayer(inviterName);
        if (!inviter.isPresent()) {
            player.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
            return;
        }

        Party party = PartySystem.getInstance().getPartyManager().getPlayerParty(inviter.get());
        if (party == null) {
            player.sendMessage(Component.text("That player is not in a party!").color(NamedTextColor.RED));
            return;
        }

        if (!party.isInvited(player)) {
            player.sendMessage(Component.text("You don't have an invitation from that player!").color(NamedTextColor.RED));
            return;
        }

        party.removeInvite(player.getUniqueId());
        PartySystem.getInstance().getPartyManager().addPlayerToParty(player, party);

        party.broadcast(Component.text(player.getUsername() + " has joined the party!").color(NamedTextColor.GREEN));
    }

    // leaving the party
    private void leaveParty(Player player, Party party) {
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!").color(NamedTextColor.RED));
            return;
        }

        if (party.getLeader().equals(player)) {
            player.sendMessage(Component.text("As the leader, you must use /party disband or promote someone first!").color(NamedTextColor.RED));
            return;
        }

        PartySystem.getInstance().getPartyManager().removePlayerFromParty(player, party);
        player.sendMessage(Component.text("You have left the party.").color(NamedTextColor.YELLOW));
        party.broadcast(Component.text(player.getUsername() + " has left the party.").color(NamedTextColor.YELLOW));
    }

    // kicking player
    private void kickPlayer(Player player, Party party, String targetName) {
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!").color(NamedTextColor.RED));
            return;
        }

        if (!party.getLeader().equals(player)) {
            player.sendMessage(Component.text("Only the party leader can kick players!").color(NamedTextColor.RED));
            return;
        }

        Optional<Player> target = PartySystem.getInstance().getProxyServer().getPlayer(targetName);
        if (!target.isPresent()) {
            player.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
            return;
        }

        Player targetPlayer = target.get();
        if (!party.isMember(targetPlayer)) {
            player.sendMessage(Component.text("That player is not in your party!").color(NamedTextColor.RED));
            return;
        }

        if (targetPlayer.equals(player)) {
            player.sendMessage(Component.text("You cannot kick yourself!").color(NamedTextColor.RED));
            return;
        }

        PartySystem.getInstance().getPartyManager().removePlayerFromParty(targetPlayer, party);
        targetPlayer.sendMessage(Component.text("You have been kicked from the party.").color(NamedTextColor.RED));
        party.broadcast(Component.text(targetPlayer.getUsername() + " has been kicked from the party.").color(NamedTextColor.YELLOW));
    }

    // listing the members AND the leader in the party
    private void listParty(Player player, Party party) {
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!").color(NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("§6§lParty Members (§e" + party.getSize() + "§6)"));
        player.sendMessage(Component.text("§6Leader: §e" + party.getLeader().getUsername()));

        if (party.getSize() > 1) {
            StringBuilder members = new StringBuilder("§6Members: ");
            for (Player member : party.getMembers()) {
                if (!member.equals(party.getLeader())) {
                    members.append("§e").append(member.getUsername()).append("§6, ");
                }
            }
            // Remove the trailing comma and space
            if (members.length() > 10) {
                members.setLength(members.length() - 2);
            }
            player.sendMessage(Component.text(members.toString()));
        }
    }

    // promoting to party leader
    private void promotePlayer(Player player, Party party, String targetName) {
        if (party == null) {
            player.sendMessage(Component.text("You are not in a party!").color(NamedTextColor.RED));
            return;
        }

        if (!party.getLeader().equals(player)) {
            player.sendMessage(Component.text("Only the party leader can promote players!").color(NamedTextColor.RED));
            return;
        }

        Optional<Player> target = PartySystem.getInstance().getProxyServer().getPlayer(targetName);
        if (!target.isPresent()) {
            player.sendMessage(Component.text("Player not found!").color(NamedTextColor.RED));
            return;
        }

        Player targetPlayer = target.get();
        if (!party.isMember(targetPlayer)) {
            player.sendMessage(Component.text("That player is not in your party!").color(NamedTextColor.RED));
            return;
        }

        if (targetPlayer.equals(player)) {
            player.sendMessage(Component.text("You are already the leader!").color(NamedTextColor.RED));
            return;
        }

        party.setLeader(targetPlayer);
        party.broadcast(Component.text(targetPlayer.getUsername() + " is now the party leader!").color(NamedTextColor.GREEN));
    }

    // suggesting names btw
    // might remove this feature, who knows
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();

        // arg length
        if (args.length == 1) {
            suggestions.add("create");
            suggestions.add("invite");
            suggestions.add("accept");
            suggestions.add("leave");
            suggestions.add("kick");
            suggestions.add("promote");
            suggestions.add("list");
            suggestions.add("disband");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("kick") ||
                    args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("accept")) {
                // suggesting players [ONLY ONLINE ONES]
                PartySystem.getInstance().getProxyServer().getAllPlayers().forEach(p -> suggestions.add(p.getUsername()));
            }
        }

        return suggestions;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // no perms needed
    }
}