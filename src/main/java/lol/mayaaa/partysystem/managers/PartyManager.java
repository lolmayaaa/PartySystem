package lol.mayaaa.partysystem.managers;

import com.velocitypowered.api.proxy.Player;
import lol.mayaaa.partysystem.PartySystem;
import lol.mayaaa.partysystem.models.Party;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

public class PartyManager {
    private Map<UUID, Party> playerParties; //player UUID to party
    private Map<UUID, Party> parties; // party ID to party object

    public PartyManager() {
        this.playerParties = new HashMap<>();
        this.parties = new HashMap<>();
    }

    // creating party
    public Party createParty(Player leader) {
        Party party = new Party(leader);
        parties.put(party.getId(), party);
        playerParties.put(leader.getUniqueId(), party);
        return party;
    }

    // disbanding party
    public void disbandParty(Party party) {
        for (Player member : party.getMembers()) {
            playerParties.remove(member.getUniqueId());
        }
        parties.remove(party.getId());
        party.disband();
    }

    public Party getPlayerParty(Player player) {
        return playerParties.get(player.getUniqueId());
    }

    public Party getPartyById(UUID partyId) {
        return parties.get(partyId);
    }

    public void addPlayerToParty(Player player, Party party) {
        party.addMember(player);
        playerParties.put(player.getUniqueId(), party);
    }

    public void removePlayerFromParty(Player player, Party party) {
        party.removeMember(player);
        playerParties.remove(player.getUniqueId());

        // he're we're checking if party size equals to 0, if it is, let's disband the party
        if (party.getSize() == 0) {
            disbandParty(party);
        }
        // if the party leader left, we'll assign new party leader
        else if (player.equals(party.getLeader())) {
            // assigning a new party leader (the first member in the set - should be the first player who joined the party)
            Iterator<Player> iterator = party.getMembers().iterator();
            if (iterator.hasNext()) {
                party.setLeader(iterator.next());
                party.broadcast(Component.text(party.getLeader().getUsername() + " is now the party leader!").color(NamedTextColor.GREEN));
            }
        }
    }

    public Collection<Party> getAllParties() {
        return parties.values();
    }
}