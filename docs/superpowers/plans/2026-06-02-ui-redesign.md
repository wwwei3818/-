# 日迹 App UI 重新设计实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将日迹 App 的 UI 从 Emoji 风格重新设计为参考图风格，提升设计感和专业度

**Architecture:** 使用 Phosphor Icons 线性图标库替换所有 Emoji，更新颜色系统为黑白为主+低饱和点缀，优化卡片设计为大圆角风格

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Phosphor Icons

---

## Task 1: 更新颜色系统

**Files:**
- Modify: `app/src/main/java/com/example/riji/ui/theme/Color.kt`

- [ ] **Step 1: 更新 Color.kt 中的颜色值**

```kotlin
package com.example.riji.ui.theme

import androidx.compose.ui.graphics.Color

// Light Mode - 黑白为主 + 低饱和点缀
val LightBackground = Color(0xFFF5F5F0)
val LightCardBackground = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1A1A1A)
val LightOnCard = Color(0xFF666666)
val LightDivider = Color(0xFFE0E0E0)
val LightSurfaceVariant = Color(0xFFF0F0EB)

// Dark Mode
val DarkBackground = Color(0xFF121212)
val DarkCardBackground = Color(0xFF1E1E1E)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnCard = Color(0xFF999999)
val DarkDivider = Color(0xFF333333)
val DarkSurfaceVariant = Color(0xFF222222)

// Macaron Accent Colors (低饱和)
val PinkAccent = Color(0xFFFFB5B5)
val BlueAccent = Color(0xFFB5D8FF)
val GreenAccent = Color(0xFFB5FFD8)
val YellowAccent = Color(0xFFFFE5B5)
val PurpleAccent = Color(0xFFD8B5FF)
val OrangeAccent = Color(0xFFFFD8B5)

// Functional Colors
val SuccessColor = Color(0xFF4CAF50)
val WarningColor = Color(0xFFFF9800)
val ErrorColor = Color(0xFFF44336)
val InfoColor = Color(0xFF2196F3)

// Category Colors
val LifeColor = Color(0xFFFFB5B5)
val WorkColor = Color(0xFFB5D8FF)
val CoupleColor = Color(0xFFFFD8B5)
val TravelColor = Color(0xFFB5FFD8)
val StudyColor = Color(0xFFD8B5FF)
```

- [ ] **Step 2: 运行构建验证颜色更新**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交颜色更新**

```bash
git add app/src/main/java/com/example/riji/ui/theme/Color.kt
git commit -m "feat: update color system to black-white + macaron accent"
```

---

## Task 2: 创建图标系统

**Files:**
- Create: `app/src/main/java/com/example/riji/ui/components/AppIcon.kt`
- Create: `app/src/main/java/com/example/riji/ui/components/IconSet.kt`

- [ ] **Step 1: 创建 AppIcon composable**

```kotlin
package com.example.riji.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppIcon(
    icon: ImageVector,
    size: Dp = 24.dp,
    containerSize: Dp = 40.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
) {
    Box(
        modifier = Modifier
            .size(containerSize)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(size),
            tint = tint
        )
    }
}
```

- [ ] **Step 2: 创建 IconSet 定义图标常量**

```kotlin
package com.example.riji.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconSet {
    // 时间类
    val Birthday = Icons.Default.Cake
    val Anniversary = Icons.Default.EmojiEvents
    val Deadline = Icons.Default.Timer
    val Calendar = Icons.Default.CalendarToday
    val Countdown = Icons.Default.HourglassBottom

    // 运动类
    val Run = Icons.Default.DirectionsRun
    val Walk = Icons.Default.DirectionsWalk
    val Fitness = Icons.Default.FitnessCenter
    val Bike = Icons.Default.DirectionsBike
    val Swim = Icons.Default.Pool
    val Yoga = Icons.Default.SelfImprovement

    // 生活类
    val TV = Icons.Default.Tv
    val Clean = Icons.Default.CleaningServices
    val Water = Icons.Default.WaterDrop
    val Food = Icons.Default.Restaurant
    val Book = Icons.Default.MenuBook
    val Music = Icons.Default.MusicNote

    // 资产类
    val Phone = Icons.Default.Phone
    val Computer = Icons.Default.Computer
    val Headphones = Icons.Default.Headphones
    val Home = Icons.Default.Home
    val Clothes = Icons.Default.Checkroom
    val Package = Icons.Default.Inventory2

    // 状态类
    val Check = Icons.Default.Check
    val Close = Icons.Default.Close
    val Add = Icons.Default.Add
    val Delete = Icons.Default.Delete
    val Settings = Icons.Default.Settings
    val Search = Icons.Default.Search

    // 其他
    val Diary = Icons.Default.Edit
    val Plan = Icons.Default.Assignment
    val Asset = Icons.Default.AccountBalance
    val Subscription = Icons.Default.Subscriptions
    val CheckIn = Icons.Default.CheckCircle
}
```

- [ ] **Step 3: 运行构建验证图标系统**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 提交图标系统**

```bash
git add app/src/main/java/com/example/riji/ui/components/AppIcon.kt
git add app/src/main/java/com/example/riji/ui/components/IconSet.kt
git commit -m "feat: add AppIcon component and IconSet with Material icons"
```

---

## Task 3: 更新 ItemCard 组件

**Files:**
- Modify: `app/src/main/java/com/example/riji/ui/components/ItemCard.kt`

- [ ] **Step 1: 更新 ItemCard 使用 ImageVector**

```kotlin
package com.example.riji.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.riji.ui.theme.AppShapes

@Composable
fun ItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    extraInfo: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.surface
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "card_scale")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = AppShapes.Card,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Big number/text
            Text(
                text = subtitle,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row with extra info and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (extraInfo != null) {
                    Text(
                        text = extraInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                AppIcon(
                    icon = icon,
                    size = 20.dp,
                    containerSize = 36.dp,
                    backgroundColor = accentColor.copy(alpha = 0.1f)
                )
            }
        }
    }
}
```

- [ ] **Step 2: 运行构建验证 ItemCard 更新**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交 ItemCard 更新**

```bash
git add app/src/main/java/com/example/riji/ui/components/ItemCard.kt
git commit -m "feat: update ItemCard to use ImageVector icons"
```

---

## Task 4: 更新 HomeScreen 使用新图标

**Files:**
- Modify: `app/src/main/java/com/example/riji/ui/screens/home/HomeScreen.kt`

- [ ] **Step 1: 更新 HomeScreen 使用 IconSet**

```kotlin
// 在 HomeScreen.kt 的 allItems 构建中，将 emoji 替换为 IconSet
// 例如：
// 之前: icon = "🎂"
// 之后: icon = IconSet.Birthday

// 需要修改的地方：
// 1. 导入 IconSet
// 2. 修改所有 HomeItem 的 icon 字段类型为 ImageVector
// 3. 修改 ItemCard 的 icon 参数
```

- [ ] **Step 2: 运行构建验证 HomeScreen 更新**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交 HomeScreen 更新**

```bash
git add app/src/main/java/com/example/riji/ui/screens/home/HomeScreen.kt
git commit -m "feat: update HomeScreen to use Material icons"
```

---

## Task 5: 更新 AddItemScreen 使用新图标

**Files:**
- Modify: `app/src/main/java/com/example/riji/ui/screens/add/AddItemScreen.kt`

- [ ] **Step 1: 更新 AddItemScreen 使用 IconSet**

```kotlin
// 将 AddMenuItem 的 icon 字段类型从 String 改为 ImageVector
// 更新所有菜单项使用 IconSet 中的图标
```

- [ ] **Step 2: 运行构建验证 AddItemScreen 更新**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交 AddItemScreen 更新**

```bash
git add app/src/main/java/com/example/riji/ui/screens/add/AddItemScreen.kt
git commit -m "feat: update AddItemScreen to use Material icons"
```

---

## Task 6: 更新其他页面使用新图标

**Files:**
- Modify: `app/src/main/java/com/example/riji/ui/screens/anniversary/AddAnniversaryScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/asset/AddAssetScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/subscription/AddSubscriptionScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/plan/AddPlanScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/checkin/AddCheckInScreen.kt`

- [ ] **Step 1: 更新所有 Add*Screen 使用 IconSet**

```kotlin
// 将每个页面的图标选择器中的 emoji 替换为 IconSet 中的图标
// 更新 FilterChip 显示 Material Icon 而不是 emoji
```

- [ ] **Step 2: 运行构建验证所有页面更新**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交所有页面更新**

```bash
git add app/src/main/java/com/example/riji/ui/screens/
git commit -m "feat: update all screens to use Material icons"
```

---

## Task 7: 更新详情页面使用新图标

**Files:**
- Modify: `app/src/main/java/com/example/riji/ui/screens/anniversary/AnniversaryDetailScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/asset/AssetScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/subscription/SubscriptionScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/plan/PlanScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/plan/PlanDetailScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/checkin/CheckInScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/diary/DiaryScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/diary/DiaryDetailScreen.kt`
- Modify: `app/src/main/java/com/example/riji/ui/screens/settings/SettingsScreen.kt`

- [ ] **Step 1: 更新所有详情页面使用 IconSet**

```kotlin
// 将每个页面中的 emoji 文本替换为 Material Icon
// 例如：
// 之前: Text("📊 数据统计")
// 之后: Icon(IconSet.BarChart, contentDescription = null) + Text("数据统计")
```

- [ ] **Step 2: 运行构建验证所有详情页面更新**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: 提交所有详情页面更新**

```bash
git add app/src/main/java/com/example/riji/ui/screens/
git commit -m "feat: update all detail screens to use Material icons"
```

---

## Task 8: 最终构建和测试

**Files:**
- None (verification only)

- [ ] **Step 1: 运行完整构建**

Run: `./gradlew clean assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 验证所有页面图标更新**

检查以下页面是否使用新的 Material Icon：
- [ ] HomeScreen
- [ ] AddItemScreen
- [ ] AddAnniversaryScreen
- [ ] AddAssetScreen
- [ ] AddSubscriptionScreen
- [ ] AddPlanScreen
- [ ] AddCheckInScreen
- [ ] AnniversaryDetailScreen
- [ ] AssetScreen
- [ ] SubscriptionScreen
- [ ] PlanScreen
- [ ] PlanDetailScreen
- [ ] CheckInScreen
- [ ] DiaryScreen
- [ ] DiaryDetailScreen
- [ ] SettingsScreen

- [ ] **Step 3: 提交最终版本**

```bash
git add -A
git commit -m "feat: complete UI redesign with Material icons"
```

---

## 完成

所有任务完成后，App 将：
1. 使用 Material Icons 替代所有 Emoji
2. 颜色系统更新为黑白为主+低饱和点缀
3. 卡片设计优化为大圆角风格
4. 整体设计感和专业度提升
