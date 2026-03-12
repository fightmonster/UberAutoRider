# 🚗 Uber Auto Rider

Android 应用，通过 Deep Link 和 UiAutomator 自动完成 Uber 打车流程。

## 功能特性

- ✅ 输入目的地地址
- ✅ 选择车型（UberX, UberXL, Black, Pool, Comfort）
- ✅ 通过 Deep Link 跳转 Uber App
- ✅ UiAutomator 自动化打车流程
- ✅ 支持自定义出发地和目的地

## 技术栈

- **Kotlin** - 主要编程语言
- **Jetpack Compose** - 现代 UI 框架
- **Material Design 3** - UI 设计
- **UiAutomator** - UI 自动化测试框架
- **Uber Deep Links** - Uber App 集成

## 项目结构

```
UberAutoRider/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fightmonster/uberautorider/
│   │   │   │   ├── MainActivity.kt          # 主界面
│   │   │   │   └── ui/theme/                 # 主题配置
│   │   │   ├── res/                          # 资源文件
│   │   │   └── AndroidManifest.xml
│   │   └── androidTest/                      # UiAutomator 测试
│   │       └── java/com/fightmonster/uberautorider/
│   │           └── UberAutomationTest.kt    # 自动化测试
│   └── build.gradle.kts
├── .github/workflows/build.yml               # GitHub Actions
└── README.md
```

## 使用方法

### 1. 编译 APK

```bash
# 克隆项目
git clone https://github.com/fightmonster/UberAutoRider.git
cd UberAutoRider

# 编译 Debug APK
./gradlew assembleDebug

# APK 位置
# app/build/outputs/apk/debug/app-debug.apk
```

### 2. 安装到设备

```bash
# 通过 ADB 安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. 运行自动化测试

```bash
# 运行 UiAutomator 自动化测试
adb shell am instrument -w -r \
  -e class com.fightmonster.uberautorider.UberAutomationTest \
  com.fightmonster.uberautorider/androidx.test.runner.AndroidJUnitRunner
```

## Uber Deep Link 格式

```
uber://?action=setPickup&pickup=myLocation&dropoff[formatted_address]=目的地地址&product_id=uberx
```

参数说明：
- `action`: 固定为 `setPickup`
- `pickup`: 出发地，`myLocation` 表示当前位置，或使用 `pickup[formatted_address]=地址`
- `dropoff[formatted_address]`: 目的地地址
- `product_id`: 车型 ID（uberx, uberxl, black, pool, comfort）

## UiAutomator 自动化流程

1. **启动 Uber App** - 通过 Intent 或 Deep Link
2. **等待地图加载** - 检测搜索框出现
3. **设置目的地** - 输入地址并选择搜索结果
4. **选择车型** - 点击对应的车型选项
5. **确认叫车** - 点击确认/Request 按钮

## 注意事项

⚠️ **重要提示**

1. **需要安装 Uber App** - 此应用需要 Uber App 才能正常工作
2. **权限要求** - 需要位置权限和网络权限
3. **自动化限制** - UiAutomator 测试需要通过 ADB 运行
4. **账户安全** - 请勿滥用自动化功能，遵守 Uber 服务条款

## 关于 Uber SDK

Uber 官方提供了 Rides SDK，但需要：
- Uber Developer 账户
- 注册应用并获取 Client ID
- OAuth 认证流程

本项目使用 Deep Link + UiAutomator 方案，无需 SDK 即可实现基本功能。

如果需要更完整的集成（如获取价格、行程状态等），建议使用官方 SDK：
- [Uber Rides SDK for Android](https://github.com/uber/rides-android-sdk)

## GitHub Actions 自动编译

每次推送到 main 分支都会自动编译 APK：

1. 进入 [Actions](../../actions) 页面
2. 选择最新的 workflow run
3. 在 Artifacts 中下载 APK

## 开发计划

- [ ] 添加位置搜索自动完成
- [ ] 支持收藏常用地址
- [ ] 添加行程历史记录
- [ ] 集成 Uber API（价格估算）
- [ ] 添加语音输入支持

## License

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
