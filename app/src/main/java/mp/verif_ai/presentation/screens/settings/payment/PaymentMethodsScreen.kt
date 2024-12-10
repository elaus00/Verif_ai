package mp.verif_ai.presentation.screens.settings.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import mp.verif_ai.presentation.viewmodel.PaymentViewModel


@Composable
fun PaymentMethodsScreen(
    navController: NavHostController,
    viewModel: PaymentViewModel = hiltViewModel(),
    onAddMethod: () -> Unit // 결제 수단 추가 콜백 추가
) {
    val userPoints by viewModel.userPoints

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues) // Scaffold의 padding 적용
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = Color(0xFFFFFFFF)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Payment",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = VerifAiColor.TextPrimary.copy(alpha = 0.64f),
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { onAddMethod() } // 결제 수단 추가 네비게이션
                )
            }

            // Points Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Points",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2A5AB3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$userPoints Points",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            // Point Charging Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Charge Points",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2A5AB3)
                )
                Spacer(modifier = Modifier.height(8.dp))

                PointsItem(
                    pointsText = "100",
                    priceText = "1000",
                    onBuyClick = { viewModel.updateUserPoints(100) }
                )
                PointsItem(
                    pointsText = "300",
                    priceText = "2700",
                    onBuyClick = { viewModel.updateUserPoints(300) }
                )
                PointsItem(
                    pointsText = "500",
                    priceText = "4500",
                    onBuyClick = { viewModel.updateUserPoints(500) }
                )
                PointsItem(
                    pointsText = "1000",
                    priceText = "9000",
                    onBuyClick = { viewModel.updateUserPoints(1000) }
                )
            }
        }
    }
}


@Composable
fun PointsItem(
    pointsText: String,
    priceText: String,
    onBuyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .width(300.dp)
                .height(34.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$pointsText points / $priceText won",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF000000),
                modifier = Modifier
                    .width(215.dp)
                    .height(30.dp)
            )
            Text(
                text = "Buy",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .clickable { onBuyClick() }
                    .padding(5.dp)
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PaymentMethodScreenPreview() {
//    PaymentMethodsScreen(navController = NavHostController(LocalContext.current),onAddMethod = {} )
//}
