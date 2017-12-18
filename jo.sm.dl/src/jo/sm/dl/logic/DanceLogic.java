package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
            diff.reset();
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
            m.getBeats().add(new SMBeat(m, tick + quarter*0, 4));
            m.getBeats().add(new SMBeat(m, tick + quarter*1, 4));
            m.getBeats().add(new SMBeat(m, tick + quarter*2, 4));
            m.getBeats().add(new SMBeat(m, tick + quarter*3, 4));
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
        List<SMBeat> beats = new ArrayList<>();
        for (MIDINote note : selectedNotes)
        {
            SMBeat beat = setNote(chart, ticksPerMeasure, note, null, randomNote(diff));
            if (beat != null)
                beats.add(beat);
        }
        annotateBeats(chart, ticksPerMeasure, midi, diff, beats, startTick, endTick);
        return beats.size();
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
                List<SMBeat> beats = new ArrayList<>();
                for (int i = 0; i < notes.size(); i++)
                {
                    Integer idx = notes.get(i);
                    String step = steps.get(i);
                    MIDINote n = inst.getNotes().get(idx);
                    SMBeat beat = setNote(chart, ticksPerMeasure, n, null, step);
                    if (beat != null)
                    {
                        used++;
                        System.out.print(DanceLogic.patternToStep(step));
                        beats.add(beat);
                    }
                }
                System.out.println();
                annotateBeats(chart, ticksPerMeasure, proj.getMIDI(), diff, beats, startTick, endTick);
                if (proj.isFlag(SMProject.MARK_PATTERNS))
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
        StringBuffer step = new StringBuffer();
        for (int i = 0; i < 4; i++)
        {
            char ch = pattern.toCharArray()[i];
            if (ch == SMBeat.NOTE_NORMAL)
                step.append("<^v>".charAt(i));
            else if (ch == SMBeat.NOTE_HOLD_HEAD)
                step.append("@>");
            else if (ch == SMBeat.NOTE_HOLD_RELEASE)
                step.append("<@");
            else if (ch == SMBeat.NOTE_MINE)
                step.append("!");
        }
        if (step.length() > 1)
        {
            step.insert(0, "(");
            step.append(")");
        }
        return step.toString();
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
        int roll;
        if (diff.isNoBackArrow())
            roll = DanceLogic.RND.nextInt(3);
        else
            roll = DanceLogic.RND.nextInt(4);
        switch (roll)
        {
            case 0:
                return "1000";
            case 1:
                return "0010";
            case 2:
                return "0001";
            case 3:
                return "0100";
        }
        return "0000";
    }
    
    private static SMBeat setNote(SMChart chart, long ticksPerMeasure, MIDINote note, Long tick, String step)
    {
        if (step.equals("0000"))
            return null;
        if (tick == null)
            tick = note.getTick();
        int measureIdx = (int)(tick/ticksPerMeasure);
        if (measureIdx >= chart.getMeasures().size())
            return null;
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
            b.setNote(note);
            return b;
        }
        else
            return null;
    }
    
    private static void expandMeasure(SMMeasure m, long newGap)
    {
        if (m.getBeats().size() == 64)
            throw new IllegalStateException("Got as high fidelity as we can");
        int alignment = m.getBeats().size()*2;
        for (int i = m.getBeats().size() - 1; i >= 0; i--)
        {
            SMBeat oldBeat = m.getBeats().get(i);
            SMBeat newBeat = new SMBeat(m, oldBeat.getTick() + newGap, alignment);
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

    private static void annotateBeats(SMChart chart, long ticksPerMeasure, MIDITune midi, 
            DiffProfile diff, List<SMBeat> beats, long startTick, long endTick)
    {
        Collections.sort(beats);
//        float minuteStart = midi.tickToMinutes(beats.get(0).getTick());
//        float minuteEnd = midi.tickToMinutes(beats.get(beats.size() - 1).getTick());
//        float minuteElapsed = minuteEnd - minuteStart;
        annotateDoubles(diff, beats);
        annotateHolds(chart, ticksPerMeasure, diff, beats);
        annotateMines(ticksPerMeasure, diff, beats);
    }

    private static void annotateDoubles(DiffProfile diff, List<SMBeat> beats)
    {
        final Map<SMBeat, Long> suitability = new HashMap<>();
        for (SMBeat beat: beats)
        {
            MIDINote note = beat.getNote();
            long loud = note.getVelocity() + note.getExpression() + note.getVolume();
            loud /= (beat.getAlignment()/4); // negative bias off of quarter notes 
            suitability.put(beat, loud);
        }
        float quotaF = beats.size()*diff.getDoublePC() + diff.getDoubleRoundOff();
        int quota = (int)quotaF;
        diff.setDoubleRoundOff(quotaF - quota);
        if (quota > 0)
        {
            List<SMBeat> candidates = new ArrayList<>();
            candidates.addAll(beats);
            Collections.sort(candidates, new Comparator<SMBeat>() {
                @Override
                public int compare(SMBeat o1, SMBeat o2)
                {
                    long l1 = suitability.get(o1);
                    long l2 = suitability.get(o2);
                    return (int)Math.signum(l2 - l1);
                }
            });
            for (int i = 0; i < quota; i++)
            {
                SMBeat choice = candidates.get(i);
                int idx = beats.indexOf(choice);
                SMBeat before = (idx > 0) ? beats.get(idx - 1) : null;
                SMBeat after = (idx < beats.size() - 1) ? beats.get(idx + 1) : null;
                Set<Integer> choices = new HashSet<>();
                choices.add(0);choices.add(1);choices.add(2);choices.add(3);
                removeChoices(choices, choice.getNotes());
                if (before != null)
                    removeChoices(choices, before.getNotes());
                if (after != null)
                    removeChoices(choices, after.getNotes());
                if (choices.size() == 0)
                {
                    choices.add(0);choices.add(1);choices.add(2);choices.add(3);
                    removeChoices(choices, choice.getNotes());
                }
                int chIDX = choices.toArray(new Integer[]{})[RND.nextInt(choices.size())];
                choice.getNotes()[chIDX] = '1';
            }
        }
    }

    private static void annotateHolds(SMChart chart,
            long ticksPerMeasure, DiffProfile diff, List<SMBeat> beats)
    {
        final Map<SMBeat, Long> suitability = new HashMap<>();
        long q = ticksPerMeasure/4;
        for (int i = 0; i < beats.size(); i++)
        {
            SMBeat beat = beats.get(i);
            if (beat.getTick()%q != 0)
                continue; // start on a quarter
            if (beat.getHowManySteps() != 1)
                continue;
            long beatEnd = beat.getTick() + beat.getNote().getDuration();
            beatEnd -= beatEnd%q; // end on nearest quarter note
            boolean conflict = false;
            for (int j = i + 1; j < beats.size(); j++)
            {
                SMBeat beat2 = beats.get(j);
                if (beat2.getTick() > beatEnd)
                    break;
                if (beat.overlap(beat2) || (beat2.getHowManySteps() != 1))
                {
                    conflict = true;
                    break;
                }
            }
            if (conflict)
                continue;
            MIDINote note = beat.getNote();
            long loud = note.getVelocity() + note.getExpression() + note.getVolume();
            loud /= (beat.getAlignment()/4); // negative bias off of quarter notes 
            long score = loud*beat.getNote().getDuration(); // long notes good
            suitability.put(beat, score);
        }
        float quotaF = beats.size()*diff.getHoldPC() + diff.getHoldRoundOff();
        int quota = (int)quotaF;
        diff.setHoldRoundOff(quotaF - quota);
        if (quota <= 0)
            return;
        if (suitability.size() == 0)
            return;
        if (quota > suitability.size())
            quota = suitability.size();
        List<SMBeat> candidates = new ArrayList<>();
        candidates.addAll(suitability.keySet());
        Collections.sort(candidates, new Comparator<SMBeat>() {
            @Override
            public int compare(SMBeat o1, SMBeat o2)
            {
                long l1 = suitability.get(o1);
                long l2 = suitability.get(o2);
                return (int)Math.signum(l2 - l1);
            }
        });
        for (int i = 0; i < quota; i++)
        {
            SMBeat choice = candidates.get(i);
            int holdPoint = -1;
            for (int j = 0; j < choice.getNotes().length; j++)
                if (choice.getNotes()[j] == SMBeat.NOTE_NORMAL)
                {
                    choice.getNotes()[j] = SMBeat.NOTE_HOLD_HEAD;
                    holdPoint = j;
                    break;
                }
            char[] stepch = "0000".toCharArray();
            stepch[holdPoint] = SMBeat.NOTE_HOLD_RELEASE;
            String step = new String(stepch);
            long beatEnd = choice.getTick() + choice.getNote().getDuration();
            beatEnd -= beatEnd%q; // end on nearest quarter note
            setNote(chart, ticksPerMeasure, null, beatEnd, step);
        }
    }

    private static void annotateMines(long ticksPerMeasure, DiffProfile diff, List<SMBeat> beats)
    {
        final Map<SMBeat, Long> suitability = new HashMap<>();
        long q = ticksPerMeasure/4;
        for (int i = 0; i < beats.size(); i++)
        {
            SMBeat beat = beats.get(i);
            if (beat.getTick()%q != 0)
                continue; // start on a quarter
            if (beat.getHowManySteps() != 1)
                continue;
            MIDINote note = beat.getNote();
            long loud = note.getVelocity() + note.getExpression() + note.getVolume();
            loud /= (beat.getAlignment()/4); // negative bias off of quarter notes 
            long score = loud*beat.getNote().getDuration(); // long notes good
            suitability.put(beat, score);
        }
        float quotaF = beats.size()*diff.getMinesPC() + diff.getMinesRoundOff();
        int quota = (int)quotaF;
        diff.setMinesRoundOff(quotaF - quota);
        if (quota <= 0)
            return;
        if (suitability.size() == 0)
            return;
        if (quota > suitability.size())
            quota = suitability.size();
        List<SMBeat> candidates = new ArrayList<>();
        candidates.addAll(suitability.keySet());
        Collections.sort(candidates, new Comparator<SMBeat>() {
            @Override
            public int compare(SMBeat o1, SMBeat o2)
            {
                long l1 = suitability.get(o1);
                long l2 = suitability.get(o2);
                return (int)Math.signum(l2 - l1);
            }
        });
        for (int i = 0; i < quota; i++)
        {
            SMBeat choice = candidates.get(i);
            List<Integer> indexes = new ArrayList<>();
            for (int j = 0; j < choice.getNotes().length; j++)
                if (choice.getNotes()[j] == SMBeat.NOTE_NONE)
                    indexes.add(j);
            int idx = indexes.get(RND.nextInt(indexes.size()));
            choice.getNotes()[idx] = SMBeat.NOTE_MINE;
        }
    }

    private static void removeChoices(Set<Integer> choices, char[] notes)
    {
        for (int i = 0; i < notes.length; i++)
            if (notes[i] != '0')
                choices.remove(i);
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