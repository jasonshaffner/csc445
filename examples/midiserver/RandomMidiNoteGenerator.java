package midiserver;

import java.util.*;
import org.jfugue.*;

public class RandomMidiNoteGenerator {
  // singleton
  private static RandomMidiNoteGenerator instance = null;
	private Random random;
  
  private RandomMidiNoteGenerator() {
		random = new Random();
	}
  
  public static RandomMidiNoteGenerator getInstance() {
    if (instance == null) instance = new RandomMidiNoteGenerator();
    return instance;
  }
  
	public Note generateNote() {
		byte[] b = new byte[1];  
		random.nextBytes(b);
		b[0] &= ~(1<<7);
		Note n = new Note(b[0], Math.random()/2);
		return n;
	}

	public void editVelocity(Note n) {
		byte[] b = new byte[2];
		random.nextBytes(b);
		b[0] &= ~(1<<7);
		b[1] &= ~(1<<7);
		n.setAttackVelocity(b[0]);
		n.setDecayVelocity(b[1]);
	}

	public void editPitch(Note n) {
		byte b = n.getValue();
		b *= random.nextInt();
		b &= ~(1<<7);
		n.setValue(b);
	}

	public void editDuration(Note n) {
		long l = n.getDuration();
		l *= (Math.random() * 10);
		n.setDuration(l/2);
	}
}	

class MidiNoteGeneratorTester {

	public static void main(String[] args) {
		Player player = new Player();
		Pattern pattern = new Pattern();
		RandomMidiNoteGenerator rmng = RandomMidiNoteGenerator.getInstance();
		HashMap<Integer,Note> hashMap = new HashMap<>();

		for (int i = 0; i < 100; i++) {
			Note n = rmng.generateNote();
			System.out.println(n.getVerifyString());
			hashMap.put(i,n);
		}

		for (Integer i : hashMap.keySet()) {
			rmng.editPitch(hashMap.get(i));
			rmng.editVelocity(hashMap.get(i));
			rmng.editDuration(hashMap.get(i));
			pattern.addElement(hashMap.get(i));
		}	

		player.play(pattern);
	}
}


