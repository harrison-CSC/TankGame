package tankrotationexample.game;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicManager
{
	private static AudioInputStream audioStream;
	private static Clip clip;
	private static FloatControl volume;
	
	static
	{
		try 
		{
			audioStream = AudioSystem.getAudioInputStream(GameWorld.class.getClassLoader().getResource("Music/Music1.wav"));
	        clip = AudioSystem.getClip();
	        clip.open(audioStream);	        
	        clip.loop(Clip.LOOP_CONTINUOUSLY);
	        volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        setVolume(50);
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
		{		
			e.printStackTrace();
		}        
	}
	
	public static void setPlaying(boolean b)
	{
		if (b)
			clip.start();
		else
			clip.stop();
	}
	
	public static void setVolume(int volumeLevel)
	{
		float desiredFraction = volumeLevel / 100f;				
		
		float logVal = (float) Math.log10(desiredFraction) * 30f;				
		volume.setValue(logVal);
	}

}
