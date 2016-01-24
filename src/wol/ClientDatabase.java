package wol;

import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ClientDatabase {

	protected ChatClient chatClient;
	protected ApiregClient apiregClient;

	protected String hostname = "jdbc:mysql://localhost:3306/";
	protected String database = "game.server";
	protected String username = "username";
	protected String password = "password";

	protected Connection mysql;
	protected CallableStatement query;

	protected ClientDatabase(ChatClient chatClient) {
		this.chatClient = chatClient;
		//this.connect();
	}

	protected ClientDatabase(ApiregClient apiregClient) {
		this.apiregClient = apiregClient;
		//this.connect();
	}

	protected void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.mysql = DriverManager.getConnection(hostname + database, username, password);
			System.out.println("Connected to mysql:"+ database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void close() {
		//mysql.close();
	}

	public String getApgar() {
		String apgar = "";
		return apgar;
	}

/*	public void checkConnection() {
		try {
			connect();
		} catch (SQLException e) {
		 	System.err.println("SQLException: " +e.getMessage());
		}
	}*/

	public void insertUser(ApiregClient client)  {
		connect();
		try {
			query = this.mysql.prepareCall("{call spInsertUser(?,?,?,?,?,?,?)}");
			query.setString(1, client.getNick());
			query.setString(2, client.getPass());
			query.setString(3, client.getApgar());
			query.setString(4, client.getEmail());
			query.setString(5, "test");
			query.registerOutParameter(6, Types.LONGVARCHAR);
			query.registerOutParameter(7, Types.INTEGER);

			Integer code = query.getInt(7);
			String message = query.getString(6);

			//ResultSet rs = query.executeQuery();
			//query = this.mysql.prepareCall("{call spTest()}");
			query.execute();
			query.close();
			this.mysql.close();

			client.setCode(code);
			client.setMessage(message);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		close();

	}
}