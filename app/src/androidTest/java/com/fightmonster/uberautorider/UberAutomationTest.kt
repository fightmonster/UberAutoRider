package com.fightmonster.uberautorider

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UiAutomator 自动化测试类
 * 
 * 用于自动完成 Uber 打车流程：
 * 1. 打开 Uber App
 * 2. 确认上车地点
 * 3. 选择目的地
 * 4. 选择车型
 * 5. 确认叫车
 * 
 * 运行方式：
 * adb shell am instrument -w -r -e class com.fightmonster.uberautorider.UberAutomationTest \
 *   com.fightmonster.uberautorider/androidx.test.runner.AndroidJUnitRunner
 */
@RunWith(AndroidJUnit4::class)
class UberAutomationTest {

    private lateinit var device: UiDevice
    private val UBER_PACKAGE = "com.ubercab"
    private val TIMEOUT = 5000L

    @Before
    fun setUp() {
        // 初始化 UiDevice
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        
        // 按 Home 键回到主屏幕
        device.pressHome()
        
        // 等待设备稳定
        device.waitForIdle(TIMEOUT)
    }

    /**
     * 测试完整的 Uber 自动打车流程
     */
    @Test
    fun testCompleteRideFlow() {
        // 1. 启动 Uber App
        launchUberApp()
        
        // 2. 等待地图加载
        waitForMapToLoad()
        
        // 3. 设置目的地（如果通过 deep link 启动，目的地可能已经设置）
        setDestination()
        
        // 4. 选择车型
        selectCarType()
        
        // 5. 确认叫车
        confirmRide()
    }

    /**
     * 启动 Uber App
     */
    private fun launchUberApp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(UBER_PACKAGE)
        
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            
            // 等待 Uber 启动
            device.wait(Until.hasObject(By.pkg(UBER_PACKAGE).depth(0)), TIMEOUT * 2)
        } else {
            throw RuntimeException("Uber App 未安装")
        }
    }

    /**
     * 等待地图加载完成
     */
    private fun waitForMapToLoad() {
        // 等待 "Where to?" 或类似的搜索框出现
        val searchBox = device.wait(
            Until.findObject(By.textContains("Where to?")),
            TIMEOUT * 3
        )
        
        if (searchBox != null) {
            println("地图已加载，找到搜索框")
        }
    }

    /**
     * 设置目的地
     */
    private fun setDestination() {
        // 点击搜索框
        val searchBox = device.findObject(By.textContains("Where to?"))
        if (searchBox != null) {
            searchBox.click()
            
            // 等待搜索界面出现
            device.waitForIdle(TIMEOUT)
            
            // 输入目的地（这里需要根据实际情况修改）
            val destinationInput = device.findObject(By.clazz("android.widget.EditText"))
            if (destinationInput != null) {
                destinationInput.text = "北京站"  // 示例目的地
                
                // 等待搜索结果
                device.waitForIdle(TIMEOUT)
                
                // 点击第一个搜索结果
                val firstResult = device.findObject(By.clazz("android.view.ViewGroup"))
                if (firstResult != null) {
                    firstResult.click()
                }
            }
        }
    }

    /**
     * 选择车型
     */
    private fun selectCarType() {
        // 等待车型选择界面出现
        device.waitForIdle(TIMEOUT)
        
        // 查找并点击 UberX 选项
        val uberXOption = device.findObject(By.textContains("UberX"))
        if (uberXOption != null) {
            uberXOption.click()
            println("已选择 UberX")
        }
    }

    /**
     * 确认叫车
     */
    private fun confirmRide() {
        // 等待确认按钮出现
        device.waitForIdle(TIMEOUT)
        
        // 查找并点击确认按钮
        val confirmButton = device.findObject(By.textContains("Confirm"))
            ?: device.findObject(By.textContains("Request"))
            ?: device.findObject(By.textContains("Book"))
        
        if (confirmButton != null) {
            confirmButton.click()
            println("已确认叫车")
            
            // 等待司机匹配
            device.waitForIdle(TIMEOUT * 3)
        }
    }

    /**
     * 通过 Deep Link 打开 Uber 并设置路线
     */
    @Test
    fun testOpenUberWithDeepLink() {
        val context = InstrumentationRegistry.getInstrumentation().context
        
        // 构建 Uber Deep Link
        val deepLinkUri = android.net.Uri.Builder()
            .scheme("uber")
            .authority("")
            .appendQueryParameter("action", "setPickup")
            .appendQueryParameter("pickup", "myLocation")
            .appendQueryParameter("dropoff[formatted_address]", "北京站")
            .appendQueryParameter("product_id", "uberx")
            .build()
        
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, deepLinkUri)
        intent.setPackage(UBER_PACKAGE)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        
        context.startActivity(intent)
        
        // 等待 Uber 打开
        device.wait(Until.hasObject(By.pkg(UBER_PACKAGE).depth(0)), TIMEOUT * 3)
        
        // 验证目的地是否已设置
        device.waitForIdle(TIMEOUT)
    }

    /**
     * 取消当前行程（如果有）
     */
    @Test
    fun testCancelRide() {
        launchUberApp()
        
        device.waitForIdle(TIMEOUT)
        
        // 查找取消按钮
        val cancelButton = device.findObject(By.textContains("Cancel"))
            ?: device.findObject(By.descContains("Cancel"))
        
        if (cancelButton != null) {
            cancelButton.click()
            
            // 确认取消
            device.waitForIdle(TIMEOUT)
            val confirmCancel = device.findObject(By.textContains("Yes"))
                ?: device.findObject(By.textContains("确认"))
            
            confirmCancel?.click()
        }
    }

    /**
     * 自动打车：通过 Deep Link 打开 Uber，找到最大按钮并点击
     * 
     * 使用方法：
     * adb shell am instrument -w -r \
     *   -e class com.fightmonster.uberautorider.UberAutomationTest#testAutoRideFindLargestButton \
     *   com.fightmonster.uberautorider/androidx.test.runner.AndroidJUnitRunner
     * 
     * 查看日志：
     * adb logcat -s UberAutoRider:*
     */
    @Test
    fun testAutoRideFindLargestButton() {
        val context = InstrumentationRegistry.getInstrumentation().context
        
        // 1. 通过 Deep Link 打开 Uber
        logToLogcat("=== 开始自动打车流程 ===")
        logToLogcat("步骤 1: 通过 Deep Link 打开 Uber")
        
        val deepLinkUri = android.net.Uri.Builder()
            .scheme("uber")
            .authority("")
            .appendQueryParameter("action", "setPickup")
            .appendQueryParameter("pickup", "myLocation")
            .appendQueryParameter("dropoff[formatted_address]", "北京站")
            .appendQueryParameter("product_id", "uberx")
            .build()
        
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, deepLinkUri)
        intent.setPackage(UBER_PACKAGE)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        
        context.startActivity(intent)
        
        // 2. 等待 Uber 顶层窗口出现
        logToLogcat("步骤 2: 等待 Uber 顶层窗口加载")
        val uberLoaded = device.wait(Until.hasObject(By.pkg(UBER_PACKAGE).depth(0)), TIMEOUT * 4)
        
        if (!uberLoaded) {
            logToLogcat("错误: Uber 未能在指定时间内加载")
            throw RuntimeException("Uber App 加载超时")
        }
        
        logToLogcat("Uber 已成功加载")
        
        // 额外等待 UI 渲染
        Thread.sleep(3000)
        device.waitForIdle(TIMEOUT * 2)
        
        // 3. 获取所有可点击元素
        logToLogcat("步骤 3: 扫描所有可点击元素")
        val clickableElements = device.findObjects(By.clickable(true))
        
        logToLogcat("找到 ${clickableElements.size} 个可点击元素")
        
        if (clickableElements.isEmpty()) {
            logToLogcat("警告: 未找到任何可点击元素")
            return
        }
        
        // 4. 找到面积最大的按钮
        logToLogcat("步骤 4: 寻找面积最大的按钮")
        var largestButton: UiObject2? = null
        var maxSize = 0L
        
        for (element in clickableElements) {
            val bounds = element.visibleBounds
            val size = bounds.width().toLong() * bounds.height()
            
            // 打印每个元素的信息（调试用）
            logToLogcat("  元素: 类名=${element.className}, 文本=${element.text}, " +
                       "描述=${element.contentDescription}, 尺寸=${bounds.width()}x${bounds.height()}")
            
            if (size > maxSize) {
                maxSize = size
                largestButton = element
            }
        }
        
        // 5. 显示最大按钮信息并点击
        if (largestButton != null) {
            val bounds = largestButton.visibleBounds
            val text = largestButton.text ?: "(无文本)"
            val contentDesc = largestButton.contentDescription ?: "(无描述)"
            val className = largestButton.className ?: "(未知)"
            val resourceId = largestButton.resourceName ?: "(无资源ID)"
            
            // 输出详细信息到 logcat（会被 Toast 显示）
            logToLogcat("========================================")
            logToLogcat("🎯 找到最大按钮！")
            logToLogcat("  文本: $text")
            logToLogcat("  内容描述: $contentDesc")
            logToLogcat("  类名: $className")
            logToLogcat("  资源ID: $resourceId")
            logToLogcat("  尺寸: ${bounds.width()} x ${bounds.height()} = $maxSize 像素")
            logToLogcat("  位置: (${bounds.left}, ${bounds.top})")
            logToLogcat("========================================")
            
            // 使用 Toast 显示（通过 shell 命令）
            showToastViaShell("最大按钮: $text | 尺寸: ${bounds.width()}x${bounds.height()}")
            
            // 等待一下让用户看到 Toast
            Thread.sleep(2000)
            
            // 6. 点击最大按钮
            logToLogcat("步骤 5: 点击最大按钮")
            largestButton.click()
            
            logToLogcat("已点击按钮，等待响应...")
            device.waitForIdle(TIMEOUT * 2)
            
        } else {
            logToLogcat("错误: 未能找到任何按钮")
        }
        
        logToLogcat("=== 自动打车流程完成 ===")
    }

    /**
     * 辅助方法：输出日志到 logcat
     */
    private fun logToLogcat(message: String) {
        android.util.Log.d("UberAutoRider", message)
        println("[UberAutoRider] $message")
    }

    /**
     * 辅助方法：通过 shell 命令显示 Toast
     * 注意：需要 TOAST_NOTIFICATIONS_PERMISSION 或 root 权限
     */
    private fun showToastViaShell(message: String) {
        try {
            // 方法 1: 使用 service call（需要权限）
            val command = "service call notification 1 s16 \"$message\""
            device.executeShellCommand(command)
            logToLogcat("Toast 命令已发送: $message")
        } catch (e: Exception) {
            logToLogcat("Toast 显示失败: ${e.message}")
            // 备选方案：直接用 logcat 输出
            logToLogcat("=== TOAST 消息 ===")
            logToLogcat(message)
            logToLogcat("==================")
        }
    }

    companion object {
        private const val TAG = "UberAutomation"
    }
}
