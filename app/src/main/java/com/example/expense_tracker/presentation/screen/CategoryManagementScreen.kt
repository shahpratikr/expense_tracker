package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.viewmodel.CategoryViewModel

// R-1: Screen for creating, renaming, and deleting custom expense categories
@Composable
fun CategoryManagementScreen(
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    var newCategoryName by remember { mutableStateOf("") }
    var categoryToRename by remember { mutableStateOf<ExpenseCategory?>(null) }
    var renameText by remember { mutableStateOf("") }

    if (categoryUiState.error != null) {
        ErrorDialog(
            message = categoryUiState.error!!,
            onDismiss = { categoryViewModel.clearError() }
        )
    }

    // Rename dialog for custom categories
    if (categoryToRename != null) {
        AlertDialog(
            onDismissRequest = { categoryToRename = null },
            title = { Text("Rename Category") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("New Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    categoryToRename?.let { categoryViewModel.renameCategory(it, renameText) }
                    categoryToRename = null
                }) { Text("Rename") }
            },
            dismissButton = {
                TextButton(onClick = { categoryToRename = null }) { Text("Cancel") }
            }
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
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
                                // R-1: Rename and delete only available for custom categories
                                if (!category.isPredefined) {
                                    IconButton(onClick = {
                                        categoryToRename = category
                                        renameText = category.name
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Rename")
                                    }
                                    IconButton(onClick = {
                                        categoryViewModel.deleteCategory(category)
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
