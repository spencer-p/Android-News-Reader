package spjpeter.ucsc.edu.newsreader;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Web requests
    private RequestQueue queue;

    // List view stuff
    private ArrayList<Element> list;
    private Adapter adapter;

    // Allows user to swipe down to refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up list
        list = new ArrayList<Element>();
        adapter = new Adapter(this, R.layout.list_item, list);
        ((ListView) findViewById(R.id.listView)).setAdapter(adapter);

        // Set up volley queue
        queue = Volley.newRequestQueue(this);

        // Set up swipe refresh layout (just gets news sites when swiped down)
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsSites();
            }
        });

        // Refresh the data to display
        getNewsSites();
    }

    // List element class for news sites in list
    class Element {
        String title, subtitle, url;

        Element(String title, String subtitle, String url) {
            this.title = title;
            this.subtitle = subtitle;
            this.url = url;
        }

        String getTitle() {
            return title;
        }

        String getSubtitle() {
            return subtitle;
        }

        String getUrl() {
            return url;
        }
    }

    // Adapter for the list view
    private class Adapter extends ArrayAdapter<Element> {
        Context context;
        int resource;

        private Adapter(Context context, int resource, List<Element> items) {
            super(context, resource, items);
            this.resource = resource;
            this.context = context;
        }

        @Override @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            Element element = getItem(position);

            // Make new view
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resource, newView, true);
            }
            // Recycle old view
            else {
                newView = (LinearLayout) convertView;
            }


            // Insert text (title, subtitle)
            ((TextView) newView.findViewById(R.id.title)).setText(element.getTitle());
            ((TextView) newView.findViewById(R.id.subtitle)).setText(element.getSubtitle());

            // Tag it with element object to store url
            newView.setTag(element);

            // Set behaviour on click
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toast(((Element)view.getTag()).getUrl());
                    browseToUrl(((Element)view.getTag()).getUrl());
                }
            });

            return newView;
        }
    }

    public void onClickRefresh(View view) {

        // Enable swipe refresher
        swipeRefreshLayout.setRefreshing(true);

        // Get news
        getNewsSites();
    }

    private void getNewsSites() {

        // Define url. Second one is for testing.
        String url = "https://luca-ucsc-teaching-backend.appspot.com/hw4/get_news_sites";
        //String url = "https://people.ucsc.edu/~spjpeter/news.html";

        // Create GET request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        processNewsSites(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast("Failed to get news sites");
                    }
                });

        // Send request
        queue.add(request);
    }

    private void processNewsSites(JSONObject sites) {
        try {

            // Get the array of sites
            JSONArray arr = sites.getJSONArray("news_sites");

            // Throw away old data
            list.clear();

            // Loop through sites
            for (int i = 0; i < arr.length(); i++) {

                // Get site
                JSONObject site = arr.getJSONObject(i);

                // Get data
                String title = site.getString("title");
                String subtitle = site.getString("subtitle");
                String url = site.getString("url");

                // If subtitle is empty, make it an empty string
                if (subtitle.equals("null")) {
                    subtitle = "";
                }

                // Add new element if has title and url
                if (!title.equals("null") && !url.equals("null")) {
                    list.add(new Element(title, subtitle, url));
                }
            }

            // Update list
            adapter.notifyDataSetChanged();

            // Disable refresher
            swipeRefreshLayout.setRefreshing(false);
        }
        catch (Exception e) {
            toast("Failed to process news sites");
        }
    }

    private void browseToUrl(String url) {
        Intent intent = new Intent(this, Browser.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
