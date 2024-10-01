package com.example.aleksandarsekulovski_multipaneshoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import java.io.Serializable
import com.example.aleksandarsekulovski_multipaneshoppingapp.ui.theme.AleksandarSekulovskiMultiPaneShoppingAppTheme

data class Product(val name: String, val price: String, val description: String) : Serializable

// ViewModel holding the list of products
class ShoppingViewModel : ViewModel() {
    val products = listOf(
        Product("Whole Milk", "$5", "This milk is decent."),
        Product("Fat Free Lactaid", "$5.50", "This milk is the best."),
        Product("Fat Free Fairlife", "$6", "This milk is pretty good.")
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AleksandarSekulovskiMultiPaneShoppingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ShoppingApp(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = viewModel()
) {
    val navController = rememberNavController()
    var selectedProduct by rememberSaveable { mutableStateOf<Product?>(null) }
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        android.content.res.Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(
                products = viewModel.products,
                selectedProduct = selectedProduct,
                onProductSelected = { selectedProduct = it },
                onUnselect = { selectedProduct = null },
                modifier = modifier
            )
        }

        else -> {
            // Portrait mode with navigation
            NavHost(
                navController = navController,
                startDestination = if (selectedProduct == null) "productList" else "productDetails",
                modifier = modifier
            ) {
                composable("productList") {
                    ProductList(
                        products = viewModel.products,
                        selectedProduct = selectedProduct,
                        onProductSelected = {
                            selectedProduct = it
                            navController.navigate("productDetails")
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                composable("productDetails") {
                    ProductDetails(
                        product = selectedProduct,
                        onBack = {
                            selectedProduct = null
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun LandscapeLayout(
    products: List<Product>,
    selectedProduct: Product?,
    onProductSelected: (Product) -> Unit,
    onUnselect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        ProductList(
            products = products,
            selectedProduct = selectedProduct,
            onProductSelected = onProductSelected,
            modifier = Modifier.weight(1f)
        )
        ProductDetails(
            product = selectedProduct,
            onBack = onUnselect,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    selectedProduct: Product?,
    onProductSelected: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(products) { product ->
            val isSelected = product == selectedProduct
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductSelected(product) }
                    .padding(16.dp)
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ProductDetails(
    product: Product?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (product != null) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Price: ${product.price}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Back")
                }
            }
        } else {
            Text(
                text = "Select a product to view details.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingAppPreview() {
    AleksandarSekulovskiMultiPaneShoppingAppTheme {
        ShoppingApp()
    }
}
