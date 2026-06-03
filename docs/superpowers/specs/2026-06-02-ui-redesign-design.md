# 日迹 App UI 重新设计规范

## 概述

将日迹 App 的 UI 从 Emoji 风格重新设计为参考图风格，提升设计感和专业度。

## 设计目标

- 参考图风格复刻
- 黑白为主色调 + 低饱和马卡龙色点缀
- 大圆角卡片设计
- 线性图标系统
- 无衬线字体（思源黑体）

## 1. 颜色系统

### 浅色模式
```
背景色:     #F5F5F0 (浅灰白)
卡片背景:   #FFFFFF (纯白)
主文字:     #1A1A1A (深黑)
副文字:     #666666 (中灰)
强调色-粉:  #FFB5B5 (樱花粉)
强调色-蓝:  #B5D8FF (天空蓝)
强调色-绿:  #B5FFD8 (薄荷绿)
强调色-黄:  #FFE5B5 (暖阳黄)
```

### 深色模式
```
背景色:     #121212 (纯黑)
卡片背景:   #1E1E1E (深灰)
主文字:     #FFFFFF (纯白)
副文字:     #999999 (浅灰)
强调色:     与浅色模式一致
```

## 2. 图标系统

### 图标库选择
使用 Phosphor Icons 线性图标库，风格特点：
- 简洁的线条
- 圆润的端点
- 统一的粗细（2dp 描边）
- 多种权重

### 图标实现
1. 在 `app/src/main/res/drawable/` 中添加 SVG 图标
2. 创建 `IconSet.kt` 定义图标常量
3. 创建 `AppIcon` composable 组件渲染图标

### 图标分类
- 时间类：生日、纪念日、截止日等
- 运动类：跑步、散步、健身等
- 生活类：看剧、打扫、喝水等
- 资产类：电子产品、家电等
- 状态类：完成、进行中、暂停等

## 3. 卡片设计

### 卡片规范
- 圆角：20dp
- 阴影：1-2dp
- 无边框
- 内边距：16dp

### 卡片布局
```
┌─────────────────────────────┐
│  标题文字                    │
│                             │
│  大号数字/文字               │
│                             │
│  辅助信息          [图标]   │
└─────────────────────────────┘
```

### 图标位置
- 卡片右下角
- 圆形背景（10% 透明度）
- 24dp 尺寸

## 4. 字体系统

### 字体选择
思源黑体（Noto Sans CJK SC）

### 字体层级
| 层级 | 字号 | 字重 | 用途 |
|-----|------|------|------|
| H1 | 28sp | Bold | 页面标题 |
| H2 | 22sp | Medium | 模块标题 |
| H3 | 18sp | Medium | 卡片标题 |
| Body | 16sp | Regular | 正文内容 |
| Caption | 14sp | Regular | 辅助说明 |
| Small | 12sp | Regular | 标签、时间 |

## 5. 组件设计

### ItemCard 组件
```kotlin
@Composable
fun ItemCard(
    icon: ImageVector,  // 改为 ImageVector
    title: String,
    subtitle: String,
    extraInfo: String? = null,
    onClick: () -> Unit
)
```

### 图标组件
```kotlin
@Composable
fun AppIcon(
    icon: ImageVector,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface
)
```

## 6. 实现步骤

### Phase 1: 颜色系统更新
- 更新 `Color.kt` 中的颜色值
- 更新 `Theme.kt` 中的颜色方案

### Phase 2: 图标系统实现
- 下载 Phosphor Icons SVG 文件
- 创建 `IconSet.kt` 定义图标常量
- 创建 `AppIcon` composable 组件

### Phase 3: 卡片组件更新
- 更新 `ItemCard.kt` 组件
- 优化卡片样式和布局

### Phase 4: 字体系统更新
- 更新 `Type.kt` 中的字体配置
- 可选：加载自定义字体

### Phase 5: 页面更新
- 更新所有使用 Emoji 的页面
- 替换为新的图标组件

## 7. 验证标准

- [ ] 所有页面使用新的图标系统
- [ ] 颜色系统符合参考图风格
- [ ] 卡片设计符合规范
- [ ] 字体层级清晰
- [ ] 浅色/深色模式正常切换
