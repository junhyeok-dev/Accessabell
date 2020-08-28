import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class TTSModule {
    public void tts(String text) {
        System.out.println("TTS Module loaded.");

        try {
            TextToSpeechClient textToSpeechClient = TextToSpeechClient.create();
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3).build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            ByteString audio = response.getAudioContent();

            OutputStream output = new FileOutputStream("response.mp3");
            output.write(audio.toByteArray());

            new JFXPanel();
            Media media = new Media(new File("response.mp3").toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            player.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
