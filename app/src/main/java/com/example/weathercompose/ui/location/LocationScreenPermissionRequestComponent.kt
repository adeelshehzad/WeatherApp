package com.example.weathercompose.ui.location

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weathercompose.R

@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember { context as? Activity }
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    var showSettingsDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            val showRationaleDialog = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, permission
                )
            } ?: false

            if (showRationaleDialog) {
                showSettingsDialog = true
            } else {
                onPermissionDenied()
            }
        }
    }

    val hasPermission = ContextCompat.checkSelfPermission(
        context, permission
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(permission)
        } else {
            onPermissionGranted()
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.location_permission_rationale_dialog_title)) },
            text = { Text(stringResource(R.string.location_permission_rationale_dialog_description)) },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    showSettingsDialog = false
                }) {
                    Text(stringResource(R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    onPermissionDenied()
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}