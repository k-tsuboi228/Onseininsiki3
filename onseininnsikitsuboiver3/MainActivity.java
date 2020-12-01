package jp.co.abs.onseininnsikitsuboiver3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //UI
    ImageView imageview;
    EditText onseiResult;

    //音声認識結果のリスト
    ArrayList<String> data;
    ArrayList<String> translateText = new ArrayList<>();

    Intent intent; // SpeechRecognizerに渡すIntent
    SpeechRecognizer recognizer;
    int BUTTON_STATUS = 0;
    String resultsString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview = findViewById(R.id.mic);
        imageview.setOnClickListener(onClick_button);

        onseiResult = findViewById(R.id.onsei);
        onseiResult.setFocusable(false);
        onseiResult.setEnabled(false);
        onseiResult.setTextColor(Color.BLACK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "マイクボタンをタップしてください", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 音声認識中にアプリから離れた場合、認識結果を破棄
        if (data.size() != 0) {
            data.remove(data.size() - 1);
        }

        stopListening();
    }

    private View.OnClickListener onClick_button = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (BUTTON_STATUS) {
                case 0:
                    BUTTON_STATUS++;
                    startListening();
                    break;
                case 1:
                    BUTTON_STATUS--;
                    stopListening();

                      if (resultsString != "") {

                          TextView textView = findViewById(R.id.resultText);
                          textView.setText("音声を聞く: リスト内テキストをタップ");

                          try{
                              new HttpGetTask().execute(new URL(getURL("&source=ja&target=en")));
                          } catch (MalformedURLException e) {
                              e.printStackTrace();
                          }
                          try{
                              new HttpGetTask().execute(new URL(getURL("&source=ja&target=fr")));
                          } catch (MalformedURLException e) {
                              e.printStackTrace();
                          }
                          try{
                              new HttpGetTask().execute(new URL(getURL("&source=ja&target=it")));
                          } catch (MalformedURLException e) {
                              e.printStackTrace();
                          }
                          try{
                              new HttpGetTask().execute(new URL(getURL("&source=ja&target=de")));
                          } catch (MalformedURLException e) {
                              e.printStackTrace();
                          }
                          try{
                              new HttpGetTask().execute(new URL(getURL("&source=ja&target=es")));
                          } catch (MalformedURLException e) {
                              e.printStackTrace();
                          }

                          ListView listView =(ListView) MainActivity.this.findViewById(R.id.list);
                          //ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list, translateText);
                          ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list, translateText);
                          listView.setAdapter(adapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                    }
                break;
            }
        }
    };

    public String getURL(String language){
        String masterURL = "https://script.google.com/macros/s/AKfycbw8auBlxHNG0aq14kE-K6CYZk49RwFl-cYfwEtAK2gLdIrp4_c/exec?text=";
        String APIUrl = masterURL + resultsString + language;

        return APIUrl;
    }

    protected void startListening() {
        try {
            if (recognizer == null) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(this);
                if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "音声認識が使えません",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                recognizer.setRecognitionListener(new listener());
            }

            if (getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size() == 0) {
                return;
            }
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            recognizer.startListening(intent);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "startListening()でエラーが起こりました",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // 音声認識を終了する
    protected void stopListening() {
        if (recognizer != null) recognizer.destroy();
        recognizer = null;
    }

    // 音声認識を再開する
    public void restartListeningService() {
        stopListening();
        startListening();
    }


    class listener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getApplicationContext(), "音声を入力してください", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            imageview.setImageResource(R.drawable.mic2);
        }

        @Override
        public void onEndOfSpeech() {
            imageview.setImageResource(R.drawable.mic);
        }

        @Override
        public void onResults(Bundle results) {
            data = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            
            for (int i = 0; i < data.size(); i++) {
                resultsString += data.get(i);
            }

            onseiResult.setText(resultsString);
            Toast.makeText(getApplicationContext(), "マイクボタンをタップしてください", Toast.LENGTH_SHORT).show();
        }


        public void onError(int error) {
            Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();

            // 音声認識を繰り返す
            restartListeningService();
        }

        // その他のメソッド RecognitionListenerの特性上記述が必須
        public void onRmsChanged(float v) {
        }

        public void onBufferReceived(byte[] bytes) {
        }

        public void onPartialResults(Bundle bundle) {
        }

        public void onEvent(int i, Bundle bundle) {
        }
    }
}

