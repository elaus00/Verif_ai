package mp.verif_ai.presentation.screens.settings.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.theme.VerifAiColor


@Composable
fun PaymentMethodsScreen(
    navController: NavHostController,
    onAddMethod:()->Unit
) {
    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController = navController)
        }
    ){paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)  // Scaffold의 padding 적용

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = Color(0xFFFFFFFF)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Child views.
            }
            Column(
                modifier = Modifier

                    .fillMaxSize()
                    .background(color = Color(0xFFFFFFFF))
                    .padding(start = 25.dp, top = 10.dp, end = 25.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(//header
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(start = 6.dp, end = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Payment",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold),
                        color = VerifAiColor.TextPrimary.copy(alpha = 0.64f),
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                Column(//profile
                    modifier = Modifier
                        .width(340.dp)
                        .height(110.dp)
                        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Points",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2A5AB3),
                            modifier=Modifier
                                .fillMaxWidth()
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.dp)
                                .padding(start = 8.dp, end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Modifier
                                .padding(0.dp)
                                .fillMaxWidth()
                                .height(0.9.dp)
                                .background(color = Color(0x1A000000))
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .padding(top = 2.dp, bottom = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "9500 Points",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF000000),
                                    modifier=Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Column(//application
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(497.dp)
                        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(311.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Charge Points",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2A5AB3),
                            modifier=Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.dp)
                                .padding(start = 8.dp, end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Modifier
                                .padding(0.dp)
                                .fillMaxWidth()
                                .height(0.9.dp)
                                .background(color = Color(0x1A000000))

                        }
                        PointsItem(
                            pointsText = "100",
                            priceText = "1000",
                            onBuyClick = onAddMethod
                        )
                        PointsItem(
                            pointsText = "300",
                            priceText = "2700",
                            onBuyClick = onAddMethod
                        )
                        PointsItem(
                            pointsText = "500",
                            priceText = "4500",
                            onBuyClick = onAddMethod
                        )
                        PointsItem(
                            pointsText = "1000",
                            priceText = "9000",
                            onBuyClick = onAddMethod
                        )
                    }
                }
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
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PaymentMethodScreenPreview() {
    PaymentMethodsScreen(navController = NavHostController(LocalContext.current),onAddMethod = {} )
}
