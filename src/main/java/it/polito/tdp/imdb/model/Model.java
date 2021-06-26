package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	Map<Integer, Actor> idMap;
	ImdbDAO dao;
	private Graph<Actor, DefaultWeightedEdge> grafo;
	List<Adiacenza> adiacenze;
	Simulazione sim;
	
	public Model() {
		dao = new ImdbDAO();
		idMap = new HashMap<>();
		dao.listAllActors(idMap);
		adiacenze = new ArrayList<>();
	}
	
	public void creaGrafo(String genere) {
		grafo = new SimpleWeightedGraph<Actor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		// vertici
		
		Graphs.addAllVertices(grafo, dao.getVertici(idMap, genere));
		
		// archi
		
		adiacenze = dao.getAdiacenze(idMap, genere);
		for (Adiacenza a: adiacenze) {
			if (grafo.getEdge(a.getA1(), a.getA2()) == null) {
				Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		
	}
	
	public List<String> getGeneri() {
		return dao.getGeneri();
	}
	
	public int numeroVertici() {
		return grafo.vertexSet().size();
	}
	
	public int numeroArchi() {
		return grafo.edgeSet().size();
	}
	
	public List<Actor> getAttori() {
		List<Actor> listaAttori = new ArrayList<>();
		for (Actor a: grafo.vertexSet()) {
			listaAttori.add(a);
		}
		Collections.sort(listaAttori);
		return listaAttori;
	}
	
	public List<Actor> getAttoriSimili(Actor a) {
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(grafo);
		List<Actor> actors = new ArrayList<>(ci.connectedSetOf(a));
		actors.remove(a);
		Collections.sort(actors);
		return actors;	
	}
	
	public List<Actor> getAttoriRaggiungibili (Actor a) {
		List<Actor> lista = new ArrayList<>();
		BreadthFirstIterator<Actor, DefaultWeightedEdge> bfv = 
				new BreadthFirstIterator<>(this.grafo, a);
		while (bfv.hasNext()) {
			Actor c = bfv.next();
			if (c != a) {
				lista.add(c);
				c = bfv.getParent(c); }
			}
		Collections.sort(lista);
		return lista;
		}
	
	public void simulate(int n) {
		sim = new Simulazione(grafo, n);
		sim.inizializza();
		sim.run();
	}
	
	public Collection<Actor> getInterviewedActors(){
		if(sim == null){
			return null;
		}
		return sim.getInterviewedActors();
	}
	
	public Integer getPauses(){
		if(sim == null){
			return null;
		}
		return sim.getPauses();
	}
}
