package com.fightmonster.uberautorider

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fightmonster.uberautorider.ui.theme.UberAutoRiderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UberAutoRiderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UberAutoRiderApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UberAutoRiderApp() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    var pickupAddress by remember { mutableStateOf("") }
    var dropoffAddress by remember { mutableStateOf("") }
    var deepLink by remember { mutableStateOf("uber://") }
    var statusMessage by remember { mutableStateOf("准备就绪") }
    var isAutomationRunning by remember { mutableStateOf(false) }
    
    // 可用的 Uber 产品类型
    val productTypes = listOf(
        "UberX" to "uberx",
        "UberXL" to "uberxl", 
        "Uber Black" to "black",
        "Uber Pool" to "pool",
        "Uber Comfort" to "comfort"
    )
    var selectedProduct by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "🚗 Uber 自动打车助手",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "输入地址，自动完成 Uber 打车流程",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Divider()
        
        // 出发地输入
        OutlinedTextField(
            value = pickupAddress,
            onValueChange = { pickupAddress = it },
            label = { Text("出发地 (可选，留空使用当前位置)") },
            placeholder = { Text("例如: 北京市朝阳区三里屯") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // 目的地输入
        OutlinedTextField(
            value = dropoffAddress,
            onValueChange = { 
                dropoffAddress = it
                // 实时更新 deeplink
                deepLink = buildUberDeepLink(pickupAddress, it, productTypes[selectedProduct].second)
            },
            label = { Text("目的地 *") },
            placeholder = { Text("例如: 北京市海淀区中关村") },
            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = dropoffAddress.isBlank() && pickupAddress.isNotBlank()
        )
        
        // 产品类型选择
        Text(
            text = "选择车型:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            productTypes.forEachIndexed { index, (name, _) ->
                FilterChip(
                    selected = selectedProduct == index,
                    onClick = { 
                        selectedProduct = index
                        deepLink = buildUberDeepLink(pickupAddress, dropoffAddress, productTypes[index].second)
                    },
                    label = { Text(name) },
                    leadingIcon = if (selectedProduct == index) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
        
        Divider()
        
        // 生成的 Deep Link 预览
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "生成的 Deep Link:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = deepLink,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 打开 Uber App 按钮
            Button(
                onClick = {
                    if (dropoffAddress.isBlank()) {
                        statusMessage = "请输入目的地"
                        return@Button
                    }
                    
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                        context.startActivity(intent)
                        statusMessage = "已打开 Uber App"
                    } catch (e: Exception) {
                        statusMessage = "打开 Uber 失败: ${e.message}"
                        Toast.makeText(context, "请先安装 Uber App", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = dropoffAddress.isNotBlank() && !isAutomationRunning
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("打开 Uber")
            }
            
            // 自动打车按钮
            Button(
                onClick = {
                    if (dropoffAddress.isBlank()) {
                        statusMessage = "请输入目的地"
                        return@Button
                    }
                    
                    // 启动自动化服务
                    isAutomationRunning = true
                    statusMessage = "正在启动自动化流程..."
                    
                    // 首先打开 Uber
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
                        context.startActivity(intent)
                        
                        // 延迟启动 UiAutomator 服务
                        // 注意：实际自动化需要在 Instrumentation 测试环境中运行
                        statusMessage = "请运行自动化测试以完成打车流程"
                        
                    } catch (e: Exception) {
                        statusMessage = "启动失败: ${e.message}"
                    }
                    
                    isAutomationRunning = false
                },
                modifier = Modifier.weight(1f),
                enabled = dropoffAddress.isNotBlank() && !isAutomationRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("自动打车")
            }
        }
        
        // 状态显示
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isAutomationRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // 使用说明
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "📖 使用说明",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = """
1. 输入目的地地址
2. 选择想要的车型
3. 点击"自动打车"按钮
4. 等待自动化流程完成

注意：
• 需要安装 Uber App
• 自动化功能需要开启无障碍服务
• 首次使用请授予必要权限
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * 构建 Uber Deep Link
 * 
 * Uber 支持的 Deep Link 格式：
 * - uber://?action=setPickup&pickup=myLocation&dropoff[formatted_address]=地址
 * - uber://?client_id=YOUR_CLIENT_ID&action=setPickup&pickup[latitude]=lat&pickup[longitude]=lng...
 */
fun buildUberDeepLink(pickup: String, dropoff: String, productId: String): String {
    val builder = Uri.Builder()
        .scheme("uber")
        .authority("")
        .appendQueryParameter("action", "setPickup")
    
    // 设置出发地
    if (pickup.isBlank()) {
        builder.appendQueryParameter("pickup", "myLocation")
    } else {
        builder.appendQueryParameter("pickup[formatted_address]", pickup)
    }
    
    // 设置目的地
    if (dropoff.isNotBlank()) {
        builder.appendQueryParameter("dropoff[formatted_address]", dropoff)
    }
    
    // 设置产品类型
    if (productId.isNotBlank()) {
        builder.appendQueryParameter("product_id", productId)
    }
    
    return builder.build().toString()
}
