package com.example.fakestore.mainapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fakestore.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakeStoreTopAppBar(
    navController: NavController
) {
    TopAppBar( // Adjust height of the TopAppBar
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFDF99F0).copy(.4f)),
        navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(25.dp),
                    tint = Color.Black
                    ) // Smaller icon
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(30.dp)  // Smaller icon size
                )
                Spacer(modifier = Modifier.width(4.dp))  // Reduced spacing between icon and text
                Text(
                    text = "Stylish",
                    color = Color(0xFF4392F9),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)  // Reduced text size
                )
            }
        }
    )
}
