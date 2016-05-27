package com.zerofall.ezstorage.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.zerofall.ezstorage.util.JointList;

/** The security box tile entity */
public class TileEntitySecurityBox extends EZTileEntity {
	
	// list of allowed players
	private JointList<SecurePlayer> allowedPlayers = new JointList();
	
	/** Get the allowed players */
	public List<SecurePlayer> getAllowedPlayers() {
		return allowedPlayers;
	}
	
	/** Set the allowed players */
	public void setAllowedPlayers(JointList<SecurePlayer> players) {
		allowedPlayers = players;
	}
	
	/** Get the amount of allowed players */
	public int getAllowedPlayerCount() {
		return allowedPlayers.size();
	}
	
	/** Is the specified player allowed? */
	public boolean isPlayerAllowed(EntityPlayer p) {
		if(getAllowedPlayerCount() == 0) return true;
		for(SecurePlayer sp : allowedPlayers) {
			if(sp.id.toString().equals(p.getPersistentID().toString())) return true;
		}
		return false;
	}
	
	/** Is this player the owner of the block? */
	public boolean isOwner(EntityPlayer p) {
		if(getAllowedPlayerCount() == 0) return false;
		return allowedPlayers.get(0).id.toString().equals(p.getPersistentID().toString());
	}
	
	/** Add a player to the list of allowed players */
	public boolean addAllowedPlayer(EntityPlayer p) {
		return allowedPlayers.add(new SecurePlayer(p));
	}
	
	/** Remove a player from the list of allowed players */
	public boolean removeAllowedPlayer(EntityPlayer p) {
		Iterator<SecurePlayer> i = allowedPlayers.iterator();
		while(i.hasNext()) {
			SecurePlayer n = i.next();
			if(n.id.toString().equals(p.getPersistentID().toString())) {
				i.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound tag) {
		int i = 0;
		tag.setInteger("allowedPlayerCount", getAllowedPlayerCount());
		for(SecurePlayer p : allowedPlayers) {
			tag.setUniqueId("allowedPlayer" + (i) + "_id", p.id);
			tag.setString("allowedPlayer" + (i++) + "_name", p.name);
		}
		return tag;
	}

	@Override
	public void readDataFromNBT(NBTTagCompound tag) {
		allowedPlayers = new JointList();
		int count = tag.getInteger("allowedPlayerCount");
		for(int i = 0; i < count; i++) {
			allowedPlayers.add(new SecurePlayer(tag.getUniqueId("allowedPlayer" + i + "_id"), tag.getString("allowedPlayer" + i + "_name")));
		}
	}
	
	/** Player identification for security purposes */
	public static class SecurePlayer {
		public UUID id;
		public String name;
		
		public SecurePlayer(EntityPlayer player) {
			this(player.getPersistentID(), player.getName());
		}
		
		public SecurePlayer(UUID id, String name) {
			this.id = id;
			this.name = name;
		}
		
		@Override
		public String toString() {
			return id + "=" + name;
		}
	}

}
