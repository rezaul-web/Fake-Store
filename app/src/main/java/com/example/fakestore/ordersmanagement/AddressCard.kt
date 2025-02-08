package com.example.fakestore.ordersmanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fakestore.model.UserAddress

@Composable
fun AddressCard(address: UserAddress, updateAddress:()->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),

        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Delivering to:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Address Line: ${address.addressLine}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("City: ${address.city}", style = MaterialTheme.typography.bodyMedium)
            Text("State: ${address.state}", style = MaterialTheme.typography.bodyMedium)
            Text("Postal Code: ${address.postalCode}", style = MaterialTheme.typography.bodyMedium)
            Text("Country: ${address.country}", style = MaterialTheme.typography.bodyMedium)

            OutlinedButton(onClick = {
                updateAddress()
            }) {
                Text(text = "Update Address")
            }
        }
    }
}