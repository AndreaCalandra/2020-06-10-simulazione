package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public void listAllActors(Map<Integer, Actor> idMap){
		String sql = "SELECT * FROM actors";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				idMap.put(res.getInt("id"), actor);
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getGeneri() {
		String sql = "SELECT DISTINCT genre FROM movies_genres";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				String g = res.getString("genre");
				
				result.add(g);
			}
			conn.close();
			Collections.sort(result);
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Actor> getVertici(Map<Integer, Actor> idMap, String genere) {
		String sql = "SELECT DISTINCT r.actor_id as id "
				+ "FROM roles r, movies_genres mg "
				+ "WHERE r.movie_id = mg.movie_id AND mg.genre = ? "
				+ "ORDER BY r.actor_id";
		List<Actor> attori = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Actor actor = idMap.get(res.getInt("id"));
				attori.add(actor);
			}
			conn.close();
			return attori;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(Map<Integer, Actor> idMap, String genere) {
		String sql = "SELECT r1.actor_id AS a1, r2.actor_id AS a2, COUNT(DISTINCT(r1.movie_id)) AS peso "
				+ "FROM movies_genres mg, roles r1, roles r2 "
				+ "WHERE mg.movie_id = r1.movie_id "
				+ "AND r1.movie_id = r2.movie_id "
				+ "AND mg.genre = ? "
				+ "AND r1.actor_id > r2.actor_id "
				+ "GROUP BY a1, a2";
		List<Adiacenza> adiacenze = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Adiacenza a = new Adiacenza(idMap.get(res.getInt("a1")), idMap.get(res.getInt("a2")), res.getInt("peso"));
				adiacenze.add(a);
			}
			conn.close();
			return adiacenze;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
}
