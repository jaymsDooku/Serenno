package io.jayms.serenno.vault;

import java.util.Set;

import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.util.Coords;

public class VaultMapBastionDataSource implements BastionDataSource {

	private VaultMapDatabase db;
	
	public VaultMapBastionDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	@Override
	public void create(Bastion value) {
		
	}

	@Override
	public void update(Bastion value) {
		
	}

	@Override
	public Bastion get(Coords key) {
		return null;
	}

	@Override
	public Set<Bastion> getAll() {
		return null;
	}

	@Override
	public void delete(Bastion value) {
		
	}

	
	
}
