package com.example.testapp.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.networking.CatHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CatViewModel(private val catHelper: CatHelper) : ViewModel() {

    /**
     * Main viewmodel class
     *
     * Calls endpoints and receives the result through flow. As well as
     * Hosting LiveData to communicate states to the MainActivity
     *
     */

    //Cat Image LiveData
    private val _bitmap = MutableLiveData<Bitmap>()
    private val bitmap : LiveData<Bitmap>
        get() = _bitmap

    //Cat Tags Live Data
    private val _tags = MutableLiveData<List<String>>()
    private val tags : LiveData<List<String>>
        get() = _tags

    //Endpoint Status Live Data
    private val _status = MutableLiveData<String>()
    private val status : LiveData<String>
        get() = _status

    init{
        getCat()
    }

    //Getter functions
    fun getCatBitmap() = bitmap
    fun getCatTags() = tags
    fun getCatStatus() = status

    /**
     * The cataas API returns random cats as image response bodies rather than JSON objects
     * This proved to be a problem in implementation by simply using a Cat model as the random
     * endpoints would not return any JSON. I probably should have switched to a different API
     * but I guess I wanted to try this approach more.
     *
     * The viewmodel takes the byte array from the response body and converts it to a bitmap for
     * the main activity to load in.
     *
     * There is a single bitmap re-used for both random and tag based searches
     */
    fun getCat(){
        viewModelScope.launch{
            catHelper.getCat()
                .flowOn(Dispatchers.IO)
                .catch { e -> e.message?.let {
                    Log.e("Error:", it)
                    _status.value = it
                } }
                .collect{
                    val bytes : ByteArray = it.bytes()
                    _bitmap.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
        }
    }

    //Receive the tags from flow and pass them to the activity
    fun getTags(){
        viewModelScope.launch{
            catHelper.getCatTags()
                .flowOn(Dispatchers.IO)
                .catch { e -> e.message?.let {
                    Log.e("Error:", it)
                    _status.value = it
                } }
                .collect{
                    _tags.value = it
                }
        }
    }

    //Receive a cat by tag from flow and pass them to the activity
    fun getCatByTag(tag: String){
        viewModelScope.launch{
            catHelper.getCatByTag(tag)
                .flowOn(Dispatchers.IO)
                .catch { e -> e.message?.let {
                    Log.e("Error:", it)
                    _status.value = it
                } }
                .collect{
                    val bytes : ByteArray = it.bytes()
                    _bitmap.value = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
        }
    }
}