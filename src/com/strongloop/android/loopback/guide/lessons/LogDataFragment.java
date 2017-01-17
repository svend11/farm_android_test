package com.strongloop.android.loopback.guide.lessons;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.guide.GuideApplication;
import com.strongloop.android.loopback.guide.R;
import com.strongloop.android.loopback.guide.util.HtmlFragment;

//import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Implementation for Lesson Two: Existing Data? No Problem.
 */
public class LogDataFragment extends HtmlFragment {

    /**
     * Unlike Lesson One, our CarModel class is based _entirely_ on an existing schema.
     * <p>
     * In this case, every field in Oracle that's defined as a NUMBER type becomes a Number,
     * and each field defined as a VARCHAR2 becomes a String.
     * <p>
     * When we load these models from Oracle, LoopBack uses these property setters and getters
     * to know what data we care about. If we left off `extras`, for example, LoopBack would
     * simply omit that field.
     */
    public static class LogDataModel extends Model {

        private int LogDataID;
        //    	private String make;
//    	private String model;
//    	private String image;
//    	private String carClass;
//    	private String color;
        private Date LogDate;
        int Hour;

        public int getHour() {
            return Hour;
        }

        public void setHour(int hour) {
            this.Hour = hour;
        }

        //		public String getMake() {
//			return make;
//		}
//		public void setMake(String make) {
//			this.make = make;
//		}
//		public String getModel() {
//			return model;
//		}
//		public void setModel(String model) {
//			this.model = model;
//		}
//		public String getImage() {
//			return image;
//		}
//		public void setImage(String image) {
//			this.image = image;
//		}
//		public String getCarClass() {
//			return carClass;
//		}
//		public void setCarClass(String carClass) {
//			this.carClass = carClass;
//		}
        public Date getLogDate() {
            return LogDate;
        }

        public void setLogDate(Date d) {
            this.LogDate = d;
        }
    }

    /**
     * Our custom ModelRepository subclass. See Lesson One for more information.
     */
    public static class LogDataRepository extends ModelRepository<LogDataModel> {
        public LogDataRepository() {
            super("LogData", LogDataModel.class);
        }
    }

    /**
     * Loads all Car models from the server. To make full use of this, return to your (running)
     * Sample Application and restart it with the DB environment variable set to "oracle".
     * For example, on most *nix flavors (including Mac OS X), that looks like:
     * <p>
     * 1. Stop the current server with Ctrl-C.
     * 2. DB=oracle slc run app
     * <p>
     * What does this do, you ask? Without that environment variable, the Sample Application uses
     * simple, in-memory storage for all models. With the environment variable, it uses a custom-made
     * Oracle adapter with a demo Oracle database we host for this purpose. If you have existing
     * data, it's that easy to pull into LoopBack. No need to leave it behind.
     * <p>
     * Advanced users: LoopBack supports multiple data sources simultaneously, albeit on a per-model
     * basis. In your next project, try connecting a schema-less model (e.g. our Note example)
     * to a Mongo data source, while connecting a legacy model (e.g. this Car example) to
     * an Oracle data source.
     */
    private void sendRequest() {
        // 1. Grab the shared RestAdapter instance.
        GuideApplication app = (GuideApplication) getActivity().getApplication();
        RestAdapter adapter = app.getLoopBackAdapter();

        // 2. Instantiate our CarRepository.See LessonOneView for further discussion.
        LogDataRepository repository = adapter.createRepository(LogDataRepository.class);

        // 3. Rather than instantiate a model directly like we did in Lesson One, we'll query
        //    the server for all Cars, filling out our ListView with the results. In this case,
        //    the Repository is really the workhorse; the Model is just a simple container.

        repository.findAll(new ModelRepository.FindAllCallback<LogDataFragment.LogDataModel>() {
            @Override
            public void onSuccess(List<LogDataModel> models) {
                list.setAdapter(new LogDataListAdapter(getActivity(), models));
            }

            @Override
            public void onError(Throwable t) {
                Log.e(getTag(), "Cannot save LogData  model.", t);
                showResult("Failed.");
            }
        });
    }

    private void showResult(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Basic ListAdapter implementation using our custom Model type.
     */
    private static class LogDataListAdapter extends ArrayAdapter<LogDataModel> {
        public LogDataListAdapter(Context context, List<LogDataModel> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        android.R.layout.simple_list_item_1, null);
            }

            LogDataModel model = getItem(position);
            if (model == null) return convertView;

            TextView textView = (TextView) convertView.findViewById(
                    android.R.id.text1);
            textView.setText(
//                    model.getModel() +
                    " - " + model.getHour());

            return convertView;
        }
    }

    //
    // GUI glue
    //
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRootView((ViewGroup) inflater.inflate(
                R.layout.fragment_lesson_two, container, false));

        list = (ListView) getRootView().findViewById(R.id.list);

        setHtmlText(R.id.content, R.string.lessonTwo_content);

        installButtonClickHandler();

        return getRootView();
    }

    private void installButtonClickHandler() {
        final Button button = (Button) getRootView().findViewById(R.id.sendRequest);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                sendRequest();
                sendGetRequest();
            }
        });
    }

    String TAG="LogDataFragment";
    JSONArray  jsonArr;
    void sendGetRequest()
    {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    GuideApplication app = (GuideApplication) getActivity().getApplication();
                    try {
                        jsonArr=  getJSONObjectFromURL(app.apiURL+"/LogData");

//                        for (JSONObject o : jsonArr) {
//                            Log.d(TAG, jsonArr[0]["DvcsID"]);
//                        }
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject row = jsonArr.getJSONObject(i);
//                            int id = row.getInt("id");
                            float data = (float) row.getDouble("LogData");
                            String dateStr = row.getString("LogTime");
//                            Date d;
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//                            try {
//                                 d = sdf.parse("dateStr");
//                            } catch (ParseException ex) {
////                                Logger.getLogger(Prime.class.getName()).log(Level.SEVERE, null, ex);
//                            }
                            Log.d(TAG, i+" : "+dateStr+" : "+data);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public static JSONArray getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

//        return new JSONObject(jsonString);
        return new JSONArray(jsonString);
    }
}
