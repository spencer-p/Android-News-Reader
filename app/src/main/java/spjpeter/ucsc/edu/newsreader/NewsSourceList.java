package spjpeter.ucsc.edu.newsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spencer on 5/1/17.
 */

class NewsSourceList {
    static ArrayList<Element> list;
    static Adapter adapter;

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

    class Adapter extends ArrayAdapter<Element> {
        Context context;
        int resource;

        public Adapter(Context context, int resource, List<Element> items) {
            super(context, resource, items);
            this.resource = resource;
            this.context = context;
        }

        @Override @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            Element element = getItem(position);

            if (convertView == null) {
                newView = new LinearLayout(getContext());
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resource, newView, true);
            }
            else {
                newView = (LinearLayout) convertView;
            }

            ((TextView) newView.findViewById(R.id.title)).setText(element.getTitle());
            ((TextView) newView.findViewById(R.id.subtitle)).setText(element.getSubtitle());
            newView.setTag(element);

            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, ((Element)view.getTag()).getUrl(), Toast.LENGTH_SHORT).show();
                }
            });

            return newView;
        }
    }
}
