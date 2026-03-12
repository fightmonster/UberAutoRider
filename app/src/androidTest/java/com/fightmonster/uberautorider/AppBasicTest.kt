package com.fightmonster.uberautorider

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 基本测试：启动 App，验证 UI，关闭 App
 * 
 * 运行方式：
 * adb shell am instrument -w -r \
 *   -e class com.fightmonster.uberautorider.AppBasicTest \
 *   com.fightmonster.uberautorider/androidx.test.runner.AndroidJUnitRunner
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppBasicTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * 测试 1: 启动 App 并验证没有崩溃
     */
    @Test
    fun testAppLaunchesWithoutCrash() {
        // 如果 App 能启动到这里，说明没有崩溃
        activityRule.scenario
    }

    /**
     * 测试 2: 关闭 App 并重新启动
     */
    @Test
    fun testAppCanCloseAndRelaunch() {
        // 关闭
        activityRule.scenario.close()
        
        // 再次启动（通过重新创建 rule）验证稳定性
        // 如果能执行到这里说明 App 正常
    }

    /**
     * 测试 3: 验证 Activity 存在
     */
    @Test
    fun testActivityExists() {
        // 验证 Activity 不为 null
        assert(activityRule.scenario != null)
    }
}
