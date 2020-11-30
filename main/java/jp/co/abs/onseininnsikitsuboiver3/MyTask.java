package jp.co.abs.onseininnsikitsuboiver3;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MyTask extends AsyncTask<String,String,String>{

    public MyTask(ListView listView){
        super();
        this.listView = listView;
    }


    ArrayList<String> translateText = new ArrayList<>();

    @Override
    protected String doInBackground(String... language) {
        String getUrl = "https://script.google.com/macros/s/AKfycbw8auBlxHNG0aq14kE-K6CYZk49RwFl-cYfwEtAK2gLdIrp4_c/exec" + "?text=";
        URL url = null;
        StringBuffer result = null;
        try {
            url = new URL(getUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // テキストを取得する
                InputStream in = connection.getInputStream();
                String encoding = connection.getContentEncoding();
                if (null == encoding) {
                    encoding = "UTF-8";
                }
                result = new StringBuffer();
                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;

                while ((line = bufReader.readLine()) != null) {
                    result.append(line);
                }

                bufReader.close();
                inReader.close();
                in.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(result);
    }
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        translateText.add(result);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getClass(), R.layout.list, translateText);
        listView.setAdapter(adapter);
    }
}
