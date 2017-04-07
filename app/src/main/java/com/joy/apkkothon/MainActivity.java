package com.joy.apkkothon;



import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static List<Post> postLists = new ArrayList<>();
    private String addr = "https://apkkothon.net/jg/get_posts?page=";
    private RecyclerView recyclerView;
    public  MyAdapter adapter;
    private AdView mAdView;
    private AlertDialog.Builder builder,builderNet;
    private  int pages;
    JsonTask jsonTask;
    int z =0;
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jsonTask= new JsonTask();
        getSupportActionBar().setTitle("APK কথন");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        avi=(AVLoadingIndicatorView)findViewById(R.id.avi);
        adapter=new MyAdapter(this,postLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit APK কথন ?");
        builder.setCancelable(false);
        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builderNet=new AlertDialog.Builder(this);
        builderNet.setMessage("Internet connection not available");
        builderNet.setCancelable(false);
        builderNet.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        if (!isNetworkAvailable()) { // loading offline
            builderNet.show();
        }

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        if(postLists.isEmpty()){

            for(int i=1;i<25;i++)
            {

                String s = Integer.toString(i);
                String address = addr + s;
                jsonTask=new JsonTask();
                jsonTask.execute(address);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                if(jsonTask.getStatus() == AsyncTask.Status.FINISHED)
                {
                    Toast.makeText(this,"Updating",Toast.LENGTH_LONG).show();
                    z=0;
                    postLists.clear();
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(adapter);
                    for(int i=1;i<25;i++)
                    {
                        String s = Integer.toString(i);
                        String address = addr + s;
                        jsonTask=new JsonTask();
                        jsonTask.execute(address);
                    }
                }
                else {
                    Toast.makeText(this,"Posts are up to date. Try later",Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        builder.show();

    }

    protected class JsonTask extends AsyncTask<String,Void,Void> {
            @Override
            public Void doInBackground(String...params){
                BufferedReader bufferedReader=null;
                HttpURLConnection connection=null;
                    try{
                        URL url=new URL(params[0]);

                        connection=(HttpURLConnection)url.openConnection();
                        connection.connect();
                        InputStream stream=connection.getInputStream();
                        bufferedReader=new BufferedReader(new InputStreamReader(stream));
                        StringBuffer stringBuffer=new StringBuffer();
                        String line="";
                        while((line=bufferedReader.readLine())!=null){
                            stringBuffer.append(line);
                        }
                        String finalStr=stringBuffer.toString();
                        try{
                            JSONObject parentobject=new JSONObject(finalStr);
                            pages=parentobject.getInt("pages");
                            System.out.println(pages);
                            JSONArray posts=parentobject.getJSONArray("posts");
                            for(int j=0;j<posts.length();j++) {
                                JSONObject finalobject = posts.getJSONObject(j);
                                Post p = new Post();
                                p.setContent(finalobject.getString("content"));
                                p.setTitle(finalobject.getString("title"));
                                p.setDate(finalobject.getString("date"));
                                p.setThumbnail(finalobject.getString("thumbnail"));
                                p.setUrl(finalobject.getString("url"));
                                JSONObject authorArray=finalobject.getJSONObject("author");
                                p.setAuthor(authorArray.getString("name"));
                                postLists.add(p);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }catch(MalformedURLException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                return null;
            }

            @Override
            protected void onPreExecute(){
                if (z==0) {
                    avi.show();
                    z++;
                }

                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void s){
                avi.hide();
                adapter.notifyDataSetChanged();
                super.onPostExecute(s);
            }

    }
}


