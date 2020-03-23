package br.com.centralandradina.centralsfake;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;

public final class main extends JavaPlugin implements Listener  
{
	ProtocolManager protocolManager;
	FileConfiguration config;
	
	 @Override
    public void onEnable()
	{
		 // Config
		 config = this.getConfig();
		 config.addDefault("minFakePlayers", 4);
		 config.addDefault("maxFakePlayers", 10);
		 config.addDefault("customMotd", "&eMy &fServer &aMotD");
		 config.options().copyDefaults(true);
	     saveConfig();
	     
    	protocolManager = ProtocolLibrary.getProtocolManager();
	 
	 	protocolManager.addPacketListener(
			 new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
			 	@Override
				public void onPacketSending(PacketEvent event) {
					
					WrappedServerPing packet = event.getPacket().getServerPings().read(0);
					
					// Players
					int players = getServer().getOnlinePlayers().size();
					Random rand = new Random();
					
					players = players + (rand.nextInt(config.getInt("maxFakePlayers") - config.getInt("minFakePlayers")) + config.getInt("minFakePlayers"));
					if(players > getServer().getMaxPlayers()) {
						players = getServer().getMaxPlayers();
					}
					packet.setPlayersOnline(players);
					
					// Motd
					String motd = config.getString("customMotd"); 
					packet.setMotD(ChatColor.translateAlternateColorCodes('&', motd));
				}
			 });
	        
    }
    
    @Override
    public void onDisable()
    { }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
    	String commandName = cmd.getName().toLowerCase();
    	
    	if(commandName.equals("cfpo")) {
			if(args.length > 0) {
				if(args[0].equals("reload")) {
					getLogger().info("-- Central Fake: RECARREGADO");
					this.reloadConfig();
				}
			}
    	}
    	
		return false;
    }
}
