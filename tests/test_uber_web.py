"""
Uber Web 自动化测试
使用 Playwright 测试 Uber 网页版
"""
import pytest
from playwright.sync_api import sync_playwright, expect
import os
import re


class TestUberWeb:
    """Uber Web 自动化测试类"""

    @pytest.fixture(scope="class")
    def browser_context(self):
        """创建浏览器上下文"""
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            context = browser.new_context(
                viewport={'width': 1280, 'height': 720},
                user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            )
            page = context.new_page()
            yield page
            context.close()
            browser.close()

    def test_uber_website_opens(self, browser_context):
        """测试 Uber 网站能正常打开"""
        page = browser_context
        
        # 访问 Uber 官网
        page.goto("https://www.uber.com", timeout=60000)
        
        # 验证页面加载 - 使用 re.compile 创建正则表达式
        expect(page).to_have_title(re.compile("Uber", re.IGNORECASE))
        
        # 截图
        os.makedirs("screenshots", exist_ok=True)
        page.screenshot(path="screenshots/uber_homepage.png")

    def test_uber_login_page(self, browser_context):
        """测试登录页面"""
        page = browser_context
        
        # 访问登录页
        page.goto("https://auth.uber.com/login/", timeout=60000)
        
        # 验证登录元素存在
        expect(page).to_have_title(re.compile("Uber", re.IGNORECASE))
        
        # 截图
        page.screenshot(path="screenshots/uber_login.png")

    def test_uber_ride_page(self, browser_context):
        """测试叫车页面"""
        page = browser_context
        
        # 访问叫车页面
        page.goto("https://m.uber.com", timeout=60000)
        
        # 等待页面加载
        page.wait_for_load_state("networkidle", timeout=30000)
        
        # 截图
        page.screenshot(path="screenshots/uber_ride_page.png")

    def test_uber_deep_link_redirect(self, browser_context):
        """测试 Deep Link 重定向"""
        page = browser_context
        
        # 访问 Uber Deep Link 页面
        # 这个 URL 会重定向到 App 或 Web 版
        page.goto("https://m.uber.com/looking", timeout=60000)
        
        # 验证页面加载
        page.wait_for_load_state("networkidle", timeout=30000)
        
        # 截图
        page.screenshot(path="screenshots/uber_deep_link.png")


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
