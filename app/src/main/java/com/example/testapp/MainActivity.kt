package com.example.testapp

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.request.transition.TransitionFactory
import com.example.testapp.networking.CatFlow
import com.example.testapp.networking.RetrofitBuilder
import com.example.testapp.ui.theme.TestAppTheme
import com.example.testapp.viewmodels.CatViewModel

class MainActivity : ComponentActivity() {

    /***
     * This app shows you cats!
     *
     * You can click one button to get a random cat or another to find random cats by a pre-existing list of tags
     * This is a pretty barebones app, just made to shown a basic kotlin app with a few modern libraries
     * It was a desired feature to include Room functionality in this app, being able to save cats locally but time constraints
     * put a stop to that. Luckily with the viewmodel setup, this should be relatively easy to implement.
     *
     * Since there is only 1 model to store and the DB is not expected to change anytime soon, setup and integration
     * would maybe be a couple hours more of work to add in the ability to save and load specific cats
     */

    private lateinit var viewModel: CatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()

        setContent {
            TestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadCats(viewModel = viewModel)
                }
            }
        }
    }

    //Setup viewmodel (This should be a factory) and listener for status
    private fun setUpViewModel(){
        viewModel = CatViewModel(CatFlow(RetrofitBuilder.catService))
        //Better error handling could be done, this is a quick fix to explain why
        //clicking some tags does not do anything
        viewModel.getCatStatus().observe(this, Observer {
            if(it.contains("404")){
                Toast.makeText(this, "No cats could be found", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

//Main compose method
@Composable
fun LoadCats(viewModel: CatViewModel, description: String = "It's a cat!") {
    //Hold the bitmap and tags as a state to react when they update
    val mBitmap = viewModel.getCatBitmap().observeAsState()
    val mTags = viewModel.getCatTags().observeAsState()

    //State control variables for changing UI
    var tagsHidden by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("Search by tags") }

    LaunchedEffect(key1 = Unit){
        viewModel.getCat()
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = Color.White))
    {
        //Loads image only if value is present
        //TODO Error handling
        mBitmap.value?.let {
            CatImage(image = it, description = description)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ){
            Button(colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.soft_green)),
                modifier = Modifier.shadow(elevation = 5.dp, shape = CircleShape, clip = false),
                onClick = {
                    viewModel.getCat()
                }) {
                //Strings should be extracted to a strings.xml file
                Text(text = "Get another cat", color = Color.White)
            }
            //Tags button shows and hides the tag list on click and changes text to match
            Button(colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.soft_pink)),
                modifier = Modifier.shadow(elevation = 5.dp, shape = CircleShape, clip = false),
                onClick = {
                    viewModel.getTags()
                    tagsHidden = !tagsHidden
                    if(tagsHidden){
                        text = "Search by tags"
                    }
                    else{
                        text = "Hide tags"
                    }

                }) {
                Text(text = text, color = Color.White)
            }
        }
        //Show list of tags if UI should not be hiding it and if there are results to show
        //Both in terms of null check but if the list is empty as well
        mTags.value?.let { stringList ->
            if(stringList.isNotEmpty() && !tagsHidden){
                TagsList(viewModel = viewModel, data = stringList)
            }
        }
    }
}

//Uses Glide to load the bitmap into the UI
//I did want to have an animation transition between images, however the Glide library does not easily support that for compose at this time
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CatImage(image: Bitmap, description: String){
    GlideImage(
        model = image,
        contentDescription = description,
        alignment = Alignment.Center,
        transition = CrossFade,
        modifier = Modifier
            .padding(start = 15.dp, top = 25.dp, end = 15.dp, bottom = 25.dp)
            .fillMaxWidth()
    )
}

//Vertical grid for the tags. Tags have a background container, which is the clickable element to search by that tag
@Composable
fun TagsList(viewModel: CatViewModel, data: List<String>, modifier: Modifier = Modifier){
    //Top and bottom fade is not my code, reference fadingEdge (line 219)
    val topBottomFade = Brush.verticalGradient(0f to Color.Transparent, 0.3f to Color.Red, 0.7f to Color.Red, 1f to Color.Transparent)
    Box(modifier = Modifier.fadingEdge(topBottomFade)){
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(100.dp),
            verticalItemSpacing = 15.dp,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            content = {
                items(data) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 5.dp, shape = CircleShape, clip = false)
                        .background(color = colorResource(id = R.color.soft_blue), shape = CircleShape)
                        .clickable {
                            viewModel.getCatByTag(it)
                        })
                    {
                        Text(text = it,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    top = 15.dp,
                                    end = 10.dp,
                                    bottom = 15.dp
                                )
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, top = 0.dp, end = 15.dp, bottom = 0.dp)
        )

    }
}

//NOT ORIGINAL CODE
// Author: nhcodes
// Source:  https://stackoverflow.com/questions/66762472/how-to-add-fading-edge-effect-to-android-jetpack-compose-column-or-row
fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestAppTheme {

    }
}