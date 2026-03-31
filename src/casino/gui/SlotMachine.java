package casino.gui;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SlotMachine{
    private int rollerNr;
    private int symbolNr;
    private Random ranGen;
    private boolean roundWon = false;

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
            ranGen = new Random(seedVal);
        }catch(NoSuchAlgorithmException e){
            ranGen = new Random();
        }
    }

    public int[] playRound(){
        int[] bar = ranGen.ints(rollerNr,1, symbolNr).toArray();
        int prev = bar[0];
        roundWon = true;
        for(int i = 0; i < bar.length; i++){
            if (prev != bar[i]){
                roundWon = false;
                return bar;
            }
            prev = bar[i];
        }
        return bar;
    }

    public boolean roundWon() {
        return roundWon;
    }
}

