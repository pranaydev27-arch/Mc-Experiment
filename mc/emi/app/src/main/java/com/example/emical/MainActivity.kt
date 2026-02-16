package com.example.emical

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emical.ui.theme.EmiCalTheme
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmiCalTheme {
                EMICalculatorScreen()
            }
        }
    }
}

@Composable
fun EMICalculatorScreen() {
    // State variables
    var principal by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }
    var emiResult by remember { mutableStateOf("") }
    var totalInterest by remember { mutableStateOf("") }

    // Error states
    var principalError by remember { mutableStateOf<String?>(null) }
    var interestError by remember { mutableStateOf<String?>(null) }
    var yearsError by remember { mutableStateOf<String?>(null) }

    // Main layout with wallpaper background
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Your personal wallpaper - made less visible with lower alpha
        Image(
            painter = painterResource(id = R.drawable.wallpaperw),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f // Makes wallpaper only 30% visible
        )

        // Semi-transparent overlay for even better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.2f)) // Light overlay to help with text visibility
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title - Changed to BLACK
            Text(
                text = "EMI CALCULATOR",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black, // Changed to black
                modifier = Modifier.padding(top = 40.dp, bottom = 30.dp),
                textAlign = TextAlign.Center
            )

            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.85f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Principal Amount - BLACK text
                    Text(
                        text = "Principal Amount (₹)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = principal,
                        onValueChange = {
                            principal = it
                            principalError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter amount", color = Color.DarkGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = principalError != null,
                        supportingText = {
                            if (principalError != null) {
                                Text(text = principalError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Interest Rate - BLACK text
                    Text(
                        text = "Interest Rate (%)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = {
                            interestRate = it
                            interestError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter rate", color = Color.DarkGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = interestError != null,
                        supportingText = {
                            if (interestError != null) {
                                Text(text = interestError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Loan Period - BLACK text
                    Text(
                        text = "Loan Period (Years)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = years,
                        onValueChange = {
                            years = it
                            yearsError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter years", color = Color.DarkGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = yearsError != null,
                        supportingText = {
                            if (yearsError != null) {
                                Text(text = yearsError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Calculate Button
                        Button(
                            onClick = {
                                // Validate inputs
                                principalError = if (principal.isEmpty()) "Enter principal amount" else null
                                interestError = if (interestRate.isEmpty()) "Enter interest rate" else null
                                yearsError = if (years.isEmpty()) "Enter years" else null

                                if (principalError == null && interestError == null && yearsError == null) {
                                    calculateEMI(
                                        principal = principal.toDoubleOrNull() ?: 0.0,
                                        rate = interestRate.toDoubleOrNull() ?: 0.0,
                                        years = years.toDoubleOrNull() ?: 0.0,
                                        onResult = { emi, interest ->
                                            emiResult = emi
                                            totalInterest = interest
                                        }
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("CALCULATE", color = Color.White)
                        }

                        // Clear Button
                        Button(
                            onClick = {
                                principal = ""
                                interestRate = ""
                                years = ""
                                emiResult = ""
                                totalInterest = ""
                                principalError = null
                                interestError = null
                                yearsError = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFf44336)
                            )
                        ) {
                            Text("CLEAR", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results Card
            if (emiResult.isNotEmpty() && totalInterest.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "RESULTS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 15.dp),
                            textAlign = TextAlign.Center
                        )

                        // Monthly EMI - BLACK text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Monthly EMI:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "₹$emiResult",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color.Gray,
                            thickness = 1.dp
                        )

                        // Total Interest - BLACK text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Interest:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "₹$totalInterest",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFf44336)
                            )
                        }
                    }
                }
            }
        }
    }
}

// EMI Calculation Function
fun calculateEMI(principal: Double, rate: Double, years: Double, onResult: (String, String) -> Unit) {
    if (principal <= 0 || rate <= 0 || years <= 0) {
        onResult("Invalid input", "Invalid input")
        return
    }

    val monthlyRate = rate / 12 / 100
    val months = years * 12

    val compoundFactor = (1 + monthlyRate).pow(months)
    val emi = (principal * monthlyRate * compoundFactor) / (compoundFactor - 1)
    val totalAmount = emi * months
    val totalInterestAmount = totalAmount - principal

    onResult(
        String.format("%.2f", emi),
        String.format("%.2f", totalInterestAmount)
    )
}

@Preview(showBackground = true)
@Composable
fun EMICalculatorPreview() {
    EmiCalTheme {
        EMICalculatorScreen()
    }
}