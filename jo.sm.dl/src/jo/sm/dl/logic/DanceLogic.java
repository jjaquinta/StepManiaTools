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
import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMMark;
import jo.sm.dl.data.SMMeasure;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.data.SMTune;

public class DanceLogic
{
    private static Random RND = new Random();
    
    public static void dance(SMProject proj)
    {
        MIDITune midi = proj.getMIDI();
        SMTune steps = proj.getTune();
        steps.setDisplayBPM(midi.getBeatsPerMinute());
        steps.getBPMs().add(new SMMark(0, midi.getBeatsPerMinute()));
        long ticksPerMeasure = midi.getPulsesPerQuarter()*4;
        for (long tick = 0; tick < midi.getLengthInTicks(); tick += ticksPerMeasure)
        {
            SMMeasure m = new SMMeasure();
            m.setStartTick(tick);
            m.getBeats().add(new SMBeat(tick + midi.getPulsesPerQuarter()*0));
            m.getBeats().add(new SMBeat(tick + midi.getPulsesPerQuarter()*1));
            m.getBeats().add(new SMBeat(tick + midi.getPulsesPerQuarter()*2));
            m.getBeats().add(new SMBeat(tick + midi.getPulsesPerQuarter()*3));
            steps.getMeasures().add(m);
        }
        int targetSteps = (int)midi.getLengthInSeconds();
        int used = addPatternNotes(proj, targetSteps, (int)ticksPerMeasure, midi.getPulsesPerQuarter());
        System.out.println("Pattern steps: "+used);
        if (used < targetSteps)
        {
            List<MIDINote> notesOfInterest = findNotes(midi, targetSteps - used, midi.getPulsesPerQuarter());
            for (MIDINote note : notesOfInterest)
                used += setNote(steps, ticksPerMeasure, note.getTick(), randomNote()) ? 1 : 0;
        }
        System.out.println("Total steps: "+used);
        steps.setNotesDescription(System.getProperty("user.name"));
        steps.setNotesChartType("dance-single");
        steps.setNotesDifficulty("Easy");
        steps.setNotesMeter(1);
    }

    private static int addPatternNotes(SMProject proj, int quantity, int ticksPerMeasure, long granularity)
    {
        int used = 0;
        for (int k = 0; k < proj.getPatterns().size(); k++)
        {
            PatDef pattern = proj.getPatterns().get(k);
            for (int i = 0; i < pattern.getNotes().size(); i++)
                pattern.getSteps().add(randomNote());
            for (int j = 0; j < pattern.getInstances().size(); j++)
            {
                PatInst inst = pattern.getInstances().get(j);
                for (int i = 0; i < inst.getNotes().size(); i++)
                {
                    MIDINote n = inst.getNotes().get(i);
                    if (n.getTick()%granularity == 0)
                    {
                        if (setNote(proj.getTune(), ticksPerMeasure, n.getTick(), pattern.getSteps().get(i)))
                            used++;
                    }
                }
                if (proj.getFlags().contains(SMProject.MARK_PATTERNS))
                {
                    float start = inst.getNotes().get(0).getTick()*proj.getMIDI().getMSPerTick()/1000.0f;
                    float stop = inst.getNotes().get(inst.getNotes().size() - 1).getTick()*proj.getMIDI().getMSPerTick()/1000.0f;
                    proj.getTune().getLyrics().add(new SMMark(start, "P"+k+"_"+j+" start"));
                    proj.getTune().getLyrics().add(new SMMark(stop, "P"+k+"_"+j+" end"));
                }
            }
            if (used > quantity)
                break;
        }
        return used;
    }

    public static String randomNote()
    {
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
        return "0000";
    }
    
    private static boolean setNote(SMTune steps, long ticksPerMeasure, long tick, String step)
    {
        SMMeasure m = steps.getMeasures().get((int)(tick/ticksPerMeasure));
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

    private static List<MIDINote> findNotes(MIDITune midi, int quantity, long granularity)
    {
        List<MIDINote> notes = new ArrayList<>();
        notes.addAll(midi.getNotes());
        // remove those off acceptable beat
        for (Iterator<MIDINote> i = notes.iterator(); i.hasNext(); )
            if (i.next().getTick()%granularity != 0)
                i.remove();
        // sort by velocity
        Collections.sort(notes, new Comparator<MIDINote>() {
            public int compare(MIDINote o1, MIDINote o2) {
                return (o2.getVelocity() - o1.getVelocity());
            };
        });
        // pick highest velociy
        for (int i = 0; i < quantity; i++)
        {
            if (notes.size() <= i)
                break;
            MIDINote baseNote = notes.get(i);
            long baseTick = baseNote.getTick();
            // remove any others that are too close
            for (int j = notes.size() - 1; j > i; j--)
            {
                long noteTick = notes.get(j).getTick();
                if (Math.abs(noteTick - baseTick) < granularity)
                    notes.remove(j);
            }
        }
        while (notes.size() > quantity)
            notes.remove(quantity);
        return notes;
    }
}
