package jo.sm.dl.logic.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jo.sm.dl.data.gen.DiffProfile;
import jo.sm.dl.data.midi.MIDINotation;
import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.midi.MIDITune;

public class AlignedChartStrategy extends DefaultChartStrategy
{
    public static final String NAME = "aligned";

    @Override
    public String getName()
    {
        return NAME;
    }

    protected List<MIDINote> thinToQuota(MIDITune midi, long ppq,
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
        added = 0;//addNotes(selectedNotes, notesOfInterest, ppq/48, quotaTypes[8]);
        quotaTypes[7] += quotaTypes[8] - added;
        added = 0;//addNotes(selectedNotes, notesOfInterest, ppq/16, quotaTypes[7]);
        quotaTypes[6] += quotaTypes[7] - added;
        added = 0;//addNotes(selectedNotes, notesOfInterest, ppq/12, quotaTypes[6]);
        quotaTypes[5] += quotaTypes[6] - added;
        added = 0;//addNotes(selectedNotes, notesOfInterest, ppq/8, quotaTypes[5]);
        quotaTypes[4] += quotaTypes[5] - added;
        added = 0;//addNotes(selectedNotes, notesOfInterest, ppq/6, quotaTypes[4]);
        quotaTypes[3] += quotaTypes[4] - added;
        added = addNotes(midi, selectedNotes, notesOfInterest, 8, quotaTypes[3]);
        quotaTypes[2] += quotaTypes[3] - added;
        added = 0;//addNotes(selectedNotes, notesOfInterest, ppq/3, quotaTypes[2]);
        quotaTypes[1] += quotaTypes[2] - added;
        added = addNotes(midi, selectedNotes, notesOfInterest, 16, quotaTypes[1]);
        quotaTypes[0] += quotaTypes[1] - added;
        added = addNotes(midi, selectedNotes, notesOfInterest, 32, quotaTypes[0]);
        return selectedNotes;
    }

    private int addNotes(MIDITune midi, List<MIDINote> selectedNotes, List<MIDINote> notesOfInterest, int mod, int quota)
    {
        if (quota == 0)
            return 0;
        List<MIDINote> candidates = new ArrayList<>();
        for (MIDINote n : notesOfInterest)
        {
            MIDINotation nn = midi.getNotation(n);
            if (nn == null)
                continue;
            if ((nn.getAlignedStart()%mod) == 0)
                candidates.add(n);
        }
        while (candidates.size() > quota)
            candidates.remove(RND.nextInt(candidates.size()));
        selectedNotes.addAll(candidates);
        notesOfInterest.removeAll(candidates);
        return candidates.size();
    }
}
