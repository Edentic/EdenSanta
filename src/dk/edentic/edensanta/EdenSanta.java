package dk.edentic.edensanta;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

import twitter4j.*;

public class EdenSanta {
	static Arm arm;
	static String hashTag = "#edentic";
	static SocketIO io;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		arm = new Arm();
		
		try {
			io = new SocketIO("http://192.168.1.35:8000");
			io.connect(new IOCallback() {
				
				@Override
				public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
					System.out.println("BESKED");
					System.out.println(arg0.toString());
				}
				
				@Override
				public void onMessage(String arg0, IOAcknowledge arg1) {
					System.out.println("BESKED");
					System.out.println(arg0);
				}
				
				@Override
				public void onError(SocketIOException arg0) {
					System.out.println("SOCKET FEJL!");
					System.out.println(arg0.getMessage());
					
				}
				
				@Override
				public void onDisconnect() {
					System.out.println("DISCONNECT");
					
				}
				
				@Override
				public void onConnect() {
					System.out.println("Connected!");
				}
				
				@Override
				public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		twitterListener();
		isThereAnybody();
	}
	
	public static void twitterListener() {
		ArrayList<String> track = new ArrayList<String>();
		track.add(hashTag);
		
		//if(io.isConnected()) {
			io.emit("newTweet", "Test tweet!");
		//}
		
		StatusListener listener = new StatusListener() {
			
			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStatus(Status arg0) {
				//if(io.isConnected()) {
				JSONObject json = new JSONObject();
				try {
					json.put("picture", arg0.getUser().getProfileImageURL());
					json.put("username", arg0.getUser().getName());
					json.put("text", arg0.getText());
					if(arg0.getMediaEntities().length > 0) {
						json.put("mediaUrl", arg0.getMediaEntities()[0].getMediaURL());
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					io.emit("newTweet", json.toString());
				//}
				arm.openArm();
				arm.rotateEyes();
				arm.shakeHand(5);
				arm.closeArm();
			}
			
			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		
		TwitterStream stream = new TwitterStreamFactory().getInstance();
		stream.addListener(listener);
		
		String[] trackArray = track.toArray(new String[track.size()]);
		stream.filter(new FilterQuery(0, null, trackArray));
	}
	
	public static void isThereAnybody() {
		Port distancePort = LocalEV3.get().getPort("S1");
		SensorModes sensor = new EV3IRSensor(distancePort);
		final SampleProvider distance = sensor.getMode("Distance");
		final float[] sample = new float[distance.sampleSize()];
		
		System.out.println("Hall¿jsa!");
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
				distance.fetchSample(sample, 0);
				float s = sample[0];
				if(s <= 57) {
					
					System.out.println("Hej med dig!");
					System.out.println(s);
					arm.shakeHand(1);
				}
		    }
		}, 0, 1000);
	}

}
