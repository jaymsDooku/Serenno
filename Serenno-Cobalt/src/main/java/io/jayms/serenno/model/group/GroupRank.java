package io.jayms.serenno.model.group;

import java.util.HashSet;
import java.util.Set;

public class GroupRank {
	
	private final String name;
	private String displayName;
	private int order;
	private Set<String> permissions;
	
	public GroupRank(Builder builder) {
		this.name = builder.getName();
		this.order = builder.getOrder();
		this.displayName = builder.getDisplayName();
		this.permissions = builder.getPermissions();
	}
	
	public String getName() {
		return name;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public boolean hasPermission(String perm) {
		return permissions.contains(perm) || permissions.contains(GroupPermissions.ALL);
	}
	
	public Set<String> getPermissions() {
		return permissions;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private String displayName;
		private int order;
		private Set<String> permissions = new HashSet<>();
		
		private Builder() {
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public Builder order(int order) {
			this.order = order;
			return this;
		}
		
		public int getOrder() {
			return order;
		}
		
		public Builder permissions(Set<String> permissions) {
			this.permissions = permissions;
			return this;
		}
		
		public Set<String> getPermissions() {
			return permissions;
		}
		
		public Builder addPermission(String permission) {
			this.permissions.add(permission);
			return this;
		}
		
		public GroupRank build() {
			return new GroupRank(this);
		}
	}
	
}
