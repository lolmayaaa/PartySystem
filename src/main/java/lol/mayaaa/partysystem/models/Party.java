package lol.mayaaa.partysystem.models;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;


import java.util.*;

public class Party {
    private UUID id;
    private Player leader;
    private Set<Player> members;
    private Set<UUID> invitedPlayers;
    private boolean isPublic;

    public Party(Player leader) {
        this.id = UUID.randomUUID();
        this.leader = leader;
        this.members = new HashSet<>();
        this.invitedPlayers = new HashSet<>();
        this.members.add(leader);
        this.isPublic = false;
    }

    public UUID getId() {
        return id;
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.leader = leader;
    }

    public Set<Player> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public Set<UUID> getInvitedPlayers() {
        return Collections.unmodifiableSet(invitedPlayers);
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean addMember(Player player) {
        return members.add(player);
    }

    public boolean removeMember(Player player) {
        return members.remove(player);
    }

    public boolean invitePlayer(Player player) {
        return invitedPlayers.add(player.getUniqueId());
    }

    public boolean removeInvite(UUID playerId) {
        return invitedPlayers.remove(playerId);
    }

    public boolean isInvited(Player player) {
        return invitedPlayers.contains(player.getUniqueId());
    }

    public boolean isMember(Player player) {
        return members.contains(player);
    }

    public int getSize() {
        return members.size();
    }

    public void broadcast(Component message) {
        for (Player member : members) {
            member.sendMessage(message);
        }
    }

    public void disband() {
        members.clear();
        invitedPlayers.clear();
        leader = null;
    }
}