package casino.gui;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SlotMachine{
    private int rollerNr;
    private int symbolNr;
    private Random ranGen;

    SlotMachine(int rollerNr, int symbolNr){
        this.rollerNr = rollerNr;
        this.symbolNr = symbolNr;
        try{
            SecureRandom ranSeed = SecureRandom.getInstance("NativePRNG");
            byte[] seed = ranSeed.generateSeed(8);
            long seedVal = seed[0];
            for(int i = 1; i < 8; i++ ){
                seedVal += seed[i];
            }
            System.out.print(seedVal);
            ranGen = new Random(seedVal);
        }catch(NoSuchAlgorithmException e){
            ranGen = new Random();
        }
    }

    public boolean playRound(){
        int[] bar = ranGen.ints(rollerNr,1, symbolNr).toArray();
        int[] counter = new int[symbolNr];
        for(int i = 0; i < symbolNr; i++){
            counter[i] = 0;
        }
        for(int i = 0; i < rollerNr; i++){
            counter[bar[i]]++;
        }
        int max = 0;
        for(int i = 0; i < symbolNr; i++){
            if(counter[i] > max){
                max = counter[i];
            }
        }
        return max == rollerNr;
    }
}

