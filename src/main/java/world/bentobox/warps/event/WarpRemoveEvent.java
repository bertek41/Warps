package world.bentobox.warps.event;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import world.bentobox.warps.Warp;

/**
 * This event is fired when a Warp is removed (when a warp sign is broken)
 * A Listener to this event can use it only to get informations. e.g: broadcast something
 * 
 * @author Poslovitch
 *
 */
public class WarpRemoveEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	
	private Location warpLoc;
	private UUID remover;
	
	/**
	 * @param plugin - BSkyBlock plugin objects
	 * @param warpLoc
	 * @param remover
	 */
	public WarpRemoveEvent(Warp plugin, Location warpLoc, UUID remover){
		this.warpLoc = warpLoc;
		this.remover = remover;
	}
	
	/**
	 * Get the location of the removed Warp
	 * @return removed warp's location
	 */
	public Location getWarpLocation(){return this.warpLoc;}
	
	/**
	 * Get who has removed the warp
	 * @return the warp's remover
	 */
	public UUID getRemover(){return this.remover;}
	
	@Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}