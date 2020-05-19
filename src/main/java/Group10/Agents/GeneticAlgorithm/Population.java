package Group10.Agents.GeneticAlgorithm;

public class Population {
    int populationsize = 100;
    Individual[] individuals = new Individual[10];

    //create population
    public void init_Population(int popsize) {
        for(int i =0; i<10; i++){
            individuals[i] = new Individual();
        }
    }
}
