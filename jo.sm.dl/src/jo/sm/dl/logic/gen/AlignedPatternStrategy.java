package jo.sm.dl.logic.gen;

import jo.sm.dl.data.midi.MIDINotation;
import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.midi.MIDITrack;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.data.sm.pat.PatDef;

public class AlignedPatternStrategy extends DefaultPatternStrategy
{
    public static final String NAME = "aligned";

    @Override
    public String getName()
    {
        return NAME;
    }

    protected boolean isOnBeat(SMProject proj, MIDINote note)
    {
        MIDINotation n = proj.getMIDI().getNotation(note);
        if (n == null)
            return false;
        return (n.getAlignedStart()%16) == 0;
    }
    
    protected float[] getScores(PatDef pat, int ppq)
    {
        //return getInstances().size()*getNotes().size();
        //return getInstances().size();
        //return getNotes().size();
        float qscore = pat.getQLen(ppq);
        float lscore = (float)Math.log10(pat.getInstances().size());
        float vscore = pat.getNormalizedVolume();
        float tscore = 1.0f;
        switch (pat.getType())
        {
            case MIDITrack.MELODY:
                tscore *= 100;
                break;
            case MIDITrack.HARMONY:
                tscore *= 80;
                break;
            case MIDITrack.BASS:
                tscore *= 50;
                break;
            case MIDITrack.RHYTHYM:
                tscore *= 20;
                break;
            case MIDITrack.UNKNOWN:
                tscore *= 20;
                break;
            case MIDITrack.INCIDENTAL:
                tscore *= 5;
                break;
            case MIDITrack.IGNORE:
                tscore *= 0;
                break;
        }
        return new float[] { qscore, lscore, vscore, tscore };
    }
    
    protected int getScore(PatDef pat, int ppq)
    {
        float[] scores = getScores(pat, ppq);
        float score = 1.0f;
        for (float s : scores)
            score *= s;
        return (int)score;
    }

    protected void printBestScores(SMProject proj, int ppq)
    {
        System.out.println(proj.getPatterns().size()+" patterns");
        for (int i = 0; i < Math.min(5, proj.getPatterns().size()); i++)
        {
            PatDef def = proj.getPatterns().get(i);
            System.out.println("  len="+def.getNotes().size()+"/"+def.getQLen(ppq)
                +", insts="+def.getInstances().size()
                +", beat="+def.getBeat()
                +", type="+MIDITrack.typeToString(def.getType()));
            float[] scores = getScores(def, ppq);
            String msg = "    score="+getScore(def, ppq);
            msg += ", qscore="+scores[0];
            msg += ", lscore="+scores[1];
            msg += ", vscore="+scores[2];
            msg += ", tscore="+scores[3];
            System.out.println("    "+msg);
            System.out.print("    ticks=");
            for (int j = 0; j < def.getNotes().size(); j++)
                System.out.print(" "+def.getNotes().get(j).getDeltaTick());
            System.out.println();
            System.out.print("    instances=");
            for (int j = 0; j < def.getInstances().size(); j++)
                System.out.print(" "+def.getInstances().get(j).getNotes().get(0).getTick());
            System.out.println();
        }
    }
}
