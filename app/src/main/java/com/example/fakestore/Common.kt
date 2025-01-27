package com.example.fakestore

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SocialButtons(
    onClickGoogle: () -> Unit,
    onClickFacebook: () -> Unit,
    color: Color = Color.White
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.sign_in_with),
                fontSize = 16.sp,
                color = color,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            HorizontalDivider(Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SignInButton(name = R.string.Google, icon = R.drawable.ic_google, onClickGoogle)
            Spacer(Modifier.weight(1f))
            SignInButton(name = R.string.facebook, icon = R.drawable.ic_facebook, onClickFacebook)
        }
    }

}

@Composable
fun SignInButton(
    @StringRes name: Int,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    color: Color = Color.White
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(color = Color.White)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
            )
            Spacer(Modifier.width(10.dp))
            Text(text = stringResource(name), fontSize = 16.sp, color = Color.Gray)
        }

    }

}

@Composable
fun LoginOption(
    onClick: () -> Unit,
    @StringRes name: Int, @StringRes text: Int,
    colorText: Color = Color.White,
    colorName: Color = Color.White

) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(text),
            color = colorText,
            fontSize = 16.sp
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.size(120.dp),

            ) {
            TextButton(onClick = { onClick() }) {
                Text(
                    text = stringResource(name),
                    fontSize = 16.sp,
                    color = colorName
                )
            }
            HorizontalDivider(Modifier.width(50.dp))

        }
    }
}

@Composable
fun FakeStoreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        unfocusedContainerColor = Color.LightGray.copy(0.4f),
        focusedContainerColor = Color.White,

        )

) {
    Column {

        label?.let {
            Row {
                Spacer(Modifier.size(8.dp))
                it()
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(
                fontWeight = FontWeight.SemiBold
            ),
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )

    }


}
