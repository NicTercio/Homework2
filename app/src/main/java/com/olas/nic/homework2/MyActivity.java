package com.olas.nic.homework2;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class MyActivity extends Activity {
    final Context context = this;
    ArrayList<String> tagsArray;
    ArrayAdapter<String> adapter;
    EditText search, tagBar;
    HashMap<String, String> map;
    InputMethodManager imm;
    //String searchText;
    //String tagText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        tagsArray = new ArrayList<String>();
        search = (EditText) findViewById(R.id.searchText);
        tagBar = (EditText) findViewById(R.id.tagText);
        final ListView list = (ListView) findViewById(R.id.listView);
        map = new HashMap<String, String>();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tagsArray);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.uta.edu/search/?q=" + map.get(tagsArray.get(position))));
                startActivity(i);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                final String[] options = {"Share","Edit","Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose an option")
                        .setItems(options, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                if(options[which].equals("Share"))
                                {
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.setType("text/plain");
                                    startActivity(Intent.createChooser(shareIntent, tagsArray.get(pos)));
                                }
                                else if(options[which].equals("Edit"))
                                {
                                    search.setText(map.get(tagsArray.get(pos)));
                                    tagBar.setText(tagsArray.get(pos));
                                    search.requestFocus();
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                                    map.remove(tagsArray.get(pos));
                                    tagsArray.remove(pos);
                                    adapter.notifyDataSetChanged();
                                    savePreferences();

                                }
                                else if(options[which].equals("Delete"))
                                {
                                    map.remove(tagsArray.get(pos));
                                    tagsArray.remove(pos);
                                    adapter.notifyDataSetChanged();
                                    savePreferences();
                                }
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();




                Log.v("long clicked", "pos: " + pos);

                return true;
            }
        });
        loadPreferences();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveButtonClick(View v)
    {
        String searchText = search.getText().toString();
        String tagText = tagBar.getText().toString();

        search.setText("");
        tagBar.setText("");

        map.put(tagText, searchText);
        tagsArray.add(tagText);
        adapter.notifyDataSetChanged();
        savePreferences();
    }
    private void savePreferences()
    {
        int i = 0;
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Size",map.size());

        for(String s : map.keySet())
        {
            i++;
            editor.putString(("key"+i) ,s);
            editor.commit();
            editor.putString(("val"+i), map.get(s));
            editor.commit();
        }

    }
    private void loadPreferences()
    {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int n = sharedPreferences.getInt("Size", 0);
        for(int i = 1; i <= n; i++)
        {
            String key = sharedPreferences.getString("key"+i, "");
            map.put(key, sharedPreferences.getString("val"+i, ""));
            tagsArray.add(key);
            adapter.notifyDataSetChanged();
        }

    }
}
