package com.qod.flappy.network;

import java.time.LocalTime;
import java.util.*;

public abstract class Evaluator {


    private static double DT = 2.0;
    private static final double PercentageOfSpeiciesToEvalate = 0.7;
    private static final double PercentageOfSpeiciesTooSmall = 0.3;

    private static final double  MUTATION_WEIGHT_RATE = 0.5f;
    private static final double  MUTATION_Q_RATE = 0.2f;
    private static final double  ADD_CONNECTION_RATE = 0.1f;
    private static final double  ADD_NODE_RATE = 0.05f;


    private final int populationSize;

    private List<Genome> genomes;
    private List<Genome> nextGenGenomes;
    Random random = new Random();

    private final List<Species> species;

    private final HashMap<Genome, Species> mappedSpecies;
    private final HashMap<Genome, Double> scoreMap;
    private double highestScore;
    private int highestGenomeIndex;

    public Evaluator(int populationSize, Genome startingGenome){
        genomes = new ArrayList<>(populationSize);
        nextGenGenomes = new ArrayList<>(populationSize);
        species = new ArrayList<>();
        mappedSpecies = new HashMap<>();
        scoreMap = new HashMap<>();
        this.populationSize = populationSize;

        Species species1 = new Species(startingGenome);
        species.add(species1);
        Genome genome;
        for (int i = 0; i < populationSize; i++ ){
            genome = startingGenome.copy();
            mappedSpecies.put(genome, species1);
            scoreMap.put(genome, 0.0);
            genomes.add(genome);
        }
        highestScore = 0.0;
    }

    public void evaluate(){
        // Reset all
        for(Species s: species){
            s.reset(random);
        }
        scoreMap.clear();
        nextGenGenomes.clear();
        highestScore = Float.MIN_VALUE;
        mappedSpecies.clear();

        // Place genomes into species
        if (species.size() > Map.amount * PercentageOfSpeiciesToEvalate){
            DT++;
        }

        if (species.size() < Map.amount * PercentageOfSpeiciesTooSmall){
            DT--;
        }

        for(Genome genome: genomes){
            boolean needsOwnSpecies = true;
            for(Species s: species) {
                if (CompatibilityDistance.count(genome, s.mascot) < DT){
                    needsOwnSpecies = false;
                    mappedSpecies.put(genome, s);
                    s.members.add(genome);
                    break;
                }
            }
            if (needsOwnSpecies){
                Species sNew = new Species(genome);
                species.add(sNew);
                mappedSpecies.put(genome, sNew);
            }
        }

        // Remove unused species
        ArrayList<Species> helperArray = new ArrayList<>();
        for(Species s: species){
            if (s.members.isEmpty()){
                helperArray.add(s);
            }
        }
        for (Species s: helperArray){
            species.remove(s);
        }

        // Evaluate genomes and assign score
        for(Genome genome: genomes){
            Species species = mappedSpecies.get(genome);

            double fitnessOfTheGenome = fitness(genome);
            double adjustedFitnessOfTheGenome = fitnessOfTheGenome / species.members.size();

            FitnessGenome GenomeAndHisFitness = new FitnessGenome(genome, fitnessOfTheGenome);
            species.fitnessPopulation.add(GenomeAndHisFitness);
            scoreMap.put(genome,adjustedFitnessOfTheGenome);

            if(species.fittestGenome.fitness < fitnessOfTheGenome){
                species.setFittestGenome(GenomeAndHisFitness);
            }
            if(fitnessOfTheGenome > highestScore){
                highestScore = fitnessOfTheGenome;
                highestGenomeIndex = genomes.indexOf(genome);
            }
        }

        // Save all best Genomes from each species
        for(Species specie: species){
            nextGenGenomes.add(specie.fittestGenome.genome.copy());
        }

        // Breed the rest of the genomes
        while (nextGenGenomes.size() < populationSize) { // Replace removed genomes by randomly breeding
            Species s = getRandomSpeciesBiasedAdjustedFitness();

            Genome p1 = getRandomGenomeBiasedAdjustedFitness(s);
            Genome p2 = getRandomGenomeBiasedAdjustedFitness(s);

            Genome child;
            if (scoreMap.get(p1) >= scoreMap.get(p2)) {
                child = Genome.crossover(p1, p2, random);
            } else {
                child = Genome.crossover(p2, p1, random);
            }
            if(random.nextDouble() > ADD_CONNECTION_RATE){
                child.addConnectionMutation(random, 50);
            }
            if (random.nextDouble() > ADD_NODE_RATE){
                child.addNodeMutation(random);
            }
            if (random.nextDouble() > MUTATION_Q_RATE){
                child.mutateNodesQ(random);
            }
            if (random.nextDouble() > MUTATION_WEIGHT_RATE){
                child.mutateConnectionWeights(random);
            }

            nextGenGenomes.add(child);
        }

        genomes = nextGenGenomes;
        nextGenGenomes = new ArrayList<>();
    }

    public int getSpeciesAmount() {
        return species.size();
    }

    public double getHighestFitness() {
        if(Map.save) {
            Map.save = false;
            LocalTime now = LocalTime.now();
            GenomePrinter.printGenome(
                    genomes.get(highestGenomeIndex),
                    now.toString().replace(':', '-') + ".png");
        }
        return highestScore;
    }

    public ArrayList<Genome> getGenomes(){
        return (ArrayList<Genome>) genomes;
    }

    /**
     * Selects a random species from the species list, where species with a higher total adjusted
     * fitness have a higher chance of being selected.
     */
    private Species getRandomSpeciesBiasedAdjustedFitness() {
        double completeWeight = 0.0;
        for (Species s : species) {
            completeWeight += s.totalAdjustedFitness;
        }
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (Species s : species) {
            countWeight += s.totalAdjustedFitness;
            if (countWeight >= r) {
                return s;
            }
        }
        throw new RuntimeException("Couldn't find a species... Number is species in total is "+species.size()+", and the total adjusted fitness is "+completeWeight);
    }

    /**
     * Selects a random genome from the species chosen, where genomes with a higher adjusted
     * fitness have a higher chance of being selected.
     */
    private Genome getRandomGenomeBiasedAdjustedFitness(Species selectFrom) {
        double completeWeight = 0.0;
        for (FitnessGenome fg : selectFrom.fitnessPopulation) {
            completeWeight += fg.fitness;
        }
        double r = Math.random() * completeWeight;
        double countWeight = 0.0;
        for (FitnessGenome fg : selectFrom.fitnessPopulation) {
            countWeight += fg.fitness;
            if (countWeight >= r) {
                return fg.genome;
            }
        }
        throw new RuntimeException("Couldn't find a genome... Number is genomes in selected species is "+selectFrom.fitnessPopulation.size()+", and the total adjusted fitness is "+completeWeight);
    }

    protected abstract double fitness(Genome genome);

    public static class FitnessGenome {
        double fitness;
        Genome genome;

        public FitnessGenome(Genome genome, double fitness) {
            this.genome = genome;
            this.fitness = fitness;
        }
    }

    public static class Species {

        public Genome mascot;
        public List<Genome> members;
        public List<FitnessGenome> fitnessPopulation;
        public double totalAdjustedFitness = 0f;
        public FitnessGenome fittestGenome;

        public Species(Genome mascot) {
            this.mascot = mascot;
            this.members = new LinkedList<>();
            this.members.add(mascot);
            this.fitnessPopulation = new ArrayList<>();
            fittestGenome = new FitnessGenome(mascot, 0.0);
        }

        public void setFittestGenome(FitnessGenome newFittestGenome){
            fittestGenome = newFittestGenome;
        }

        /**
         * Resets Species / Population
         * @param r - Random Object
         */
        public void reset(Random r) {
            int newMascotIndex = r.nextInt(members.size());
            this.mascot = members.get(newMascotIndex);
            members.clear();
            fitnessPopulation.clear();
            totalAdjustedFitness = 0f;
            fittestGenome = new FitnessGenome(mascot, 0.0);
        }
    }

}