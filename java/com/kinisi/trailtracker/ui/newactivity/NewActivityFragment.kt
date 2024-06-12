package com.kinisi.trailtracker.ui.newactivity


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kinisi.trailtracker.databinding.FragmentDashboardBinding
import com.kinisi.trailtracker.models.DataModel
import com.kinisi.trailtracker.models.SearchModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_favorites.recycler_view
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NewActivityFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    lateinit var adapter: RecyclerAdapter
    lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        // parseJSON()
        searchButton.setOnClickListener {
            execQuery(editText.getText().toString())
        }
        /*editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                // Toast.makeText(requireActivity(), "Great success", Toast.LENGTH_SHORT).show()
                execQuery(editText.getText().toString())
                return@OnKeyListener true
            }
            false
        })*/
        editText.addTextChangedListener( object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                execQuery(editText.getText().toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        var tmpArr: ArrayList<SearchModel> = ArrayList()
        super.onViewCreated(itemView, savedInstanceState)
        recycler_view.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = RecyclerAdapter(tmpArr)

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun execQuery(userQuery: String): java.util.ArrayList<SearchModel>  {
        var tmpArr: java.util.ArrayList<SearchModel> = java.util.ArrayList()

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://kinisi-search-6f45a3.ent.us-central1.gcp.cloud.es.io/api/as/v1/engines/kinisi-search/search")
            // URL("https://eflask-kinisi.herokuapp.com/api/closest&n=5&45.9604792,-123.6870344")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.setRequestProperty(
                "Accept", "application/json"
            )
            httpsURLConnection.setRequestProperty(
                "Content", "application/json"
            )
            httpsURLConnection.setRequestProperty(
                "Authorization", "Bearer search-ngvhsxdu1hjd3rzb433px13e"
            )
            // The format of response we want to get from the server
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            val reqString = "{\"query\": \"$userQuery\"}"
            val wr = OutputStreamWriter(httpsURLConnection.getOutputStream())
            wr.write(reqString);
            wr.flush();

            println("URL : $url")
            // println("Response Code : $responseCode")

            // Check if the connection is successful
            val responseCode = httpsURLConnection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {
                    // println(response.toString())
                    // ret = response

                    val jsonObject = JSONTokener(response).nextValue() as JSONObject

                    // val jsonArray = jsonObject.getJSONObject(i)
                    // results
                    val results = jsonObject.getJSONArray("results")

                    for (i in 0 until results.length()) {

                        val iter = results.getJSONObject(i)

                        Log.i("results: ", results.toString())

                        val nameObj = iter.getJSONObject("tags_name")
                        val name = nameObj.getString("raw")
                        Log.i("name: ", name.toString())
                        val sacObj = iter.getJSONObject("tags_sac_scale")
                        val sacType = sacObj.getString("raw").replace("_", " ")
                        Log.i("Sac Scale: ", sacType)
                        val xCoordObj = iter.getJSONObject("geometry_coordinates_0_1")
                        val xCoord = xCoordObj.getString("raw")
                        val yCoordObj = iter.getJSONObject("geometry_coordinates_1_0")
                        val yCoord = yCoordObj.getString("raw")
                        val idObj = iter.getJSONObject("id")
                        val trailID = idObj.getString("raw")

                        val model = SearchModel(
                            trailID,
                            name,
                            sacType,
                            xCoord,
                            yCoord,
                        )

                        tmpArr.add(model)
                        adapter = RecyclerAdapter(tmpArr)
                        adapter!!.notifyDataSetChanged()
                        Log.d("model: ", model.toString())
                        Log.d("tmp arr: ", tmpArr.toString())

                    }

                    binding.recyclerView.adapter = adapter
                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
        return tmpArr
    }
  /*  @SuppressLint("NotifyDataSetChanged")
    fun parseJSON(): java.util.ArrayList<DataModel> {
        var tmpArr: java.util.ArrayList<DataModel> = java.util.ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            val url =
                URL("https://eflask-kinisi.herokuapp.com/api/closest&n=1&47.503900920958415,-122.04708518140542")
            // URL("https://eflask-kinisi.herokuapp.com/api/closest&n=5&45.9604792,-123.6870344")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.setRequestProperty(
                "Accept",
                "application/json"
            ) // The format of response we want to get from the server
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            // Check if the connection is successful
            val responseCode = httpsURLConnection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    val jsonObject = JSONTokener(response).nextValue() as JSONArray

                    for (i in 0 until jsonObject.length()) {
                        val jsonArray = jsonObject.getJSONObject(i)
                        // ID
                        val id = jsonArray.getString("id")
                        Log.i("ID: ", id)

                        val tags = jsonArray.getJSONObject("tags")
                        val name = tags.getString("name")
                        val type = tags.getString("sac_scale")
                        val dist = tags.getDouble("dist")

                        Log.i("Dist", dist.toString())
                        Log.i("Name ", name)
                        Log.i("Type ", type)

                        val model = DataModel(
                            id,
                            name,
                            type,
                            "%.2f".format(dist).toDouble().toString() + " mi"
                        )

                        tmpArr.add(model)
                        adapter = RecyclerAdapter(tmpArr)
                        adapter!!.notifyDataSetChanged()
                        Log.d("model: ", model.toString())
                        Log.d("tmp arr: ", tmpArr.toString())

                    }

                    binding.recyclerView.adapter = adapter
                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
        return tmpArr
    }*/
/*    fun parseJSON(): java.util.ArrayList<DataModel> {
        var tmpArr: java.util.ArrayList<DataModel> = java.util.ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            val url =
                  URL("https://eflask-kinisi.herokuapp.com/api/closest&n=15&47.503900920958415,-122.04708518140542")
                 // URL("https://eflask-kinisi.herokuapp.com/api/closest&n=5&45.9604792,-123.6870344")
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.setRequestProperty(
                "Accept",
                "application/json"
            ) // The format of response we want to get from the server
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            // Check if the connection is successful
            val responseCode = httpsURLConnection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    val jsonObject = JSONTokener(response).nextValue() as JSONArray

                    for (i in 0 until jsonObject.length()) {
                        val jsonArray = jsonObject.getJSONObject(i)
                        // ID
                        val id = jsonArray.getString("id")
                        Log.i("ID: ", id)

                        val tags = jsonArray.getJSONObject("tags")
                        val name = tags.getString("name")
                        val type = tags.getString("sac_scale")
                        val dist = tags.getDouble("dist")

                        Log.i("Dist", dist.toString())
                        Log.i("Name ", name)
                        Log.i("Type ", type)

                        val model = DataModel(
                            id,
                            name,
                            type,
                            "%.2f".format(dist).toDouble().toString() + " mi"
                        )

                        tmpArr.add(model)
                        adapter = RecyclerAdapter(tmpArr)
                        adapter!!.notifyDataSetChanged()
                        Log.d("model: ", model.toString())
                        Log.d("tmp arr: ", tmpArr.toString())

                    }

                    binding.recyclerView.adapter = adapter
                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
        return tmpArr
    }*/

}