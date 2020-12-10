package jp.co.abs.onseininnsikitsuboiver3;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public final class HttpGetTask4 extends AsyncTask<String, Void, String> {

    private ArrayList<String> translateText;
    private ListView listView;
    private Context mContext;

    public HttpGetTask4(ArrayList<String> translateText, ListView listView, Context context){
        super();
        this.translateText = translateText;
        this.listView = listView;
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        // 取得したテキストを格納する変数
        final StringBuilder result = new StringBuilder();
        // アクセス先URL
        //final URL url = urls[0];
        HttpURLConnection con = null;
        try {
            URL url = new URL(strings[0]);
            // ローカル処理
            // コネクション取得
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // 通信に成功した
                // テキストを取得する
                final InputStream in = con.getInputStream();
                //final String encoding = con.getContentEncoding();
                final InputStreamReader inReader = new InputStreamReader(in);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                bufReader.close();
                inReader.close();
                in.close();
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);

        if(result != ""){
            translateText.add(result);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.list, translateText);
            listView.setAdapter(adapter);
        }else{
            Toast.makeText(mContext,"翻訳に失敗しました",Toast.LENGTH_SHORT).show();
        }
    }
}