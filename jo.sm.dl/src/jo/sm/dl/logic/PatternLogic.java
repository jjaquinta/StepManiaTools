package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.PatDef;
import jo.sm.dl.data.PatInst;
import jo.sm.dl.data.PatNote;
import jo.sm.dl.data.SMProject;

public class PatternLogic
{
    private static final int MIN_PAT = 4;
    private static final int MIN_INST = 4;
    
    public static void findPatterns(SMProject proj)
    {
        Map<Long, List<MIDINote>> voices = new HashMap<>();
        for (MIDINote note : proj.getMIDI().getNotes())
        {
            long v = ((long)note.getTrack()<<32) | ((long)note.getBank()<<16) | ((long)note.getProgram()<<0);
            List<MIDINote> voice = voices.get(v);
            if (voice == null)
            {
                voice = new ArrayList<>();
                voices.put(v, voice);
            }
            if ((voice.size() == 0) || (voice.get(voice.size() - 1).getTick() != note.getTick()))
                voice.add(note);
        }
        System.out.println(proj.getMIDI().getPulsesPerQuarter()+" pulses per quarternote");
        System.out.println(voices.size()+" voices");
        for (Long v : voices.keySet())
        {
            List<MIDINote> voice = voices.get(v);
            for (int i = 0; i < voice.size() - MIN_PAT; i++)
            {
                PatDef best = null;
                for (int patLen = MIN_PAT; i + patLen < voice.size(); patLen++)
                {
                    PatDef def = makePatDef(voice, i, patLen);
                    if (patExists(proj.getPatterns(), def))
                        continue;
                    findInstances(def, voices.values());
                    if (def.getInstances().size() < MIN_INST)
                        break;
                    if ((best != null) && (def.getInstances().size() < best.getInstances().size()))
                        break;
                    best = def;
                }
                if (best != null)
                {
                    calcLCD(best);
                    proj.getPatterns().add(best);
                }
            }
        }
        Collections.sort(proj.getPatterns(), new Comparator<PatDef>() {
            @Override
            public int compare(PatDef o1, PatDef o2)
            {
                //return o2.getInstances().size()*o2.getNotes().size() - o1.getInstances().size()*o1.getNotes().size();
                return o2.getNotes().size() - o1.getNotes().size();
            }
        });
        System.out.println(proj.getPatterns().size()+" patterns");
        for (int i = 0; i < Math.min(5, proj.getPatterns().size()); i++)
        {
            PatDef def = proj.getPatterns().get(i);
            System.out.println("  len="+def.getNotes().size()+", insts="+def.getInstances().size()+", beat="+def.getBeat());
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
    
    private static void calcLCD(PatDef best)
    {
        long lcd = best.getNotes().get(1).getDeltaTick();
        for (int i = 2; i < best.getNotes().size(); i++)
            lcd = MIDILogic.lcd(lcd, best.getNotes().get(i).getDeltaTick());
        best.setBeat(lcd);
    }

    private static boolean patExists(List<PatDef> patterns, PatDef def)
    {
        for (PatDef def2 : patterns)
            if (equals(def2, def))
                return true;
        return false;
    }

    private static boolean equals(PatDef def1, PatDef def2)
    {
        if (def1.getNotes().size() != def2.getNotes().size())
            return false;
        for (int i = 0; i < def1.getNotes().size(); i++)
        {
            PatNote n1 = def1.getNotes().get(i);
            PatNote n2 = def2.getNotes().get(i);
            if (!equals(n1, n2))
                return false;
        }
        return true;
    }

    private static boolean equals(PatNote n1, PatNote n2)
    {
        if (n1.getDeltaPitch() != n2.getDeltaPitch())
            return false;
//        if (n1.getDeltaVelocity() != n2.getDeltaVelocity())
//            return false;
        if (n1.getDeltaTick() != n2.getDeltaTick())
            return false;
        if (n1.getDuration() != n2.getDuration())
            return false;
        return true;
    }

    private static void findInstances(PatDef def, Collection<List<MIDINote>> voices)
    {
        for (List<MIDINote> voice : voices)
        {
            for (int i = 0; i < voice.size() - def.getNotes().size(); i++)
                if (isPattern(def, voice, i))
                    def.getInstances().add(makePatInst(def, voice, i));
        }
    }

    private static boolean isPattern(PatDef def, List<MIDINote> voice, int idx)
    {
        MIDINote base = voice.get(idx);
        for (int i = 1; i < def.getNotes().size(); i++)
        {
            MIDINote n = voice.get(idx + i);
            PatNote note = def.getNotes().get(i);
            if ((n.getPitch() - base.getPitch()) != note.getDeltaPitch())
                return false;
//            if ((n.getVelocity() - base.getVelocity()) != note.getDeltaVelocity())
//                return false;
            if ((n.getTick() - base.getTick()) != note.getDeltaTick())
                return false;
            if (n.getDuration() != note.getDuration())
                return false;
        }
        return true;
    }

    private static PatInst makePatInst(PatDef def, List<MIDINote> voice, int idx)
    {
        PatInst inst = new PatInst();
        for (int i = 0; i < def.getNotes().size(); i++)
        {
            inst.getNotes().add(voice.get(idx + i));
        }
        return inst;
    }

    private static PatDef makePatDef(List<MIDINote> voice, int o, int l)
    {
        PatDef pat = new PatDef();
        MIDINote base = voice.get(o);
        for (int i = 0; i < l; i++)
        {
            MIDINote n = voice.get(o + i);
            PatNote note = new PatNote();
            note.setDeltaPitch(n.getPitch() - base.getPitch());
            note.setDeltaTick(n.getTick() - base.getTick());
            note.setDeltaVelocity(n.getVelocity() - base.getVelocity());
            note.setDuration(n.getDuration());
            pat.getNotes().add(note);
        }
        return pat;
    }
}
