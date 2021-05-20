package jo.sm.dle.logic;

import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.sm.SMBeat;
import jo.sm.dle.data.SongBean;

public class SelectionLogic
{
    public static void addToSelection(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (MIDINote note : notes)
            song.getSelectedNotes().add(note);
        song.fireMonotonicPropertyChange("selectedNotes");
    }
    
    public static void toggleSelection(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (MIDINote note : notes)
            if (song.getSelectedNotes().contains(note))
                song.getSelectedNotes().remove(note);
            else
                song.getSelectedNotes().add(note);
        song.fireMonotonicPropertyChange("selectedNotes");
    }
    
    public static void setSelection(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getSelectedNotes().clear();
        for (MIDINote note : notes)
            song.getSelectedNotes().add(note);
        song.fireMonotonicPropertyChange("selectedNotes");
    }
    
    public static void removeFromSelection(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (MIDINote note : notes)
            song.getSelectedNotes().remove(note);
        song.fireMonotonicPropertyChange("selectedNotes");
    }
    
    public static void clearSelection()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getSelectedNotes().clear();
        song.fireMonotonicPropertyChange("selectedNotes");
    }
    
    public static void addToHighlights(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (MIDINote note : notes)
            song.getNoteHighlights().add(note);
        song.fireMonotonicPropertyChange("noteHighlights");
    }
    
    public static void setHighlights(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getNoteHighlights().clear();
        for (MIDINote note : notes)
            song.getNoteHighlights().add(note);
        song.fireMonotonicPropertyChange("noteHighlights");
    }
    
    public static void removeFromHighlights(MIDINote... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (MIDINote note : notes)
            song.getNoteHighlights().remove(note);
        song.fireMonotonicPropertyChange("noteHighlights");
    }
    
    public static void clearHighlights()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getNoteHighlights().clear();
        song.fireMonotonicPropertyChange("noteHighlights");
    }
    
    public static void addToBeats(SMBeat... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (SMBeat note : notes)
            song.getSelectedBeats().add(note);
        song.fireMonotonicPropertyChange("selectedBeats");
    }
    
    public static void toggleBeats(SMBeat... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (SMBeat note : notes)
            if (song.getSelectedBeats().contains(note))
                song.getSelectedBeats().remove(note);
            else
                song.getSelectedBeats().add(note);
        song.fireMonotonicPropertyChange("selectedBeats");
    }
    
    public static void setBeats(SMBeat... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getSelectedBeats().clear();
        for (SMBeat note : notes)
            song.getSelectedBeats().add(note);
        song.fireMonotonicPropertyChange("selectedBeats");
    }
    
    public static void removeFromBeats(SMBeat... notes)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (SMBeat note : notes)
            song.getSelectedBeats().remove(note);
        song.fireMonotonicPropertyChange("selectedBeats");
    }
    
    public static void clearBeats()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getSelectedBeats().clear();
        song.fireMonotonicPropertyChange("selectedBeats");
    }

}
