package com.fightmonster.uberautorider

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 基本测试：启动 App，验证 UI，关闭 App
 */
@RunWith(AndroidJUnit4::class)
class AppBasicTest {

    /**
     * 测试 1: 启动 App 并验证主界面显示
     */
    @Test
    fun testLaunchApp() {
        // 启动 MainActivity
        val scenario = launchActivity<MainActivity>()
        
        // 验证标题显示
        Espresso.onView(ViewMatchers.withText("🚗 Uber 自动打车助手"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        
        // 验证目的地输入框显示
        Espresso.onView(ViewMatchers.withHint("出发地 (可选，留空使用当前位置)"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        
        // 验证目的地输入框显示
        Espresso.onView(ViewMatchers.withHint("例如: 北京市海淀区中关村"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        
        scenario.close()
    }

    /**
     * 测试 2: 验证按钮状态
     */
    @Test
    fun testButtonsInitialState() {
        val scenario = launchActivity<MainActivity>()
        
        // 验证打开 Uber 按钮初始状态（目的地为空时应该禁用）
        Espresso.onView(ViewMatchers.withText("打开 Uber"))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
        
        // 验证自动打车按钮初始状态
        Espresso.onView(ViewMatchers.withText("自动打车"))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
        
        scenario.close()
    }

    /**
     * 测试 3: 启动和关闭 App
     */
    @Test
    fun testLaunchAndCloseApp() {
        // 启动
        val scenario = launchActivity<MainActivity>()
        
        // 验证 App 正常显示
        Espresso.onView(ViewMatchers.withText("📖 使用说明"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        
        // 关闭
        scenario.close()
        
        // 再次启动确保没有崩溃
        val scenario2 = launchActivity<MainActivity>()
        Espresso.onView(ViewMatchers.withText("🚗 Uber 自动打车助手"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario2.close()
    }

    /**
     * 测试 4: 验证车型选择
     */
    @Test
    fun testCarTypeSelection() {
        val scenario = launchActivity<MainActivity>()
        
        // 验证车型选项显示
        Espresso.onView(ViewMatchers.withText("UberX"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        
        Espresso.onView(ViewMatchers.withText("UberXL"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        
        scenario.close()
    }
}
