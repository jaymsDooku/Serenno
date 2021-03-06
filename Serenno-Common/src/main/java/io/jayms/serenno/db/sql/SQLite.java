package io.jayms.serenno.db.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLite extends Database {
	
	private final String location;
	private final String database;
	private final File SQLfile;

	public SQLite(final JavaPlugin plugin, final Logger log, final String prefix, final String database, final String location) {
		super(plugin, log, prefix, "[SQLite] ");
		this.database = database;
		this.location = location;

		final File folder = new File(this.location);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.SQLfile = new File(folder.getAbsolutePath() + File.separator + this.database);
	}
	
	public File getSQLfile() {
		return SQLfile;
	}

	@Override
	public Connection open() {
		if (this.connection != null) {
			return this.connection;
		}
		
		try {
			Class.forName("org.sqlite.JDBC");

			this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.SQLfile.getAbsolutePath());
			this.printInfo("Connection established!");

			return this.connection;
		}
		catch (final ClassNotFoundException e) {
			this.printErr("JDBC driver not found!", true);
			return null;
		}
		catch (final SQLException e) {
			this.printErr("SQLite exception during connection.", true);
			return null;
		}
	}

}
