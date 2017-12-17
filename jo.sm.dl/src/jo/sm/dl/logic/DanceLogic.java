package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jo.sm.dl.data.DiffProfile;
import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.PatDef;
import jo.sm.dl.data.PatInst;
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
    
    public static void dance(SMProject proj)
    {
        MIDITune midi = proj.getMIDI();
        SMTune steps = proj.getTune();        
        steps.setDisplayBPM(midi.getBeatsPerMinute());
        steps.getBPMs().addAll(midi.getBPMs());
        for (int i = 0; i < DIFFICULTIES.length; i++)
        {
            int idx = proj.getDifficulties().get(DIFFICULTIES[i]);
            DiffProfile diff = DifficultyLogic.getProfile(idx);
            generateChart(proj, DIFFICULTIES[i], diff);
        }
    }
    
    private static void generateChart(SMProject proj, String difficulty, DiffProfile diff)
    {
        MIDITune midi = proj.getMIDI();
        SMChart chart = new SMChart();
        proj.getTune().getCharts().add(chart);
        int quarter = midi.getPulsesPerQuarter();
        long ticksPerMeasure = midi.getPulsesPerQuarter()*4;
        for (long tick = 0; tick < midi.getLengthInTicks(); tick += ticksPerMeasure)
        {
            SMMeasure m = new SMMeasure();
            m.setStartTick(tick);
            m.getBeats().add(new SMBeat(tick + quarter*0));
            m.getBeats().add(new SMBeat(tick + quarter*1));
            m.getBeats().add(new SMBeat(tick + quarter*2));
            m.getBeats().add(new SMBeat(tick + quarter*3));
            chart.getMeasures().add(m);
        }
        int targetSteps = (int)midi.getLengthInSeconds()*diff.getNPM()/60;
        System.out.println("Target steps: "+targetSteps);
        List<DanceBlackout> taken = new ArrayList<>();
        int used = addPatternNotes(proj, chart, (int)(targetSteps*PATTERN_QUOTA), (int)ticksPerMeasure, diff, taken);
        System.out.println("Pattern steps: "+used);
        Collections.sort(taken);
        long mark = 0;
        for (DanceBlackout bo : taken)
        {
            used += addRandomNotes(midi, chart, ticksPerMeasure, quarter, diff, mark+1, bo.mStartTick-1);
            mark = bo.mEndTick;
        }
        used += addRandomNotes(midi, chart, ticksPerMeasure, quarter, diff, mark+1, midi.getLengthInTicks());
//        if (used < targetSteps)
//            used += addRandomNotes(midi, chart, ticksPerMeasure, quarter, diff, targetSteps);
        System.out.println("Total steps: "+used);
        chart.setNotesDescription(System.getProperty("user.name"));
        chart.setNotesChartType("dance-single");
        chart.setNotesDifficulty(difficulty);
        chart.setNotesMeter(diff.getMeter());
    }

    public static int addRandomNotes(MIDITune midi, SMChart chart,
            long ticksPerMeasure, long quarter, DiffProfile diff, long startTick, long endTick)
    {
        List<MIDINote> notesOfInterest = findNotes(midi, startTick, endTick);
        if (notesOfInterest.size() == 0)
            return 0;
        List<MIDINote> selectedNotes = thinToQuota(midi, quarter, diff,
                notesOfInterest);
        int used = 0;
        for (MIDINote note : selectedNotes)
            used += setNote(chart, ticksPerMeasure, note.getTick(), randomNote(diff)) ? 1 : 0;
        return used;
    }

    public static List<MIDINote> thinToQuota(MIDITune midi, long quarter,
            DiffProfile diff, List<MIDINote> notesOfInterest)
    {
        Collections.sort(notesOfInterest);
        float minuteStart = midi.tickToMinutes(notesOfInterest.get(0).getTick());
        float minuteEnd = midi.tickToMinutes(notesOfInterest.get(notesOfInterest.size() - 1).getTick());
        float minuteElapsed = minuteEnd - minuteStart;
        int quotaNotes = (int)(minuteElapsed*diff.getNPM());
        int[] quotaTypes = new int[9];
        quotaTypes[0] = (int)(diff.getNote4ths()*quotaNotes);
        quotaTypes[1] = (int)(diff.getNote8ths()*quotaNotes);
        quotaTypes[2] = (int)(diff.getNote12ths()*quotaNotes);
        quotaTypes[3] = (int)(diff.getNote16ths()*quotaNotes);
        quotaTypes[4] = (int)(diff.getNote24ths()*quotaNotes);
        quotaTypes[5] = (int)(diff.getNote32nds()*quotaNotes);
        quotaTypes[6] = (int)(diff.getNote48ths()*quotaNotes);
        quotaTypes[7] = (int)(diff.getNote64ths()*quotaNotes);
        quotaTypes[8] = (int)(diff.getNote192nds()*quotaNotes);
        List<MIDINote> selectedNotes = new ArrayList<>();
        int added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/48, quotaTypes[8]);
        quotaTypes[7] += quotaTypes[8] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/16, quotaTypes[7]);
        quotaTypes[6] += quotaTypes[7] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/12, quotaTypes[6]);
        quotaTypes[5] += quotaTypes[6] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/8, quotaTypes[5]);
        quotaTypes[4] += quotaTypes[5] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/6, quotaTypes[4]);
        quotaTypes[3] += quotaTypes[4] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/4, quotaTypes[3]);
        quotaTypes[2] += quotaTypes[3] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/3, quotaTypes[2]);
        quotaTypes[1] += quotaTypes[2] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter/2, quotaTypes[1]);
        quotaTypes[0] += quotaTypes[1] - added;
        added = addNotes(selectedNotes, notesOfInterest, quarter, quotaTypes[0]);
        return selectedNotes;
    }

    private static int addNotes(List<MIDINote> selectedNotes, List<MIDINote> notesOfInterest, long granularity, int quota)
    {
        if (quota == 0)
            return 0;
        List<MIDINote> candidates = new ArrayList<>();
        for (MIDINote n : notesOfInterest)
            if ((n.getTick()%granularity) == 0)
                candidates.add(n);
        while (candidates.size() > quota)
            candidates.remove(RND.nextInt(candidates.size()));
        selectedNotes.addAll(candidates);
        notesOfInterest.removeAll(candidates);
        return candidates.size();
    }

    private static int addPatternNotes(SMProject proj, SMChart chart, int quantity, int ticksPerMeasure, DiffProfile diff, List<DanceBlackout> taken)
    {
        int used = 0;
        int quarter = proj.getMIDI().getPulsesPerQuarter();
        for (int k = 0; k < proj.getPatterns().size(); k++)
        {
            PatDef pattern = proj.getPatterns().get(k);
            // aim to be under quota by thinning out pattern
            List<MIDINote> notesOfInterest = new ArrayList<>();
            notesOfInterest.addAll(pattern.getInstances().get(0).getNotes());
            Map<MIDINote, Integer> patNotes = new HashMap<>();
            for (int i = 0; i < notesOfInterest.size(); i++)
                patNotes.put(notesOfInterest.get(i), i);
            List<MIDINote> selectedNotes = thinToQuota(proj.getMIDI(), quarter, diff,
                    notesOfInterest);
            if (selectedNotes.size() < PatternLogic.MIN_INST)
                continue;
            List<Integer> notes = new ArrayList<>();
            List<String> steps = new ArrayList<>();
            for (MIDINote note : selectedNotes)
            {
                notes.add(patNotes.get(note));
                steps.add(randomNote(diff));
            }
                        
            for (int j = 0; j < pattern.getInstances().size(); j++)
            {
                PatInst inst = pattern.getInstances().get(j);
                long startTick = inst.getNotes().get(0).getTick();
                long endTick = inst.getNotes().get(inst.getNotes().size() - 1).getTick();
                if (intersects(startTick, endTick, taken))
                    continue;
                if (startTick%quarter != 0)
                    continue;
                taken.add(new DanceBlackout(startTick, endTick));
                System.out.print("P"+k+"_"+j+": ");
                for (int i = 0; i < notes.size(); i++)
                {
                    Integer idx = notes.get(i);
                    String step = steps.get(i);
                    MIDINote n = inst.getNotes().get(idx);
                    if (setNote(chart, ticksPerMeasure, n.getTick(), step))
                    {
                        used++;
                        System.out.print(DanceLogic.patternToStep(step));
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

    /*
    public static int addRandomNotes(MIDITune midi, SMChart chart,
            long ticksPerMeasure, long quarter, DiffProfile diff, int targetSteps)
    {
        int used = 0;
        Comparator<Long> c = new Comparator<Long>() {            
            @Override
            public int compare(Long o1, Long o2)
            {
                return (int)Math.signum(o1 - o2);
            }
        };
        List<MIDINote> notesOfInterest = findNotes(midi, quarter);
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
            used += setNote(chart, ticksPerMeasure, best.getTick(), randomNote(diff)) ? 1 : 0;
            taken.add(bestidx, best.getTick());
            notesOfInterest.remove(best);
        }
        return used;
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
    private static int addPatternNotes(SMProject proj, SMChart chart, int quantity, int ticksPerMeasure, long quarter, DiffProfile diff, List<DanceBlackout> taken)
    {
        int used = 0;
        for (int k = 0; k < proj.getPatterns().size(); k++)
        {
            PatDef pattern = proj.getPatterns().get(k);
            List<PatNote> notes = new ArrayList<>();
            for (PatNote n : pattern.getNotes())
                if (n.getDeltaTick()%quarter == 0)
                    notes.add(new PatNote(n, randomNote(diff)));
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
                if (startTick%quarter != 0)
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
*/

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

    public static String randomNote(DiffProfile diff)
    {
        if (DanceLogic.RND.nextFloat() > diff.getDoublePC())
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
            throw new IllegalStateException("tick="+tick+", measureIdx="+measureIdx+", start="+m.getStartTick()+", measure="+ticksPerMeasure); // should never happen
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

    private static List<MIDINote> findNotes(MIDITune midi, long startTick, long endTick)
    {
        List<MIDINote> notes = new ArrayList<>();
        for (MIDINote note : midi.getNotes())
            if ((note.getTick() >= startTick) && (note.getTick() <= endTick))
                notes.add(note);
        return notes;
    }
}

class DanceBlackout implements Comparable<DanceBlackout>
{
    long    mStartTick;
    long    mEndTick;
    
    public DanceBlackout(long start, long end)
    {
        mStartTick = start;
        mEndTick = end;
    }
    
    @Override
    public int compareTo(DanceBlackout o)
    {
        return (int)Math.signum(mStartTick - o.mStartTick);
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