package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.PatDef;
import jo.sm.dl.data.PatInst;
import jo.sm.dl.data.PatNote;
import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMChart;
import jo.sm.dl.data.SMMark;
import jo.sm.dl.data.SMMeasure;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.data.SMTune;

public class DanceLogic
{
    private static Random RND = new Random();

    private static float PATTERN_QUOTA = .75f;

    private static final String[] DIFFICULTIES = {
            SMProject.DIFF_BEGINNER, SMProject.DIFF_EASY, SMProject.DIFF_MEDIUM, SMProject.DIFF_HARD, SMProject.DIFF_CHALLENGE
    };
    private static final int[] NPMS = {
            96,
            66,
            85,
            103,
            125,
            146,
            168,
            197,
            235,
            339,
            398,
            454,
            542,
    };
    private static final float[] DOUBLES = {
            8.0f,
            10.0f,
            11.0f,
            12.0f,
            14.0f,
            11.0f,
            14.0f,
            12.0f,
            11.0f,
            14.0f,
            17.0f,
            16.0f,
            16.0f,
    };
    
    public static void dance(SMProject proj)
    {
        MIDITune midi = proj.getMIDI();
        SMTune steps = proj.getTune();        
        steps.setDisplayBPM(midi.getBeatsPerMinute());
        steps.getBPMs().addAll(midi.getBPMs());
        for (int i = 0; i < DIFFICULTIES.length; i++)
        {
            int idx = proj.getDifficulties().get(DIFFICULTIES[i]);
            generateChart(proj, DIFFICULTIES[i], idx+1, NPMS[idx], 0/*DOUBLES[idx]*/);
        }
    }
    
    private static void generateChart(SMProject proj, String difficulty, int meter, int npm, float doublePC)
    {
        MIDITune midi = proj.getMIDI();
        SMChart chart = new SMChart();
        proj.getTune().getCharts().add(chart);
        int granularity = midi.getPulsesPerQuarter();
        long ticksPerMeasure = midi.getPulsesPerQuarter()*4;
        for (long tick = 0; tick < midi.getLengthInTicks(); tick += ticksPerMeasure)
        {
            SMMeasure m = new SMMeasure();
            m.setStartTick(tick);
            m.getBeats().add(new SMBeat(tick + granularity*0));
            m.getBeats().add(new SMBeat(tick + granularity*1));
            m.getBeats().add(new SMBeat(tick + granularity*2));
            m.getBeats().add(new SMBeat(tick + granularity*3));
            chart.getMeasures().add(m);
        }
        int targetSteps = (int)midi.getLengthInSeconds()*npm/60;
        System.out.println("Target steps: "+targetSteps);
        List<DanceBlackout> taken = new ArrayList<>();
        int used = addPatternNotes(proj, chart, (int)(targetSteps*PATTERN_QUOTA), (int)ticksPerMeasure, granularity, doublePC, taken);
        System.out.println("Pattern steps: "+used);
        if (used < targetSteps)
            used += addRandomNotes(midi, chart, ticksPerMeasure, granularity, doublePC, targetSteps);
        System.out.println("Total steps: "+used);
        chart.setNotesDescription(System.getProperty("user.name"));
        chart.setNotesChartType("dance-single");
        chart.setNotesDifficulty(difficulty);
        chart.setNotesMeter(meter);
    }

    public static int addRandomNotes(MIDITune midi, SMChart chart,
            long ticksPerMeasure, long granularity, float doublePC, int targetSteps)
    {
        int used = 0;
        Comparator<Long> c = new Comparator<Long>() {            
            @Override
            public int compare(Long o1, Long o2)
            {
                return (int)Math.signum(o1 - o2);
            }
        };
        List<MIDINote> notesOfInterest = findNotes(midi, granularity);
        List<Long> taken = new ArrayList<>();
        for (SMMeasure m : chart.getMeasures())
            for (SMBeat b : m.getBeats())
                if (b.isAnySteps())
                    taken.add(b.getTick());
        Collections.sort(taken);
        while ((notesOfInterest.size() > 0) && (used < targetSteps))
        {
            // sort notes
            MIDINote best = null;
            long bestd = 0;
            int bestidx = 0;
            if (taken.size() == 0)
            {
                bestidx = 0;
                best = notesOfInterest.get(RND.nextInt(notesOfInterest.size()));
            }
            else
                for (MIDINote note : notesOfInterest)
                {
                    int idx = Collections.binarySearch(taken, note.getTick(), c);
                    if (idx >= 0)
                        continue;
                    idx = -idx - 1;
                    long dist;
                    if (idx == 0)
                        dist = taken.get(0) - note.getTick();
                    else if (idx == taken.size())
                        dist = note.getTick() - taken.get(taken.size() - 1);
                    else
                        dist = Math.min(note.getTick() - taken.get(idx - 1), taken.get(idx) - note.getTick());
                    if ((best == null) || (dist > bestd))
                    {
                        best = note;
                        bestd = dist;
                        bestidx = idx;
                    }
                }                
            if (best == null)
                break;
            used += setNote(chart, ticksPerMeasure, best.getTick(), randomNote(doublePC)) ? 1 : 0;
            taken.add(bestidx, best.getTick());
            notesOfInterest.remove(best);
        }
        return used;
    }

    private static int addPatternNotes(SMProject proj, SMChart chart, int quantity, int ticksPerMeasure, long granularity, float doublePC, List<DanceBlackout> taken)
    {
        int used = 0;
        for (int k = 0; k < proj.getPatterns().size(); k++)
        {
            PatDef pattern = proj.getPatterns().get(k);
            List<PatNote> notes = new ArrayList<>();
            for (PatNote n : pattern.getNotes())
                if (n.getDeltaTick()%granularity == 0)
                    notes.add(new PatNote(n, randomNote(doublePC)));
            // aim to be under quota by thinning out pattern
            int overbudget = used + notes.size()*pattern.getInstances().size() - quantity;
            if (overbudget > 0)
            {
                int tothin = overbudget/pattern.getInstances().size() + 1;
                while ((tothin-- > 0) && (notes.size() > 0))
                    notes.remove(RND.nextInt(notes.size()));
            }
            if (notes.size() < PatternLogic.MIN_INST)
                continue;
            for (int j = 0; j < pattern.getInstances().size(); j++)
            {
                PatInst inst = pattern.getInstances().get(j);
                long startTick = inst.getNotes().get(0).getTick();
                long endTick = inst.getNotes().get(inst.getNotes().size() - 1).getTick();
                if (intersects(startTick, endTick, taken))
                    continue;
                if (startTick%granularity != 0)
                    continue;
                taken.add(new DanceBlackout(startTick, endTick));
                System.out.print("P"+k+"_"+j+": ");
                for (PatNote pNote : notes)
                {
                    MIDINote n = inst.getNotes().get(pNote.getIndex());
                    if (setNote(chart, ticksPerMeasure, n.getTick(), pNote.getSteps()))
                    {
                        used++;
                        System.out.print(DanceLogic.patternToStep(pNote.getSteps()));
                    }
                }
                System.out.println();
                if (proj.getFlags().contains(SMProject.MARK_PATTERNS))
                {
                    float start = inst.getNotes().get(0).getTick()*proj.getMIDI().getMSPerTick()/1000.0f;
                    float stop = inst.getNotes().get(inst.getNotes().size() - 1).getTick()*proj.getMIDI().getMSPerTick()/1000.0f;
                    proj.getTune().getLyrics().add(new SMMark(start, "P"+k+"_"+j+" start"));
                    proj.getTune().getLyrics().add(new SMMark(stop, "P"+k+"_"+j+" end"));
                }
                inst.setUsed(true);
                if (used > quantity)
                    break;
            }
            if (used > quantity)
                break;
        }
        return used;
    }

    private static String patternToStep(String pattern)
    {
        switch (pattern)
        {
            case "1000":
                return "<";
            case "0100":
                return "^";
            case "0010":
                return "v";
            case "0001":
                return ">";
            case "1100":
                return "(<^)";
            case "1010":
                return "(<v)";
            case "1001":
                return "(<>)";
            case "0110":
                return "(^v)";
            case "0101":
                return "(^>)";
            case "0011":
                return "(v>)";
        }
        return "?";
    }
    
    private static boolean intersects(long startTick, long endTick,
            List<DanceBlackout> taken)
    {
        for (DanceBlackout t : taken)
            if (t.overlaps(startTick, endTick))
                return true;
        return false;
    }

    public static String randomNote(float doublePC)
    {
        if (DanceLogic.RND.nextFloat() > doublePC)
            switch (DanceLogic.RND.nextInt(4))
            {
                case 0:
                    return "1000";
                case 1:
                    return "0100";
                case 2:
                    return "0010";
                case 3:
                    return "0001";
            }
        else
            switch (DanceLogic.RND.nextInt(6))
            {
                case 0:
                    return "1100";
                case 1:
                    return "1010";
                case 2:
                    return "1001";
                case 3:
                    return "0110";
                case 4:
                    return "0101";
                case 5:
                    return "0011";
            }
        return "0000";
    }
    
    private static boolean setNote(SMChart chart, long ticksPerMeasure, long tick, String step)
    {
        if (step.equals("0000"))
            return false;
        int measureIdx = (int)(tick/ticksPerMeasure);
        if (measureIdx >= chart.getMeasures().size())
            return false;
        SMMeasure m = chart.getMeasures().get(measureIdx);
        if (tick < m.getStartTick() || (tick >= m.getStartTick() + ticksPerMeasure))
            throw new IllegalStateException(); // should never happen
        long offset = tick - m.getStartTick();
        long gapPerBeat = m.getBeats().get(1).getTick() - m.getStartTick();
        while (offset%gapPerBeat != 0)
        {
            expandMeasure(m, gapPerBeat/2);
            gapPerBeat = m.getBeats().get(1).getTick() - m.getStartTick();
        }
        int idx = (int)(offset/gapPerBeat);
        SMBeat b = m.getBeats().get(idx);
        if ((new String(b.getNotes())).equals("0000"))
        {
            b.setNotes(step.toCharArray());
            return true;
        }
        else
            return false;
    }
    
    private static void expandMeasure(SMMeasure m, long newGap)
    {
        if (m.getBeats().size() == 64)
            throw new IllegalStateException("Got as high fidelity as we can");
        for (int i = m.getBeats().size() - 1; i >= 0; i--)
        {
            SMBeat oldBeat = m.getBeats().get(i);
            SMBeat newBeat = new SMBeat(oldBeat.getTick() + newGap);
            m.getBeats().add(i + 1, newBeat);
        }
    }

    private static List<MIDINote> findNotes(MIDITune midi, long granularity)
    {
        List<MIDINote> notes = new ArrayList<>();
        notes.addAll(midi.getNotes());
        // remove those off acceptable beat
        for (Iterator<MIDINote> i = notes.iterator(); i.hasNext(); )
            if (i.next().getTick()%granularity != 0)
                i.remove();
        return notes;
    }
}

class DanceBlackout
{
    long    mStartTick;
    long    mEndTick;
    
    public DanceBlackout(long start, long end)
    {
        mStartTick = start;
        mEndTick = end;
    }
    
    public boolean contains(long tick)
    {
        return (mStartTick <= tick) && (tick <= mEndTick);
    }
    
    public boolean overlaps(long s, long e)
    {
        if (mStartTick < s)
            return mEndTick > s;
        else
            return e > mStartTick;
    }
}