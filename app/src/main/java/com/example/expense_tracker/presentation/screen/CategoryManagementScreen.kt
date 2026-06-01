package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.viewmodel.CategoryViewModel

@Composable
fun CategoryManagementScreen(
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    var newCategoryName by remember { mutableStateOf("") }

    if (categoryUiState.error != null) {
        ErrorDialog(
            message = categoryUiState.error!!,
            onDismiss = { categoryViewModel.clearError() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Categories") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Button(
                onClick = {
                    if (newCategoryName.isNotBlank()) {
                        categoryViewModel.addCategory(newCategoryName)
                        newCategoryName = ""
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp)
            ) {
                Text("Add Category")
            }

            if (categoryUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (categoryUiState.categories.isEmpty()) {
                Text("No categories found", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(categoryUiState.categories) { category ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (category.isPredefined) {
                                    Text(
                                        text = "Predefined",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
