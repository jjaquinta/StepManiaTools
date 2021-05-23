package jo.sm.dl.logic.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.sm.dl.data.gen.DanceProfile;
import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.data.sm.pat.PatDef;
import jo.sm.dl.data.sm.pat.PatInst;
import jo.sm.dl.data.sm.pat.PatNote;
import jo.sm.dl.logic.MIDILogic;
import jo.sm.dl.logic.PatternLogic;

public class DefaultPatternStrategy implements IPatternStrategy
{
    @Override
    public String getName()
    {
        return "default";
    }

    @Override
    public void findPatterns(SMProject proj, DanceProfile prof)
    {
        // sort notes by voices
        MIDINote firstNote = null;
        MIDINote lastNote = null;
        Map<Long, List<MIDINote>> voices = new HashMap<>();
        for (MIDINote note : proj.getMIDI().getNotes())
        {
            long v = note.getVoice();
            List<MIDINote> voice = voices.get(v);
            if (voice == null)
            {
                voice = new ArrayList<>();
                voices.put(v, voice);
            }
            if ((voice.size() == 0) || (voice.get(voice.size() - 1).getTick() != note.getTick()))
                voice.add(note);
            if ((firstNote == null) || (note.getTick() < firstNote.getTick()))
                firstNote = note;
            if ((lastNote == null) || (note.getTick() + note.getDuration() > lastNote.getTick() + lastNote.getDuration()))
                lastNote = note;
        }
        int q = proj.getMIDI().getPulsesPerQuarter();
        System.out.println(q+" pulses per quarternote");
        System.out.println(voices.size()+" voices");
        System.out.println("First tick="+firstNote.getTick()+", lastTick="+lastNote.getTick()+"->"+(lastNote.getTick() + lastNote.getDuration()));
        for (Long v : voices.keySet())
        {
            List<MIDINote> voice = voices.get(v);
            int type = proj.getMIDI().getTrackInfos().get(voice.get(0).getTrack()).getType();
            for (int i = 0; i < voice.size() - PatternLogic.MIN_INST; i++)
            {
                if ((voice.get(i).getTick()%q) != 0)
                    continue;   // patterns must start on a beat
                PatDef best = null;
                for (int patLen = PatternLogic.MIN_PAT; i + patLen < voice.size(); patLen++)
                {
                    PatDef def = makePatDef(voice, i, patLen);
                    if (patExists(proj.getPatterns(), def))
                        continue;
                    findInstances(def, voices.values(), q);
                    if (def.getInstances().size() < PatternLogic.MIN_INST)
                        break;
                    if ((best != null) && (def.getInstances().size() < best.getInstances().size()))
                        break;
                    best = def;
                }
                if (best != null)
                {
                    calcLCD(best);
                    best.setType(type);
                    proj.getPatterns().add(best);
                }
            }
        }
        Collections.sort(proj.getPatterns(), new Comparator<PatDef>() {
            @Override
            public int compare(PatDef o1, PatDef o2)
            {
                return o2.getScore(q) - o1.getScore(q);
            }
        });
        System.out.println(proj.getPatterns().size()+" patterns");
        for (int i = 0; i < Math.min(5, proj.getPatterns().size()); i++)
        {
            PatDef def = proj.getPatterns().get(i);
            System.out.println("  len="+def.getNotes().size()+"/"+def.getQLen(q)+", insts="+def.getInstances().size()+", beat="+def.getBeat()+", score="+def.getScore(q)+", type="+def.getType());
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

    private void calcLCD(PatDef best)
    {
        long lcd = best.getNotes().get(1).getDeltaTick();
        for (int i = 2; i < best.getNotes().size(); i++)
            lcd = MIDILogic.lcd(lcd, best.getNotes().get(i).getDeltaTick());
        best.setBeat(lcd);
    }

    private boolean patExists(List<PatDef> patterns, PatDef def)
    {
        for (PatDef def2 : patterns)
            if (equals(def2, def))
                return true;
        return false;
    }

    private boolean equals(PatDef def1, PatDef def2)
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

    private boolean equals(PatNote n1, PatNote n2)
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

    private void findInstances(PatDef def, Collection<List<MIDINote>> voices, int quarter)
    {
        for (List<MIDINote> voice : voices)
        {
            for (int i = 0; i < voice.size() - def.getNotes().size(); i++)
            {
                if ((voice.get(i).getTick()%quarter) != 0)
                    continue;
                if (isPattern(def, voice, i))
                {
                    def.getInstances().add(makePatInst(def, voice, i));
                    i += def.getNotes().size();
                    i--; // since we're going to ++ right away
                }
            }
        }
    }

    private boolean isPattern(PatDef def, List<MIDINote> voice, int idx)
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

    private PatInst makePatInst(PatDef def, List<MIDINote> voice, int idx)
    {
        PatInst inst = new PatInst();
        for (int i = 0; i < def.getNotes().size(); i++)
        {
            inst.getNotes().add(voice.get(idx + i));
        }
        return inst;
    }

    private PatDef makePatDef(List<MIDINote> voice, int o, int l)
    {
        PatDef pat = new PatDef();
        MIDINote base = voice.get(o);
        for (int i = 0; i < l; i++)
        {
            MIDINote n = voice.get(o + i);
            PatNote note = new PatNote();
            note.setIndex(i);
            note.setDeltaPitch(n.getPitch() - base.getPitch());
            note.setDeltaTick(n.getTick() - base.getTick());
            note.setDeltaVelocity(n.getVelocity() - base.getVelocity());
            note.setDuration(n.getDuration());
            pat.getNotes().add(note);
        }
        return pat;
    }
}
