package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulazione {
	
	private Graph<Actor, DefaultWeightedEdge> grafo;
	//input
	int giorni;
	//output
	int pause;
	Map<Integer, Actor> attoriIntervistati;
	
	List<Actor> attoriDaIntervistare;
	
	public Simulazione(Graph<Actor, DefaultWeightedEdge> g, int gio) {
		this.grafo = g;
		this.giorni = gio;
	}
	
	public void inizializza() {
		attoriIntervistati = new HashMap<Integer, Actor> ();
		this.pause = 0;
		attoriDaIntervistare = new ArrayList<Actor> (grafo.vertexSet());
	}
	
	public void run() {
		int contaPause = 0;
		for (int i = 1; i <= giorni; i++) {
			Random r = new Random();
			
			if (i == 1 || !attoriIntervistati.containsKey(i-1)) {
				// primo intervistato
				
				//prendo uno a caso da attoriDaIntervistare
				Actor actor = attoriDaIntervistare.get(r.nextInt(attoriDaIntervistare.size()));
				attoriDaIntervistare.remove(actor);
				
				//lo metto in attoriIntervistati
				attoriIntervistati.put(actor.getId(), actor);
				
				//stampo a video
				System.out.println("giorno " + i + " selezionato casualmente: " + actor.getFirstName() + " " + actor.getLastName());
				
				continue;
			} 
			
			if(i >= 3 && attoriIntervistati.containsKey(i-1) && attoriIntervistati.containsKey(i-2) 
					&& attoriIntervistati.get(i-1).gender.equals(attoriIntervistati.get(i-2).gender)) {
				//per due giorni di fila il produttore ha intervistato attori dello stesso genere -> con il 90% di probabilità pausa
				if(r.nextFloat() <= 0.9) {
					this.pause ++;
					System.out.println("GIORNO " + i + " - pausa!");
					continue ;
				}
			}
				
			if (r.nextFloat() <= 0.6) {
					// un altro casuale
					
					Actor actor = attoriDaIntervistare.get(r.nextInt(attoriDaIntervistare.size()));
					attoriDaIntervistare.remove(actor);
					
					//lo metto in attoriIntervistati
					attoriIntervistati.put(actor.getId(), actor);
					
					//stampo a video
					System.out.println("giorno " + i + " selezionato casualmente: " + actor.getFirstName() + " " + actor.getLastName());
					continue;
				} else {
					// consigliato
					
					Actor ultimo = attoriIntervistati.get(i-1);
					Actor racc = raccomandato(ultimo);
					
					if (racc == null || !attoriDaIntervistare.contains(racc)) {
						Actor actor = attoriDaIntervistare.get(r.nextInt(attoriDaIntervistare.size()));
						attoriDaIntervistare.remove(actor);
						
						//lo metto in attoriIntervistati
						attoriIntervistati.put(actor.getId(), actor);
						
						//stampo a video
						System.out.println("giorno " + i + " selezionato casualmente: " + actor.getFirstName() + " " + actor.getLastName());
						continue;
					} else {
						attoriDaIntervistare.remove(racc);
						
						//lo metto in attoriIntervistati
						attoriIntervistati.put(racc.getId(), racc);
						
						//stampo a video
						System.out.println("giorno " + i + " selezionato tramite consiglio: " + racc.getFirstName() + " " + racc.getLastName());
						continue;
					}
				}
			}
			
		
		
	}

	private Actor raccomandato(Actor ultimo) {
		// TODO Auto-generated method stub
		double peso = 0;
		Actor ra = null;
		
		for (Actor a: Graphs.neighborListOf(grafo, ultimo)) {
			if (grafo.getEdgeWeight(grafo.getEdge(ultimo, a)) > peso) {
				ra = a;
				peso = grafo.getEdgeWeight(grafo.getEdge(ultimo, a));		
			}
		}
		
		return ra;
	}
	
	public int getPauses() {
		return this.pause;
	}
	
	public Collection<Actor> getInterviewedActors(){
		return this.attoriIntervistati.values();
	}

}
