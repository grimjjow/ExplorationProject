package Group10.Agents.GeneticAlgorithm;

import java.util.Random;

public class GenAlg {
    //Bug here
    //Population population = new Population;
    int Generations =0;

    //TODO
    //Create fitniss test

    public void Crossover(Individual Parent1, Individual Parent2){
        Random random = new Random();
        int CrossoverPoint = random.nextInt(Parent1.getGeneLength());

        //Swap over genes
        //for(int i=0; i<CrossoverPoint;i++){
            //TODO Get best genes and crossover
        //}
    }

    public void mutate(Individual individual) {
        Random random = new Random();

        int mutatepoint = random.nextInt(individual.getGeneLength());
        //individual[mutatepoint] += 0.1//some value
    }
}
