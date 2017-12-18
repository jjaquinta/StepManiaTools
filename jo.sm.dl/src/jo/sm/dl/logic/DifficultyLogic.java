package jo.sm.dl.logic;

import java.util.HashMap;
import java.util.Map;

import jo.sm.dl.data.DiffProfile;

public class DifficultyLogic
{
    private static Map<Integer, DiffProfile> mProfiles = new HashMap<>();
    
    public static void init(String[] argv)
    {
        addDefaults();
    }
    private static final int[] NPMS = {
            50,     // 1
            66,
            85,
            103,
            125,    // 5
            146,
            168,
            197,
            235,
            339,    // 10
            398,
            454,
            542,
    };
    private static final float[] DOUBLES = {
            .00f,   // 1
            .01f,
            .02f,
            .04f,
            .08f,  // 5
            .09f,
            .10f,
            .11f,
            .12f,
            .12f,  // 10
            .12f,
            .12f,
            .12f,
    };
    private static final float[] HOLDS = {
            .00f,   // 1
            .01f,
            .02f,
            .04f,
            .08f,  // 5
            .09f,
            .10f,
            .11f,
            .12f,
            .12f,  // 10
            .12f,
            .12f,
            .12f,
    };
    private static final float[] ROLLS = {
            .0f,   // 1
            .0f,
            .0f,
            .0f,
            .0f,  // 5
            .0f,
            .0f,
            .0f,
            .0f,
            .0f,  // 10
            .0f,
            .0f,
            .0f,
    };
    private static final float[] MINES = {
            .00f,   // 1
            .00f,
            .01f,
            .02f,
            .04f,  // 5
            .04f,
            .05f,
            .05f,
            .06f,
            .06f,  // 10
            .06f,
            .06f,
            .06f,
    };
    private static final float[][] NOTES = {
            // 4ths, 8ths, 12ths, 16ths, 24ths, 32nds, 48ths, 64ths, 192nds, 
            { 1.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },   // 1
            { 1.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.95f, 0.05f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.90f, 0.10f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.89f, 0.11f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },  // 5
            { 0.88f, 0.12f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.87f, 0.13f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.86f, 0.14f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.85f, 0.15f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.84f, 0.16f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },  // 10
            { 0.83f, 0.17f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.82f, 0.18f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
            { 0.81f, 0.19f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f },
    };
   
    private static void addDefaults()
    {
        for (int meter = 1; meter <= 13; meter++)
        {
            DiffProfile p = new DiffProfile();
            p.setMeter(meter);
            p.setNPM(NPMS[meter - 1]);
            p.setDoublePC(DOUBLES[meter - 1]);
            p.setHoldPC(HOLDS[meter - 1]);
            p.setRollPC(ROLLS[meter - 1]);
            p.setMinesPC(MINES[meter - 1]);
            p.setNote4ths(NOTES[meter - 1][0]);
            p.setNote8ths(NOTES[meter - 1][1]);
            p.setNote12ths(NOTES[meter - 1][2]);
            p.setNote16ths(NOTES[meter - 1][3]);
            p.setNote24ths(NOTES[meter - 1][4]);
            p.setNote32nds(NOTES[meter - 1][5]);
            p.setNote48ths(NOTES[meter - 1][6]);
            p.setNote64ths(NOTES[meter - 1][7]);
            p.setNote192nds(NOTES[meter - 1][8]);
            if (meter == 1)
                p.setNoBackArrow(true);
            mProfiles.put(meter, p);
        }
    }

    public static DiffProfile getProfile(int idx)
    {
        return mProfiles.get(idx);
    }
}
