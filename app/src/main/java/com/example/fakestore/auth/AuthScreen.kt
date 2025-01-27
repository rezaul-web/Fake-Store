package com.example.fakestore.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fakestore.utils.LoginOption
import com.example.fakestore.R
import com.example.fakestore.utils.SocialButtons
import com.example.fakestore.ui.theme.Orange


@Composable
fun AuthScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val imageSize = remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent, Color.Black
        ),
        startY = imageSize.value.height.toFloat() / 3
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Orange
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.skip))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 110.dp)
                .padding(16.dp)
        ) {

            Text(
                text = stringResource(R.string.welcome), color = Color.Black,
                fontSize = 50.sp,

                )
            Text(
                text = stringResource(R.string.fake_store), color = Orange,
                fontSize = 50.sp,

                )
            Text(
                text = stringResource(R.string.favorite_store), color = Color.LightGray,
                fontSize = 20.sp,

                )

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(34.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {


            SocialButtons(
                onClickFacebook = {}, onClickGoogle = {},
            )

            Button(
                onClick = {
                    navController.navigate("sign_up")
                }, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f)
                ),
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_with_email_phone),
                    color = Color.White,
                    fontSize = 20.sp,
                )
            }
            LoginOption(text = R.string.already_have_account, name = R.string.sign_in, onClick = {
                navController.navigate("log_in")
            })

        }

    }

}




