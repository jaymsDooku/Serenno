package io.jayms.serenno.listener.citadel;

import java.text.DecimalFormat;

import org.bukkit.event.Listener;

import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;

public class CitadelListener implements Listener {

	protected CitadelManager cm;
	protected ReinforcementManager rm;
	protected BastionManager bm;
	
	protected final DecimalFormat df = new DecimalFormat("##.##");
	
	protected CitadelListener(CitadelManager cm, ReinforcementManager rm, BastionManager bm) {
		this.cm = cm;
		this.rm = rm;
		this.bm = bm;
	}
	
}
