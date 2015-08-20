/*******************************************************************************
 PROJECT:       Hive
 FILE:          Utils.java
 DESCRIPTION:   Class for utility methods
 CHANGES:
 Version        Date        Author           Description
 ---------  ----------  ---------------  ------------------------------------
 1.0        16/07/2014  Juan Pablo B.    1. Initial definition.
 *******************************************************************************/
package uk.co.wehive.hive.utils;

import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    private static final String FRAME_DUMP_FOLDER_PATH = Environment.getExternalStorageDirectory() + "";
    private static final String SALT = "DYhG93b0qyJfIxfs2guVoUubWwvniR2G0FgaC9zi";
    private static final int MAX_LENGTH = 30;

    /**
     * Validate a email.
     *
     * @param target email to validate.
     * @return True if email is valid, false other case.
     */
    public final static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void testTrimmingVideo() {
        try {
            H264TrackImpl h264Track = new H264TrackImpl(new BufferedInputStream(new FileInputStream("/mnt/sdcard/encoded.h264")));
            Movie m = new Movie();
            m.addTrack(h264Track);
            IsoFile out = new DefaultMp4Builder().build(m);
            FileOutputStream fos = new FileOutputStream(new File("/mnt/sdcard/encoded.mp4"));
            out.getBox(fos.getChannel());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isVideoTooLong(String currentMultimediaPath) {
        try {
            ReadableByteChannel in = Channels.newChannel((new FileInputStream(currentMultimediaPath)));
            Movie movie = MovieCreator.build(in);
            List<Track> tracks = movie.getTracks();
            double endTime = (double) getDuration(tracks.get(0)) / tracks.get(0).getTrackMetaData().getTimescale();
            return endTime > MAX_LENGTH;
        } catch (Exception e) {
            return true;
        }

    }

    public String trimVideo(int limitMiliseconds, String currentMultimediaPath) {
        try {
            ReadableByteChannel in = Channels.newChannel((new FileInputStream(currentMultimediaPath)));
            Movie movie = MovieCreator.build(in);

            List<Track> tracks = movie.getTracks();
            movie.setTracks(new LinkedList<Track>());

            double startTime = 0;//start from 0 seconds
            double endTime = (double) getDuration(tracks.get(0)) / tracks.get(0).getTrackMetaData().getTimescale();
            endTime = Math.min(endTime, limitMiliseconds);
            boolean timeCorrected = false;

            // Here we try to find a track that has sync samples. Since we can only start decoding
            // at such a sample we SHOULD make sure that the start of the new fragment is exactly
            // such a frame
            for (Track track : tracks) {
                if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                    if (timeCorrected) {
                        // This exception here could be a false positive in case we have multiple tracks
                        // with sync samples at exactly the same positions. E.g. a single movie containing
                        // multiple qualities of the same video (Microsoft Smooth Streaming file)
                        throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                    }
                    startTime = correctTimeToSyncSample(track, startTime, false);
                    endTime = correctTimeToSyncSample(track, endTime, true);
                    Log.i("CreatePostFragment ", "start " + startTime + " end " + endTime);
                    timeCorrected = true;
                }
            }

            for (Track track : tracks) {
                long currentSample = 0;
                double currentTime = 0;
                long startSample = -1;
                long endSample = -1;


                for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
                    TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
                    for (int j = 0; j < entry.getCount(); j++) {
                        // entry.getDelta() is the amount of time the current sample covers.
                        if (currentTime <= startTime) {
                            // current sample is still before the new starttime
                            startSample = currentSample;
                        } else if (currentTime <= endTime) {
                            // current sample is after the new start time and still before the new endtime
                            endSample = currentSample;
                        } else {
                            // current sample is after the end of the cropped video
                            break;
                        }
                        currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                        currentSample++;
                    }
                }
                movie.addTrack(new CroppedTrack(track, startSample, endSample));
            }

            File myMovie = new File(FRAME_DUMP_FOLDER_PATH + File.separator + "4.mp4");

            if (startTime == endTime)
                throw new Exception("times are equal, something went bad in the conversion");
            IsoFile out = new DefaultMp4Builder().build(movie);
            FileOutputStream fos = new FileOutputStream(myMovie);
            FileChannel fc = fos.getChannel();
            out.getBox(fc);
            fc.close();
            fos.close();
            return myMovie.getCanonicalPath();
        } catch (Exception e) {
            Log.d("CreatePostFragment", " trimming error " + e.getMessage());
            return null;
        }
    }

    protected long getDuration(Track track) {
        long duration = 0;
        for (TimeToSampleBox.Entry entry : track.getDecodingTimeEntries()) {
            duration += entry.getCount() * entry.getDelta();
        }
        return duration;
    }

    private double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {
                if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                    // samples always start with 1 but we start with zero therefore +1
                    Log.i("CreatePostFragment ", " track.getSyncSamples() " + track.getSyncSamples() + " currentSample+1 " + currentSample + 1);
                    timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
                    Log.i("CreatePostFragment ", " index " + Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) + " currentTime " + currentTime);
                }
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    public static String getSHAHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String newPassword = SALT + input;
        md.update(newPassword.getBytes("UTF-8"));
        byte[] digest = md.digest();
        StringBuffer hash = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xFF & digest[i]);
            if (hex.length() == 1) {
                hash.append('0');
            }
            hash.append(hex);
        }
        return hash.toString();
    }
}