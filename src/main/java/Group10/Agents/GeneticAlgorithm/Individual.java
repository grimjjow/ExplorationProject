package Group10.Agents.GeneticAlgorithm;

import java.util.Random;

public class Individual {
    int fitness;
    int[] genes = new int[2];
    int Gene_length = 2;

    public Individual() {
        Random random  = new Random();
        //Put random (0,1) value into array
        for(int i= 0; i < Gene_length; i++){
            genes[i] = Math.abs(random.nextInt() % 2);
        }

        fitness = 0;
    }

    public void Calculatefitness(){
        //TODO
    }


    public void getFitness(){
        //TODO
    }

    public int getGeneLength(){
        return this.Gene_length;
    }
}
